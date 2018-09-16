package com.tessModule.tess.convert.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.inject.Inject;
import javax.inject.Named;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tessModule.tess.common.message.EncodingResourceBundleMessageSource;
import com.tessModule.tess.common.properties.LoadProperties;
import com.tessModule.tess.convert.service.ConvertImageInterface;
import com.tessModule.tess.convert.vo.Convert;

public class ConvertImageImpl {
	private final Logger LOGGER = LoggerFactory.getLogger(ConvertImageImpl.class);
	
	@Inject
    @Named("messageSource")
    private EncodingResourceBundleMessageSource messageSource;
	
	public class ConvertImageAtUri implements ConvertImageInterface{
		private final Logger LOGGER = LoggerFactory.getLogger(ConvertImageAtUri.class);
		
		@Inject
	    @Named("messageSource")
	    private EncodingResourceBundleMessageSource messageSource;
		
		public Mat getMat(Convert convert) throws Exception {
			
			File source = null;
	        File dest = null;
	        Mat mat = null;
	        String uri = convert.getInput_file_location();
	        
			try {
				String localFileLocation = LoadProperties.FILE_STORE_PATH + File.separator + convert.getConvert_id() + File.separator + uri.substring(uri.lastIndexOf("/")+1, uri.length());
				
				source = new File(uri);
		        dest = new File(localFileLocation);
		        copyFileUsingChannel(source, dest);
		        
		        if(dest.exists() && dest.isFile()){
		        	mat = Imgcodecs.imread(localFileLocation);
		        	
		        	convert.setFileName(uri.substring(uri.lastIndexOf("/")+1, uri.lastIndexOf(".")));
		        	convert.setFileExtension(uri.substring(uri.lastIndexOf(".")));
		        }else{
		        	String[] statusArgs = {localFileLocation};
		        	LOGGER.error(messageSource.getMessage("convertImage.failToMakeMat", statusArgs));
		        }
			} catch (Exception e) {
				LOGGER.error("");
			} 
			
			return mat;
		}
		
		public void copyFileUsingChannel(File source, File dest) throws IOException {
		    FileChannel sourceChannel = null;
		    FileChannel destChannel = null;
		    try {
				sourceChannel = new FileInputStream(source).getChannel();
				destChannel = new FileOutputStream(dest).getChannel();
				destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
			}finally{
				sourceChannel.close();
				destChannel.close();
			}
		}
		
	}
	
	public class ConvertImageAtRepository implements ConvertImageInterface{
		public Mat getMat(Convert convert) throws Exception {
			return Imgcodecs.imread( convert.getInput_file_location() );
		}
	}
	
	public ConvertImageInterface getInstance(){
		if(LoadProperties.CONVERT_TYPE.equals("uri")){
			return new ConvertImageAtUri();
		}else{
			return new ConvertImageAtRepository();
		}
	}
	
	public Mat readImageToMat(Convert convert) throws Exception {
		ConvertImageInterface convertImage = null;
		
		if(convert != null){
			convertImage = getInstance();
		}else{
			throw new Exception("convert is null");
		}
		
		return convertImage.getMat(convert);
	}

}


