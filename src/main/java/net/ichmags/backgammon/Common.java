/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon;

import org.slf4j.Logger;

/**
 * A helper class to easily share common definitions and functionalities.
 * 
 * @author Anastasios Patrikis
 */
public class Common {

	/**
	 * Definition of the platform dependent <i>new line</i>.
	 * This is useful for debug output. 
	 */
	public static String NEWLINE = System.getProperty("line.separator");
	
	/**
	 * The name of the {@link Logger} that is used for logging game play actions.
	 */
	public static String LOGGER_GAMEPLAY = "GAMEPLAY";
}
