package com.cre.drachenbasis.util;
/* 

 * authors: Aditi Majumder and Sandy Irani
 * University of California, Irvine
 * Last update: 07/11/08
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.*;

public class MyImageWriter
{
	private static final Logger log = LoggerFactory.getLogger(MyImageWriter.class);
	//Write an image on the output file
    public static BufferedImage writeImage(BufferedImage inputImage, int[][][] imageData)
    {
        //BufferedImage inputImage = MyImageReader.readImageIntoBufferedImage( inputFileName );
        if ( inputImage == null )
        {
            log.error(" Input image is null.");
            return null;
        }

        BufferedImage outputImage = new BufferedImage( inputImage.getWidth(), inputImage.getHeight(),
                                                       inputImage.getType() );
        WritableRaster outputRaster, inputRaster;
        inputRaster = inputImage.getRaster();
        outputRaster = inputRaster.createCompatibleWritableRaster();

        int[] pixelData = new int[ outputRaster.getNumBands() ];
        int numbands = outputRaster.getNumBands();
        
        log.debug(" In WriteImage, numbands = " + numbands );

        int height, width;
        height = outputRaster.getHeight();
        width = outputRaster.getWidth();

        for ( int y = 0; y < height; y++ )
            for ( int x = 0; x < width; x++ )
            {
                for ( int band = 0; band < numbands; band++ )
                {
                    pixelData[ band ] = imageData[band][y][x];
                }
                outputRaster.setPixel(x, y, pixelData );
            }
        outputImage.setData( outputRaster );
/*
        File outputFile = new File( outputFileName );
        try
        {
            if ( !ImageIO.write( outputImage, "jpg", outputFile ))
            {
                System.out.println("Could not find image format for output image.");
                return false;
            }
        }
        catch ( Exception e )
        {
            System.out.println("Could not write output file.");
            return false;
        }
*/
        return outputImage;
    }
}
