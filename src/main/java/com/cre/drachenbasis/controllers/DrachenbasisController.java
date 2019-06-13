package com.cre.drachenbasis.controllers;


import com.cre.drachenbasis.models.AG;
import com.cre.drachenbasis.models.Drache;
import com.cre.drachenbasis.models.DracheActivity;
import com.cre.drachenbasis.models.Initialisation;
import com.cre.drachenbasis.models.DracheActivity.Presence;
import com.cre.drachenbasis.models.Room;
import com.cre.drachenbasis.models.WebCam;
import com.cre.drachenbasis.models.RFIDReader;
import com.cre.drachenbasis.repository.DracheRepository;
import com.cre.drachenbasis.repository.InitialisationRepository;
import com.cre.drachenbasis.repository.DracheActivityRepository;
import com.cre.drachenbasis.repository.RoomsRepository;
import com.cre.drachenbasis.repository.WebCamRepository;
import com.cre.drachenbasis.repository.AGRepository;
import com.cre.drachenbasis.repository.RFIDRepository;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;


import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cre.drachenbasis.repository.DistinctDrachenActivity;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class DrachenbasisController {

    @Autowired
    DracheRepository drachenRepository;
    @Autowired
    RoomsRepository roomsRepository;
    @Autowired
    WebCamRepository webCamRepository;
    @Autowired
    DracheActivityRepository drachenActivityRepository;    
	@Autowired
	InitialisationRepository init;
	@Autowired
	AGRepository agRepository;
	@Autowired
	RFIDRepository rfidRepository;	

    private SimpleDateFormat initDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static final Logger log = LoggerFactory.getLogger(DrachenbasisController.class);
    
    @GetMapping("/drachen")
    public List<Drache> getAllDrachen() {
        Sort sortByFirstName = new Sort(Sort.Direction.ASC, "first_name");
        return drachenRepository.findAll(sortByFirstName);
    }

    @GetMapping("/drachen/activity")
    public List<DracheActivity> getAllDrachenActivity() {
        Sort sortByFirstName = new Sort(Sort.Direction.ASC, "first_name");
        return drachenActivityRepository.findAll(sortByFirstName);
    }
    @GetMapping("/drachen/activity/homework")
    public List<DracheActivity> getAllHomeworkDrachenActivity() { 
    	Sort sortByFirstName = new Sort(Sort.Direction.ASC, "first_name");
        return drachenActivityRepository.findAllHomeworkDrachen(sortByFirstName);
    }

    @RequestMapping("/drachen/activity/excused")
    public ResponseEntity<?> excused(@RequestBody DracheActivity dactivity){
    		dactivity.setStatus(Presence.EXCUSED);
        	drachenActivityRepository.save(dactivity);
        	return ResponseEntity.ok().build();
    }

    @GetMapping("/drachen/activity/schoolclass")
    public List<String> getDistinctSchoolclass(){
    		//drachenActivityRepository.findDistinctSchoolclassBySchoolclass();
    	return DistinctDrachenActivity.distinct("drachenactivity","schoolclass");
    }

    @GetMapping("/drachen/activity/arrival")
    public List<String> getDistinctArrival(){
    		//drachenActivityRepository.findDistinctSchoolclassBySchoolclass();
    	return DistinctDrachenActivity.distinct("drachenactivity","arrival");
    }
    
    @GetMapping("/drachen/activity/leaving")
    public List<String> getDistinctLeaving(){
    		//drachenActivityRepository.findDistinctSchoolclassBySchoolclass();
    	return DistinctDrachenActivity.distinct("drachenactivity","leaving");
    }    
    
    @GetMapping("/ag/start")
    public List<String> getDistinctstart(){
    	return DistinctDrachenActivity.distinct("ag","von");
    }

    @GetMapping("/ag/list")
    public List<String> getAGList(){
    	return DistinctDrachenActivity.distinct("ag","ag_name");
    }
    
    @RequestMapping("/drachen/activity/initialise")
    public ResponseEntity<?> initialise(@RequestBody String day){
    	String _method = "initialise";
    	log.info(_method + "|start");
    	log.info(_method + "|" + day);
    	
    	List<DracheActivity> ldActivity = drachenActivityRepository.findAll();
    	log.debug(_method + "List size: " + ldActivity.size());
    	for (DracheActivity da: ldActivity) {
    		da.setStatus(da.getPresenceForDay(day));
    		da.setComment(da.getCommentForDay(day));
    		da.setArrival(da.getArrivalForDay(day));
    		da.setLeaving(da.getLeavingForDay(day));
        	log.debug(_method + "New status: " + da.getStatus());
    		da.setRoom("");
    		da.setLastRoom("");
    		da.setEssenStatus(false);
    		da.setHausaufgabenStatus(false);
    		drachenActivityRepository.save(da);
    	} 	
    	
    	log.info(_method + "|end");
    	return ResponseEntity.ok().build();
    }
           
    @RequestMapping("/drachen/activity/checkin")
    public ResponseEntity<?> checkin(@RequestBody DracheActivity dactivity){
    		dactivity.setStatus(Presence.IN);
    		dactivity.setRoom("Hort");
        	drachenActivityRepository.save(dactivity);
        	return ResponseEntity.ok().build();
    }
    
    @RequestMapping("/drachen/activity/checkout")
    public ResponseEntity<?> checkout(@RequestBody DracheActivity dactivity){
    		dactivity.setStatus(Presence.OUT);
    		dactivity.setRoom("");
        	drachenActivityRepository.save(dactivity);
        	return ResponseEntity.ok().build();
    }

    @RequestMapping("/drachen/activity/setroom")
    public ResponseEntity<?> setroom(@RequestBody DracheActivity dactivity){
    	String _method = "setroom";
    	log.info(_method + "|start");
    	log.info(_method + "| " + dactivity.getFirst_name());
    	log.info(_method + "| " + dactivity.getRoom());    	
    	drachenActivityRepository.save(dactivity);
    	log.info(_method + "|end");
    	return ResponseEntity.ok().build();
    }
    
    @RequestMapping("/drachen/activity/sethomework")
    public ResponseEntity<?> sethomework(@RequestBody DracheActivity dactivity){
    	String _method = "sethomework";
    	log.info(_method + "|start");
    	log.info(_method + "| " + dactivity.getFirst_name());
    	log.info(_method + "| " + dactivity.getHausaufgabenStatus()); 
    	if (dactivity.getHausaufgabenStatus())
    		dactivity.setHausaufgabenStatus(false);
    	else 
    		dactivity.setHausaufgabenStatus(true);
    	drachenActivityRepository.save(dactivity);
    	log.info(_method + "|end");
    	return ResponseEntity.ok().build();
    }

    @RequestMapping("/drachen/activity/setessen")
    public ResponseEntity<?> setessen(@RequestBody DracheActivity dactivity){
    	String _method = "setessen";
    	log.info(_method + "|start");
    	log.info(_method + "| " + dactivity.getFirst_name());
    	log.info(_method + "| " + dactivity.getEssenStatus()); 
    	if (dactivity.getEssenStatus())
    		dactivity.setEssenStatus(false);
    	else
    		dactivity.setEssenStatus(true);
    	drachenActivityRepository.save(dactivity);
    	log.info(_method + "|end");
    	return ResponseEntity.ok().build();
    }
    
    @GetMapping(value="/drachen/{first_name}")
    public ResponseEntity<Drache> getByFirstName(@PathVariable String first_name) {
    	log.info("Search for: " + first_name);
        Drache drache = drachenRepository.findByThePersonsFirstname(first_name);
        if(drache == null) {
        	log.info("Nix gefunden");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info("Drache" + drache.getFirst_name());
            return new ResponseEntity<>(drache, HttpStatus.OK);
        }
    }

    @GetMapping(value="/drachen/activity/{first_name}")
    public ResponseEntity<DracheActivity> getDracheActivityByFirstName(@PathVariable String first_name) {
    	log.info("Search for: " + first_name);
        DracheActivity dracheActivity = drachenActivityRepository.findByThePersonsFirstname(first_name);
        if(dracheActivity == null) {
        	log.info("Nix gefunden");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info("Drache found:" + dracheActivity.getFirst_name());
            return new ResponseEntity<>(dracheActivity, HttpStatus.OK);
        }
    }
    
    @GetMapping("/webcams")
    public List<WebCam> getAllWebCams() {
        Sort sortByName = new Sort(Sort.Direction.ASC, "name");
        return webCamRepository.findAll(sortByName);
    }

    @GetMapping("/rfidreader")
    public List<RFIDReader> getAllRFIDReader() {
        Sort sortByName = new Sort(Sort.Direction.ASC, "hostname");
        return rfidRepository.findAll(sortByName);
    }

    
    @GetMapping(value="/webcams/{room}")
    public ResponseEntity<WebCam> getWebCamByName(@PathVariable("room") String room) {
        WebCam webcam = webCamRepository.findOne(room);
        if(webcam == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(webcam, HttpStatus.OK);
        }
    }
    
 
    
    @GetMapping("/rooms")
    public List<Room> getAllRooms() {
        return roomsRepository.getAllRoomsLatestActivity();
    }

    @GetMapping(value="/rooms/{name}")
    public ResponseEntity<Room> getRoomByName(@PathVariable("name") String room_name) {
        Room room = roomsRepository.findOne(room_name);
        if(room == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
    }
    
    @RequestMapping(value="/webcams")
    public ResponseEntity<?> addWebCam(@RequestBody WebCam webcam){
    	webCamRepository.insert(webcam);
    	return ResponseEntity.ok().build();
    }
    
    @RequestMapping(value="/rfidreader")
    public ResponseEntity<?> addRFIDReader(@RequestBody RFIDReader rfidreader){
    	rfidRepository.insert(rfidreader);
    	return ResponseEntity.ok().build();
    }
    
    @GetMapping(value="/rfidreader/{hostname}")
    public ResponseEntity<RFIDReader> getReaderByhostname(@PathVariable("hostname") String hostname) {
        RFIDReader reader = rfidRepository.findByHostname(hostname);
        if(reader == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(reader, HttpStatus.OK);
        }
    }    
    @RequestMapping(value = "/webcams/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteWebCam(@PathVariable("id") String id) {
        log.info("Fetching & Deleting WebCam with ip {}", id);
        
        WebCam w = webCamRepository.findOne(id);
        if (w == null) {
            log.error("Unable to delete. WebCam with id {} not found.", id);
            return ResponseEntity.notFound().build();
        }
        log.info("About to delete all the activity in room {}", w.getRoom());
        Long l = roomsRepository.deleteRoomByName(w.getRoom());
        if (l >0 ) {
        	log.info("Deleted all the activity in room {}", w.getRoom());
        }
        log.info("About to delete WebCam in room {}", w.getRoom());
        webCamRepository.delete(w);
        log.info("Deleted WebCam in room {}", w.getRoom());
        return ResponseEntity.ok().build();
    }
    
    @RequestMapping(value = "/rfidreader/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteRFIDReader(@PathVariable("id") String id) {
        log.info("Fetching & Deleting RFIDReader with id {}", id);
        
        RFIDReader w = rfidRepository.findOne(id);
        if (w == null) {
            log.error("Unable to delete. rfidReader with id {} not found.", id);
            return ResponseEntity.notFound().build();
        }
        log.info("About to delete all the activity in room {}", w.getRoom());
        Long l = roomsRepository.deleteRoomByName(w.getRoom());
        if (l >0 ) {
        	log.info("Deleted all the activity in room {}", w.getRoom());
        }
        log.info("About to delete rfidReader in room {}", w.getRoom());
        rfidRepository.delete(w);
        log.info("Deleted rfidReader in room {}", w.getRoom());
        return ResponseEntity.ok().build();
    }
   
    ByteArrayOutputStream createQRCodeImg(String text)
    {
    	// Create the ByteMatrix for the QR-Code that encodes the given String
		Hashtable hintMap = new Hashtable();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = null;
		int matrixWidth = 0;
		try {
			byteMatrix = qrCodeWriter.encode(text,
					BarcodeFormat.QR_CODE, 230, 230, hintMap);
		        // Make the BufferedImage that are to hold the QRCode
		        matrixWidth = byteMatrix.getWidth();
		} catch (WriterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedImage image = new BufferedImage(matrixWidth, matrixWidth,
				BufferedImage.TYPE_INT_RGB);
		image.createGraphics();

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);
		// Paint and save the image using the ByteMatrix
		graphics.setColor(Color.BLACK);

		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", baos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//byte[] imageData = baos.toByteArray();
		//ByteArrayResource bars = new ByteArrayResource(imageData);
    	return baos;
    }
    
    @GetMapping(value="/drachen/qrcode")
    @ResponseBody
    public ResponseEntity<InputStreamResource> serveFile() {

    	
	    Document document = new Document(PageSize.A4.rotate());
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
			PdfWriter.getInstance(document, baos);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        document.open();
        float[] columnWidths = {20,10,20,10,20,10};
        PdfPTable table = new PdfPTable(columnWidths);
        table.setWidthPercentage(90);
        Image img = null;
        
        List<Drache> drachen = drachenRepository.findAll();
        for(Drache drache : drachen) {
        
			try {
				img = Image.getInstance(createQRCodeImg(drache.getFirst_name()).toByteArray());
			} catch (BadElementException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        PdfPCell cell = new PdfPCell(img, true);
	        table.addCell(cell);
	        table.addCell(new Phrase(drache.getFirst_name()));
	        log.info(drache.getFirst_name());

        }
        for(int i = 1; i< drachen.size() % 3;i++) {
        	table.addCell(new Phrase(""));
        	table.addCell(new Phrase(""));
        }
        
        try {
			document.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        document.close();
		
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=drachenbsis.pdf");
        
    	//generate the pdf document with QRCodes
        return ResponseEntity
        		.ok()
        		.headers(headers)
        		.body(new InputStreamResource(new ByteArrayInputStream(baos.toByteArray())));
    }

    @GetMapping("/ag")
    public List<AG> getAllAGs() {
        Sort sortByName = new Sort(Sort.Direction.ASC, "first_name");
        return agRepository.findAll(sortByName);
    }

    
    @PostMapping("/ag/upload")
    public String handleAgFileUpload(@RequestParam("file") MultipartFile file){
    	String _method = "handleAgFileUpload";
    	log.info(_method + "|start");
    	InputStream excel;
    	SimpleDateFormat localTimeFormat = new SimpleDateFormat("HH:mm");
    	String cell_value;
    	
		try {
			agRepository.deleteAll();
			log.info(_method + "|Deleted all the ags");
			excel = file.getInputStream();
			Workbook workbook = new XSSFWorkbook(excel);
			//TODO: check if there are more sheets in the workbook and write a warning
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
	
			while(iterator.hasNext()) {
				Row currentRow = iterator.next();
				if (currentRow.getRowNum() > 3) {		
					AG ag = new AG();
					if(currentRow.getCell(0) != null) {
						ag.setFirst_name(currentRow.getCell(0).getStringCellValue());
						log.debug(ag.getFirst_name());
						ag.setSchoolclass(currentRow.getCell(1).getStringCellValue());
						ag.setHort(currentRow.getCell(2).getStringCellValue());					
						ag.setAg_name(currentRow.getCell(3).getStringCellValue());					
	
						//von
						if (DateUtil.isCellDateFormatted(currentRow.getCell(4))){
							if (currentRow.getCell(4) == null || currentRow.getCell(4).getCellType() == Cell.CELL_TYPE_BLANK) {
								ag.setVon("00:00");
							}
							else
							{
							cell_value = localTimeFormat.format(currentRow.getCell(4).getDateCellValue().getTime());
							ag.setVon(cell_value);
	
							}
						}
						//bis
						if (DateUtil.isCellDateFormatted(currentRow.getCell(5))){
							if (currentRow.getCell(3) == null || currentRow.getCell(5).getCellType() == Cell.CELL_TYPE_BLANK) {
								ag.setBis("");
							}
							else
							{
							cell_value = localTimeFormat.format(currentRow.getCell(5).getDateCellValue().getTime());
							ag.setBis(cell_value);
							}
						}
						ag.setDay(currentRow.getCell(6).getStringCellValue());
						agRepository.insert(ag);

					}
				}
			}
			workbook.close();
		} catch (IOException e) {
			log.error(_method+"|Error: " + e.toString());
			
		}
		log.info(_method + "|end");
        return "{\"result\": \"ok\"}";    	
    }
    
    @PostMapping("/drachen/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file){
    	String _method = "handleFileUpload";
    	log.info(_method + "|start");
    	InputStream excel;
    	SimpleDateFormat localTimeFormat = new SimpleDateFormat("HH:mm");
    	String cell_value;
    	Calendar c = Calendar.getInstance();
    	
		try {
			drachenRepository.deleteAll();
			drachenActivityRepository.deleteAll();
			log.info(_method + "|Deleted all the names");
			excel = file.getInputStream();
			Workbook workbook = new XSSFWorkbook(excel);
			//TODO: check if there are more sheets in the workbook and write a warning
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
			
			while(iterator.hasNext()) {
				Row currentRow = iterator.next();
				if (currentRow.getRowNum() > 4) {		
					Drache drache = new Drache();
					DracheActivity dactivity = new DracheActivity();
					dactivity.setStatus(Presence.ABSENT);
					dactivity.setPresenceForDay("Mo", Presence.ABSENT);
					dactivity.setPresenceForDay("Di", Presence.ABSENT);
					dactivity.setPresenceForDay("Mi", Presence.ABSENT);
					dactivity.setPresenceForDay("Do", Presence.ABSENT);
					dactivity.setPresenceForDay("Fr", Presence.ABSENT);

					drache.setFirst_name(currentRow.getCell(0).getStringCellValue());
					dactivity.setFirst_name(currentRow.getCell(0).getStringCellValue());
					
					drache.setSchoolclass(currentRow.getCell(1).getStringCellValue());
					dactivity.setSchoolclass(currentRow.getCell(1).getStringCellValue());

					//mo_von
					if (DateUtil.isCellDateFormatted(currentRow.getCell(2))){
						if (currentRow.getCell(2) == null || currentRow.getCell(2).getCellType() == Cell.CELL_TYPE_BLANK) {
							drache.setMo_von("00:00");
							dactivity.setArrivalForDay("Mo", "00:00");
						}
						else
						{
						cell_value = localTimeFormat.format(currentRow.getCell(2).getDateCellValue().getTime());
						drache.setMo_von(cell_value);
						dactivity.setArrivalForDay("Mo", cell_value);
						}
					}
					//mo_bis
					if (DateUtil.isCellDateFormatted(currentRow.getCell(3))){
						if (currentRow.getCell(3) == null || currentRow.getCell(3).getCellType() == Cell.CELL_TYPE_BLANK) {
							drache.setMo_bis("");
							dactivity.setLeavingForDay("Mo", "00:00");
						}
						else
						{
						cell_value = localTimeFormat.format(currentRow.getCell(3).getDateCellValue().getTime());
						drache.setMo_bis(cell_value);
						dactivity.setLeavingForDay("Mo", cell_value);
						}
					}
					//mo_comment
					drache.setMo_comment(currentRow.getCell(4).getStringCellValue());
					dactivity.setCommentForDay("Mo", drache.getMo_comment());

					
					//di_von
					if (DateUtil.isCellDateFormatted(currentRow.getCell(5))){
						if (currentRow.getCell(5) == null || currentRow.getCell(5).getCellType() == Cell.CELL_TYPE_BLANK){
							drache.setDi_von("");
							dactivity.setArrivalForDay("Di", "00:00");
						}
						else
						{
						cell_value = localTimeFormat.format(currentRow.getCell(5).getDateCellValue().getTime());
						drache.setDi_von(cell_value);
						dactivity.setArrivalForDay("Di", cell_value);
						}
					}
					//di_bis
					if (DateUtil.isCellDateFormatted(currentRow.getCell(6))){
						if (currentRow.getCell(6) == null || currentRow.getCell(6).getCellType() == Cell.CELL_TYPE_BLANK) {
							drache.setDi_bis("");
							dactivity.setLeavingForDay("Di", "00:00");
						}
						else
						{
						cell_value = localTimeFormat.format(currentRow.getCell(6).getDateCellValue().getTime());
						drache.setDi_bis(cell_value);
						dactivity.setLeavingForDay("Di", cell_value);
						}
					}
					//di_comment
					drache.setDi_comment(currentRow.getCell(7).getStringCellValue());
					dactivity.setCommentForDay("Di", drache.getDi_comment());

					
					//mi_von
					if (DateUtil.isCellDateFormatted(currentRow.getCell(8))){
						if (currentRow.getCell(8) == null || currentRow.getCell(8).getCellType() == Cell.CELL_TYPE_BLANK) {
							drache.setMi_von("");
							dactivity.setArrivalForDay("Mi", "00:00");
						}
						else
						{
						cell_value = localTimeFormat.format(currentRow.getCell(8).getDateCellValue().getTime());
						drache.setMi_von(cell_value);
						dactivity.setArrivalForDay("Mi", cell_value);
						}
					}
					//mi_bis
					if (DateUtil.isCellDateFormatted(currentRow.getCell(9))){
						if (currentRow.getCell(9) == null || currentRow.getCell(9).getCellType() == Cell.CELL_TYPE_BLANK) {
							drache.setMi_bis("");
							dactivity.setLeavingForDay("Mi", "00:00");
						}
						else
						{
						cell_value = localTimeFormat.format(currentRow.getCell(9).getDateCellValue().getTime());
						drache.setMi_bis(cell_value);
						dactivity.setLeavingForDay("Mi", cell_value);
						}
					}				
					//mi_comment
					drache.setMi_comment(currentRow.getCell(10).getStringCellValue());
					dactivity.setCommentForDay("Mi", drache.getMi_comment());

					
					//do_von
					if (DateUtil.isCellDateFormatted(currentRow.getCell(11))){
						if (currentRow.getCell(11) == null || currentRow.getCell(11).getCellType() == Cell.CELL_TYPE_BLANK) {
							drache.setDo_von("");
							dactivity.setArrivalForDay("Do", "00:00");
						}
						else
						{
						cell_value = localTimeFormat.format(currentRow.getCell(11).getDateCellValue().getTime());
						drache.setDo_von(cell_value);
						dactivity.setArrivalForDay("Do", cell_value);
						}
					}
					//do_bis
					if (DateUtil.isCellDateFormatted(currentRow.getCell(12))){
						if (currentRow.getCell(12) == null || currentRow.getCell(12).getCellType() == Cell.CELL_TYPE_BLANK) {
							drache.setDo_bis("");
							dactivity.setLeavingForDay("Do", "00:00");
						}
						else
						{
						cell_value = localTimeFormat.format(currentRow.getCell(12).getDateCellValue().getTime());
						drache.setDo_bis(cell_value);
						dactivity.setLeavingForDay("Do", cell_value);
						}
					}
					//do_comment
					drache.setDo_comment(currentRow.getCell(13).getStringCellValue());
					dactivity.setCommentForDay("Do", drache.getDo_comment());
					
					//fr_von
					if (DateUtil.isCellDateFormatted(currentRow.getCell(14))){
						if (currentRow.getCell(14) == null || currentRow.getCell(14).getCellType() == Cell.CELL_TYPE_BLANK) {
							drache.setFr_von("");
							dactivity.setArrivalForDay("Fr", "00:00");
						}
						else
						{
						cell_value = localTimeFormat.format(currentRow.getCell(14).getDateCellValue().getTime());
						drache.setFr_von(cell_value);
						dactivity.setArrivalForDay("Fr", cell_value);
						}
					}
					//fr_bis
					if (DateUtil.isCellDateFormatted(currentRow.getCell(15))){
						if (currentRow.getCell(15) == null || currentRow.getCell(15).getCellType() == Cell.CELL_TYPE_BLANK) {
							drache.setFr_bis("");
							dactivity.setLeavingForDay("Fr", "00:00");
						}
						else
						{
						cell_value = localTimeFormat.format(currentRow.getCell(15).getDateCellValue().getTime());
						drache.setFr_bis(cell_value);
						dactivity.setLeavingForDay("Fr", cell_value);
						}
					}
					//fr_comment
					drache.setFr_comment(currentRow.getCell(16).getStringCellValue());
					dactivity.setCommentForDay("Fr", drache.getFr_comment());

					//hausaufgaben
					if (drache.getSchoolclass().startsWith("3") ||drache.getSchoolclass().startsWith("4") ) {
						drache.setHausaufgaben(true);
						dactivity.setHausaufgaben(true);
					}
					else
					{
					    drache.setHausaufgaben(false);
					    dactivity.setHausaufgaben(false);
					}

					/*
					while( cellIterator.hasNext()) {
						Cell currentCell = cellIterator.next();
						if (currentCell.getCellTypeEnum() == CellType.STRING ) {
							name = currentCell.getStringCellValue();
							drache.setFirst_name(name);
						}
						if (currentCell.getCellTypeEnum() == CellType. ) {
							name = currentCell.getStringCellValue();
							drache.setFirst_name(name);
						}
						
					}*/
					log.info(_method + "| " + drache.toString());
					
					if (!drache.getFr_von().isEmpty() && !drache.getFr_bis().isEmpty()) 
					{	
						dactivity.setStatus(Presence.PRESENT);
						dactivity.setPresenceForDay("Fr", Presence.PRESENT);
					}
					if (!drache.getDo_von().isEmpty() && !drache.getDo_bis().isEmpty()) 
					{
						dactivity.setStatus(Presence.PRESENT);				
						dactivity.setPresenceForDay("Do", Presence.PRESENT);						
					}
					if (!drache.getMi_von().isEmpty() && !drache.getMi_bis().isEmpty()) 
					{
						dactivity.setStatus(Presence.PRESENT);
						dactivity.setPresenceForDay("Mi", Presence.PRESENT);
					}
					if (!drache.getDi_von().isEmpty() && !drache.getDi_bis().isEmpty()) 
					{
						dactivity.setStatus(Presence.PRESENT);
						dactivity.setPresenceForDay("Di", Presence.PRESENT);
					}
					if (!drache.getMo_von().isEmpty() && !drache.getMo_bis().isEmpty()) 
					{
						dactivity.setStatus(Presence.PRESENT);
						dactivity.setPresenceForDay("Mo", Presence.PRESENT);
					}
					dactivity.setEssenStatus(false);
					dactivity.setHausaufgabenStatus(false);

					drachenRepository.insert(drache);
					drachenActivityRepository.insert(dactivity);
					
					Initialisation initStatus = new Initialisation();
			    	initStatus.setTable("drachenactivity");
			    	initStatus.setSnapshot(initDateFormat.format(new Date()));
			    	init.save(initStatus);

				}
			}
			workbook.close();
		} catch (IOException e) {
			log.error(_method+"|Error: " + e.toString());
			
		}
		log.info(_method + "|end");
        return "{\"result\": \"ok\"}";
    }    
    /*public String handleFileUpload(@RequestParam("file") MultipartFile file){
    	String _method = "handleFileUpload";
    	log.info(_method + "|start");
    	InputStream excel;
    	String name;
		try {
			drachenRepository.deleteAll();
			log.info(_method + "|Deleted all the names");
			excel = file.getInputStream();
			Workbook workbook = new XSSFWorkbook(excel);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
			
			while(iterator.hasNext()) {
				Row currentRow = iterator.next();
				Iterator<Cell> cellIterator = currentRow.iterator();
				while( cellIterator.hasNext()) {
					Cell currentCell = cellIterator.next();
					if (currentCell.getCellTypeEnum() == CellType.STRING ) {
						name = currentCell.getStringCellValue();
						if (!name.contains("Name")) {
						 log.info(_method + "| " + name);
						 Drache drache = new Drache();
						 drache.setFirst_name(name);
						 drachenRepository.insert(drache);
						}
					}
				}
			}
			workbook.close();
		} catch (IOException e) {
			log.error(_method+"|Error: " + e.toString());
			
		}
		log.info(_method + "|end");
        return "{\"result\": \"ok\"}";
    }
    */
}
