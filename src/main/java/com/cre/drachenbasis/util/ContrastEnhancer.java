package com.cre.drachenbasis.util;
/* 
 * authors: Aditi Majumder and Sandy Irani
 * University of California, Irvine
 * Last update: 07/11/08
 */

import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContrastEnhancer
{
	private static final Logger log = LoggerFactory.getLogger(ContrastEnhancer.class);

    /* This is the primary method call.
     * Given a two dimensional array, max and min values, as well as delta,
     * this will return a new image with contrast enhanced.
     */
    public static double[][] enhance(double[][] imageData, boolean[][] criticalMap,
                                    double[][] maxValueMap, double minValue, double delta )
    {
        Stack hillockStack = new Stack();

        // create a new hillock consisting of entire image and push it onto the stack
        hillockStack.push( new Hillock( imageData, criticalMap, maxValueMap, delta ) );

        // keep processing hillocks until there are non left
        while ( !hillockStack.isEmpty() )
        {
        	//pop a hillock
            Hillock nextHillock = (Hillock)hillockStack.pop();
            
            Hillock[] newHillocks = nextHillock.processHillock();
            for ( int i = 0; i < newHillocks.length; i++ )
            	
            	//push a hillock 
                if ( newHillocks[i] != null )
                    hillockStack.push( newHillocks[i] );
            log.debug("Number of hillocks is " + hillockStack.size());
        }
        log.debug("END Contrast Enhancer");
        return imageData;
    }
}
