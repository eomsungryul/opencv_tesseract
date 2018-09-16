/**
 * 
 */
package com.tessModule.tess.common.properties;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadProperties {
    
	private static final Logger LOGGER = LoggerFactory.getLogger(LoadProperties.class);
    private static final String STORE_PATH_KEY = "imgPath";
    private static final String DLL_PATH_KEY = "dllPath";
    private static final String LOCALE_KEY = "locale";
    private static final String API_KEY = "apikey";
    private static final String APP_STATE_KEY = "appState";
    private static final String CONVERT_TYPE_KEY = "convertType";
    
    
    public static final String API_KEY_VALUE;
    public static final String FILE_STORE_PATH;
    public static final String DLL_PATH;
    public static final String LOCALE;
    public static final String APP_STATE;
    public static final String CONVERT_TYPE;
    
    static {
    	Properties props = new Properties();
			try {
				props.load(LoadProperties.class.getClassLoader().getResourceAsStream("/config/config.properties"));
			} catch (IOException e) {
				LOGGER.error(e.toString());
			}
			
			APP_STATE = props.getProperty(APP_STATE_KEY);
			CONVERT_TYPE = props.getProperty(CONVERT_TYPE_KEY);
			LOCALE = props.getProperty(LOCALE_KEY);
			DLL_PATH = props.getProperty(DLL_PATH_KEY);
			FILE_STORE_PATH = props.getProperty(STORE_PATH_KEY);
			API_KEY_VALUE = props.getProperty(API_KEY);
    }
}
