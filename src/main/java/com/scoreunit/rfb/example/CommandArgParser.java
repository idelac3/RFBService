package com.scoreunit.rfb.example;

import java.util.Properties;

/**
 * This is helper class to parse command line arguments
 *  give to application.
 *  
 * Arguments might come in several forms, like:
 * <pre>
 *  -p [value]
 *  --port [value]
 *  -p[value]
 *  --port=[value]
 * </pre>
 * 
 * This class will try to support them all if possible.
 * 
 * @author igor.delac@gmail.com
 *
 */
public class CommandArgParser {

	/**
	 * Key, value pair of strings.
	 * 
	 * Key is argument name, and value is argument value.
	 * 
	 * If key is just an flag, as in example:<br>
	 * <i>  -d for debugging </i><br>
	 *  then value should be string <i>true</i>
	 *  to indicate that argument is given on command line.  
	 */
	final Properties map;

	/**
	 * Construct new command arg. parser.
	 *
	 * @param args	list of program arguments
	 */
	public CommandArgParser(final String[] args) {
	
		this.map = new Properties();
		
		this.parse(args);
	}
	
	/**
	 * Parse arguments.
	 * 
	 * @param args	-	list of arguments from command line
	 */
	public void parse(final String[] args) {

		this.map.clear();
		
		String key = null, value = null;
		
		for (final String arg : args) {
		
			if (key == null && arg.startsWith("--")) {
				
				key = arg.substring(2);
				value = null;
				
				// Support for something like: --port=1234
				int pos = key.indexOf('='); 
				
				if (pos > 0) {
					
					value = key.substring(pos + 1);
					key = key.substring(0, pos);
					
					// Save key, value in map.
					this.map.setProperty(key, value);
					key = null;
					value = null;
				}
			}
			else if (key == null && arg.startsWith("-")) {
				
				key = arg.substring(1);
				value = null;
				
				// Support for something like: -p=1234
				int pos = key.indexOf('='); 
				
				if (pos > 0) {
					
					value = key.substring(pos + 1);
					key = key.substring(0, pos);
				}
				else if (key.length() > 1) {
					// Support for something like: -p1234
					
					value = key.substring(1);
					key = key.substring(0, 1);
				}
				
				if (key != null && value != null) {
					
					// Save key, value in map.
					this.map.setProperty(key, value);
					key = null;
					value = null;
				}
			}
			else if (key != null && value == null) {
				
				if (arg.startsWith("--")) {
					
					value = "true";
										
					// Save key, value in map.
					this.map.setProperty(key, value);
					
					key = arg.substring(2);
					value = null;					
				}
				else if (arg.startsWith("-")) {
					
					value = "true";
										
					// Save key, value in map.
					this.map.setProperty(key, value);
					
					key = arg.substring(1);
					value = null;					
				}
				else {
				
					value = arg;
										
					// Save key, value in map.
					this.map.setProperty(key, value);
					
					key = null;
					value = null;
				}
			}			
			else {
				
				System.err.println(
						String.format("Argument '%s' not valid.", arg));
			}
		}
		
		if (key != null && value == null) {
			
			this.map.setProperty(key, "true");
		}
		else if (key != null && value != null) {
			
			this.map.setProperty(key, value);
		}
	}
	
	/**
	 * Read key, value map for argument names and values.
	 *  
	 * @return	{@link Properties} object
	 */
	public Properties getArguments() {
		
		return this.map;
	}
}
