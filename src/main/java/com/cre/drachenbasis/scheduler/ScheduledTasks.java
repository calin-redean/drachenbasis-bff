package com.cre.drachenbasis.scheduler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.cre.drachenbasis.repository.RoomsRepository;
import com.cre.drachenbasis.repository.WebCamRepository;
import com.cre.drachenbasis.repository.DracheActivityRepository;
import com.cre.drachenbasis.repository.InitialisationRepository;
import com.cre.drachenbasis.models.Drache;
import com.cre.drachenbasis.models.DracheActivity;
import com.cre.drachenbasis.models.Initialisation;
import com.cre.drachenbasis.models.Room;
import com.cre.drachenbasis.models.WebCam;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.MultipleBarcodeReader;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

@Component
public class ScheduledTasks {

	@Autowired
    RoomsRepository roomsRepo;
	@Autowired
	WebCamRepository webCamRepo;
	@Autowired
	DracheActivityRepository dactivity;
	
	@Autowired
	InitialisationRepository init;
		
	@Value("${drachenbasis.save.snapshot}")
	private boolean save_snapshot;
	
	@Value("${drachenbasis.save.snapshot.path}")
	private String snapshot_path;
	
	@Value("${drachenbasis.test.path}")
	private String test_path;
	
	@Value("${drachenbasis.test.day}")	
	private String test_day;

	@Value("${drachenbasis.test.enable}")
	private boolean test_enabled;
	
	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private SimpleDateFormat initDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private SimpleDateFormat snapshotFormat = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");

    public void insertDragonsInRoom(String room_name,Set<String> qrContent) {
    	String _method = "insertDragonsInRooms";
    	log.debug(_method + "|start");
    	
    	Room r = new Room();
    	r.setName(room_name);
    	r.setDrachen(qrContent);
    	r.setSnapshot(dateFormat.format(new Date()));
    	DracheActivity activity;
    	String lastroom_name;
    	 	
    	for (String name : qrContent) {
    		log.debug(_method + "| search for: " + name);
    		activity = dactivity.findByThePersonsFirstname(name);
    		if (activity != null) {
    			log.debug(_method + "| insert into room: " + room_name);
    			lastroom_name=activity.getRoom();
    			if (!room_name.equals(lastroom_name))
    				activity.setLastRoom(lastroom_name);
    			activity.setRoom(room_name);
    			dactivity.save(activity);
    		}
    	}
    	
    	Room lastRoomStatus = roomsRepo.findRoomByName(room_name);
    	if (lastRoomStatus != null) {
	    	Set<String> lastDrachenList = lastRoomStatus.getDrachen();
	    	log.debug(_method + "| last room status: "+ room_name + " " + lastDrachenList.toString() );
	
	    	lastDrachenList.removeAll(qrContent);
	    	log.debug(_method + "| no longer in room: "+ room_name + " "+ lastDrachenList.toString());
	    	
	    	for (String name : lastDrachenList) {
	    		log.debug(_method + "| search for: " + name);
	    		activity = dactivity.findByThePersonsFirstname(name);
	    		if (activity != null) {
	    			log.debug(_method + "| remove from room: " + room_name);
	    			activity.setLastRoom(room_name);
	    			activity.setRoom("Hort");
	    			dactivity.save(activity);
	    		}
	    	}
    	}
    	roomsRepo.save(r);
    	log.debug(_method + "|end");
    }
    
    public Set<String> processSnapshot(InputStream is, String room) {
    	String _method = "processSnapshot";
    	log.info(_method +"|start");
    	long smillis = System.currentTimeMillis();
		BufferedImage image=null;//, rawImage = null;
		//boolean noqrcode = false;
		Set<String> barcodeContents = new HashSet<>();
		try {
			image = ImageIO.read(is);
			/*
			image = Contrast.startEnhancement(rawImage, 2);
			if (image != null) {
				log.info(_method +"|contrast enhanced successfully");
				File outputFile = new File( "/tmp/test.jpg" );
		        try
		        {
		            if ( !ImageIO.write( image, "jpg", outputFile ))
		            {
		                System.out.println("Could not find image format for output image.");
		            }
		        }
		        catch ( Exception e )
		        {
		            System.out.println("Could not write output file.");
		        }
			}
			File file = new File("/tmp/test.jpg");
            image = ImageIO.read(file);
             */            
			LuminanceSource source = new BufferedImageLuminanceSource(image);
	    
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
	
			MultipleBarcodeReader reader = new QRCodeMultiReader();
			
			HashMap<DecodeHintType,BarcodeFormat> hints = new HashMap<DecodeHintType,BarcodeFormat>();
		    hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
		    hints.put(DecodeHintType.TRY_HARDER, BarcodeFormat.QR_CODE);
		    
			Result[] results = null;
			results = reader.decodeMultiple(bitmap,hints);
        	
	        for (Result result : results) {
	        	barcodeContents.add(result.getText());
	        }
	        log.info(_method + "| qrCodes:" + barcodeContents.size());
        	log.info(_method + "| " + barcodeContents.toString());
	        
	        if (save_snapshot) {
	        	String filePath = snapshot_path +"/" + room + "_" + "qr_"+ results.length + "_" + snapshotFormat.format(new Date()) + ".jpg";
	        	log.debug(_method + "|Save image to: " + filePath);
	        	File fImage = new File(filePath);
	        	File homeImage = new File("/home");
	        	Long usableSpace = homeImage.getUsableSpace();
	        	log.info(_method + "| usable space (MB): " + usableSpace/1000000);
	        	if (usableSpace/1000000 > 20) {
	        		ImageIO.write(image, "JPG",fImage);
	        		log.debug(_method + "|File written successfully!");
	        	} else
	        	{
	        		log.error(_method + "|not enough usable space (MB):"+ usableSpace/1000000);
	        	}
	        	
	        }
		} catch (NotFoundException e) {
	    	log.error(_method + "|error: " + e.toString());
		}
		catch (IOException e) {
        	log.error(_method + "|error: " + e.toString());
        }
		catch (Exception e) {
        	log.error(_method + "|error: " + e.toString());
        	e.printStackTrace();
        }
		long emillis = System.currentTimeMillis();
        log.info(_method + "|end|" + String.valueOf(emillis-smillis)); 
    	return barcodeContents;
    }

    //@Scheduled(fixedRate = 30000)
    //public void houseKeeping() {
    	//roomsRepo add a method to delete the entries older than x days
    	//roomsRepo.deleteAll();
    //}
    
    public void testSnapshot() {
    	String _method = "testSnapshot";
    	log.info(_method + "|start|");
    	File[] files = new File(test_path).listFiles();
    	for (File file : files) {
            if (file.isDirectory()) {
                log.info("Not digging into folder: " + file.getName());
            } else {
            	String room = file.getName().substring(0, file.getName().indexOf("."));
            	log.info("File: " + room);
            	InputStream is;
				try {
					is = new FileInputStream(file);
					Set<String> qrCodeContent = processSnapshot(is,room);
					insertDragonsInRoom(room,qrCodeContent);
					is.close();
				} catch (IOException e) {
					log.error("Unable to read the file:" + file.getName());
					log.error(e.toString());
				}
            }
        }
    	log.info(_method + "|end|");
    }
    
    @Scheduled(fixedDelay = 30000)
    public void snapshot() {
    	if (test_enabled) {
    		testSnapshot();
    	} else {
    		takeSnapshot();
    	}
    }

    @Scheduled(cron="${drachenbasis.init.cron}")
    public void initialize() {
    	String _method = "initialise";
    	log.info(_method + "|start");
    	
    	//check if the initialization was performed today
    	String currentDate = initDateFormat.format(new Date());
    	log.info(_method + "| current date: " + currentDate);
    	Initialisation initStatus = init.findTableBySnapshot(currentDate);
    	if (initStatus != null) {
    		log.info(_method + "| snapshot date: " + initStatus.getSnapshot());
    		if (!Objects.equals(currentDate,initStatus.getSnapshot())) {	
    			initialiseActivity(currentDate);
    		}
    	} else {
    		initialiseActivity(currentDate);
    	}
    	
    	log.info(_method + "|end");
    }
    
    public void initialiseActivity(String currentDate) {
    	String _method = "initialiseActivity";
    	log.info(_method + "|start: " + currentDate);
    	
    	Calendar c = Calendar.getInstance();
    	int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
    	String day = "Mo";
    	//find day of the week
    	switch(dayOfWeek) {
    	case 2:
    		day = "Mo";
    		break;
    	case 3:
    		day = "Di";
    		break;
    	case 4:
    		day = "Mi";
    		break;
    	case 5:
    		day = "Do";
    		break;
    	case 6:
    		day = "Fr";
    		break;    		
    	}

    	if (!test_day.isEmpty())
    		day = test_day;
    	
    	log.info(_method + "|" + day); 
    	
    	List<DracheActivity> ldActivity = dactivity.findAll();
    	log.debug(_method + "List size: " + ldActivity.size());
    	for (DracheActivity da: ldActivity) {
    		da.setStatus(da.getPresenceForDay(day));
    		da.setComment(da.getCommentForDay(day));
    		da.setArrival(da.getArrivalForDay(day));
    		da.setLeaving(da.getLeavingForDay(day));
        	log.debug(_method + "New status: " + da.getStatus());
    		da.setRoom("");
    		da.setEssenStatus(false);
    		da.setHausaufgabenStatus(false);
    		dactivity.save(da);
    	}
    	
    	Initialisation initStatus = new Initialisation();
    	initStatus.setTable("drachenactivity");
    	initStatus.setSnapshot(currentDate);
    	init.save(initStatus);

    	log.info(_method + "|end");    	
    }

    public void takeSnapshot() {
    	String _method = "takeSnapshot";
    	log.info(_method + "|start");
    	long smillis = System.currentTimeMillis();
		int timeout = 5;
		InputStream is = null;
        try {
        	List<WebCam> webCams = webCamRepo.findAllActive();
        	
        	for(WebCam webcam : webCams) {
        		log.info(_method + "|room " + webcam.getRoom() );
        	
        		RequestConfig config = RequestConfig.custom()
        				.setConnectTimeout(timeout * 1000)
        				.setConnectionRequestTimeout(timeout * 1000)
        				.setSocketTimeout(timeout * 1000).build();
        	
        		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        		HttpGet request = new HttpGet(
        				"http://"+webcam.getUsername() +":"+ webcam.getPass()+"@"+webcam.getIp()+"/snapshot.jpg");

        		try {
        		HttpResponse response = client.execute(request);
        		HttpEntity entity = response.getEntity();
 
        		int responseCode = response.getStatusLine().getStatusCode();
        		
        		log.info(_method + "|rc:" + responseCode + "|Request Url: " + request.getURI());
        		
        		if (responseCode == 200) {
        			is = entity.getContent();
        			Set<String> qrCodeContent= processSnapshot(is,webcam.getRoom());
        			insertDragonsInRoom(webcam.getRoom(),qrCodeContent);
        			webcam.setErrorcount(0);
        			is.close();
        		}
        		
            	client.close();
                } catch (ClientProtocolException e) {
                    log.error(_method + "|error: " + e.toString());
                    webcam.setErrorcount(webcam.getErrorcount()+1);
        	    } catch (IOException e) {
        	        log.error(_method + "|error: " + e.toString());
                    webcam.setErrorcount(webcam.getErrorcount()+1);
        	    }
            	webCamRepo.save(webcam);
        	}
        } catch (UnsupportedOperationException e) {
            log.error(_method + "|error: " + e.toString());
	    }
        long emillis = System.currentTimeMillis();
        log.info(_method + "|end|" + String.valueOf(emillis-smillis));        
    }
}
