package com.tessModule.tess.cmmn.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.tessModule.tess.common.properties.LoadProperties;

@Component("fileMngUtil")
public class FileMngUtil {

	public static final int BUFF_SIZE = 2048;

	public static final String NONE = "none"; //default Type;
	public static final String IMAGE = "image"; //image Type;

	private static final String[] NONE_FILES = {"doc","docx","xls","xlsx","ppt","pptx","jpg","gif","png","bmp","jpeg","txt","zip","hwp","pdf","mdb"};
	private static final String[] IMAGE_FILES = {"jpg","gif","png","bmp","jpeg"};

	
//	@Resource(name = "propertiesService")
//	protected EgovPropertyService propertyService;

	private static final Logger LOGGER = LoggerFactory.getLogger(FileMngUtil.class);

	/**
	 * 첨부파일에 대한 목록 정보를 취득한다.
	 *
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public String parseFileInf(Map<String, List<MultipartFile>> files, String KeyStr, String storePath) throws Exception {
//		int fileKey = fileKeyParam;

		String storePathString = LoadProperties.FILE_STORE_PATH;
		String subPath = "";
		String atchFileIdString = "";

		if ("".equals(storePath) || storePath == null) {
//			storePathString = propertyService.getString("Globals.fileStorePath");
			subPath = File.separator + StringUtil.getTimeStamp().substring(0,6);
		} else {
			storePathString = storePath;
			subPath = File.separator + StringUtil.getTimeStamp().substring(0,6);
		}

		File saveFolder = new File(storePathString+subPath);

		if (!saveFolder.exists() || saveFolder.isFile()) {
			saveFolder.mkdirs();
		}

		Iterator<Entry<String, List<MultipartFile>>> itr = files.entrySet().iterator();
		List<MultipartFile> fileList;
		String filePath = "";
		List<HashMap<String, String>> result  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> fMap;

		int cnt = 0;
		while (itr.hasNext()) {
			Entry<String, List<MultipartFile>> entry = itr.next();

			fileList = entry.getValue();

			for(MultipartFile file : fileList){
				String orginFileName = file.getOriginalFilename();

				//--------------------------------------
				// 원 파일명이 없는 경우 처리
				// (첨부가 되지 않은 input file type)
				//--------------------------------------
				if ("".equals(orginFileName)) continue;
				////------------------------------------

				int index = orginFileName.lastIndexOf(".");
				//String fileName = orginFileName.substring(0, index);
				String fileExt = orginFileName.substring(index + 1);
				String newName = KeyStr + StringUtil.getTimeStamp() + cnt++;
				long _size = file.getSize();

				if (fileExt.endsWith("jsp") || fileExt.endsWith("js") || fileExt.endsWith("html") || fileExt.endsWith("htm")) {
					LOGGER.debug("파일 타입을 확인하세요.");
					continue;
				}

				if (!"".equals(orginFileName)) {
					filePath = storePathString + subPath + File.separator + newName+"."+fileExt;
					file.transferTo(new File(filePath));
				}
				
				
//				fMap = new HashMap<String, String>();


//				fMap.put("fileKey",  entry.getKey());
//				fMap.put("fileType", fileExt);
//				fMap.put("filePath", filePath);
//				fMap.put("fileName", orginFileName);
//				fMap.put("fileSize", Long.toString(_size));

				//writeFile(file, newName, storePathString);
//				result.add(fMap);
			}
		}

		return filePath;
	}
	
	/**
	 * 첨부파일에 대한 목록 정보를 취득한다.
	 *
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public List<HashMap<String, String>> parseFileInf_bk(Map<String, List<MultipartFile>> files, String KeyStr, String storePath) throws Exception {
//		int fileKey = fileKeyParam;
		
		String storePathString = LoadProperties.FILE_STORE_PATH;
		String subPath = "";
		String atchFileIdString = "";
		
		if ("".equals(storePath) || storePath == null) {
//			storePathString = propertyService.getString("Globals.fileStorePath");
			subPath = File.separator + StringUtil.getTimeStamp().substring(0,6);
		} else {
			storePathString = storePath;
			subPath = File.separator + StringUtil.getTimeStamp().substring(0,6);
		}
		
		File saveFolder = new File(storePathString+subPath);
		
		if (!saveFolder.exists() || saveFolder.isFile()) {
			saveFolder.mkdirs();
		}
		
		Iterator<Entry<String, List<MultipartFile>>> itr = files.entrySet().iterator();
		List<MultipartFile> fileList;
		String filePath = "";
		List<HashMap<String, String>> result  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> fMap;
		
		int cnt = 0;
		while (itr.hasNext()) {
			Entry<String, List<MultipartFile>> entry = itr.next();
			
			fileList = entry.getValue();
			
			for(MultipartFile file : fileList){
				String orginFileName = file.getOriginalFilename();
				
				//--------------------------------------
				// 원 파일명이 없는 경우 처리
				// (첨부가 되지 않은 input file type)
				//--------------------------------------
				if ("".equals(orginFileName)) continue;
				////------------------------------------
				
				int index = orginFileName.lastIndexOf(".");
				//String fileName = orginFileName.substring(0, index);
				String fileExt = orginFileName.substring(index + 1);
				String newName = KeyStr + StringUtil.getTimeStamp() + cnt++;
				long _size = file.getSize();
				
				if (fileExt.endsWith("jsp") || fileExt.endsWith("js") || fileExt.endsWith("html") || fileExt.endsWith("htm")) {
					LOGGER.debug("파일 타입을 확인하세요.");
					continue;
				}
				
				if (!"".equals(orginFileName)) {
					filePath = storePathString + subPath + File.separator + newName;
					file.transferTo(new File(filePath));
				}
				
				
//				fMap = new HashMap<String, String>();
				
				
//				fMap.put("fileKey",  entry.getKey());
//				fMap.put("fileType", fileExt);
//				fMap.put("filePath", filePath);
//				fMap.put("fileName", orginFileName);
//				fMap.put("fileSize", Long.toString(_size));
				
				//writeFile(file, newName, storePathString);
//				result.add(fMap);
			}
		}
		
		return result;
	}

	/**
	 * 첨부파일을 서버에 저장한다.
	 *
	 * @param file
	 * @param newName
	 * @param stordFilePath
	 * @throws Exception
	 */
	protected void writeUploadedFile(MultipartFile file, String newName, String stordFilePath) throws Exception {
	InputStream stream = null;
	OutputStream bos = null;
	String stordFilePathReal = (stordFilePath==null?"":stordFilePath).replaceAll("..","");
	try {
		stream = file.getInputStream();
		File cFile = new File(stordFilePathReal);

		if (!cFile.isDirectory()) {
		boolean _flag = cFile.mkdir();
		if (!_flag) {
			throw new IOException("Directory creation Failed ");
		}
		}

		bos = new FileOutputStream(stordFilePathReal + File.separator + newName);

		int bytesRead = 0;
		byte[] buffer = new byte[BUFF_SIZE];

		while ((bytesRead = stream.read(buffer, 0, BUFF_SIZE)) != -1) {
		bos.write(buffer, 0, bytesRead);
		}
	} catch (FileNotFoundException fnfe) {
		LOGGER.debug("fnfe: {}", fnfe);
	} catch (IOException ioe) {
		LOGGER.debug("ioe: {}", ioe);
	} catch (Exception e) {
		LOGGER.debug("e: {}", e);
	} finally {
		if (bos != null) {
		try {
			bos.close();
		} catch (IOException ioe) {
			LOGGER.debug("IOE: {}", ioe.getMessage());
		} catch (Exception ignore) {
			LOGGER.debug("IGNORED: {}", ignore.getMessage());
		}
		}
		if (stream != null) {
		try {
			stream.close();
		} catch (IOException ioe) {
			LOGGER.debug("IOE: {}", ioe.getMessage());
		} catch (Exception ignore) {
			LOGGER.debug("IGNORED: {}", ignore.getMessage());
		}
		}
	}
	}

	/**
	 * 서버의 파일을 다운로드한다.
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public static void downFile(HttpServletRequest request, HttpServletResponse response) throws Exception {

	String downFileName = StringUtil.isNullToString(request.getAttribute("downFile")).replaceAll("..","");
	String orgFileName = StringUtil.isNullToString(request.getAttribute("orgFileName")).replaceAll("..","");

	/*if ((String)request.getAttribute("downFile") == null) {
		downFileName = "";
	} else {
		downFileName = EgovStringUtil.isNullToString(request.getAttribute("downFile"));
	}*/

	/*if ((String)request.getAttribute("orgFileName") == null) {
		orgFileName = "";
	} else {
		orgFileName = (String)request.getAttribute("orginFile");
	}*/

	File file = new File(downFileName);

	if (!file.exists()) {
		throw new FileNotFoundException(downFileName);
	}

	if (!file.isFile()) {
		throw new FileNotFoundException(downFileName);
	}

	byte[] b = new byte[BUFF_SIZE]; //buffer size 2K.
	String fName = (new String(orgFileName.getBytes(), "UTF-8")).replaceAll("\r\n","");
	response.setContentType("application/x-msdownload");
	response.setHeader("Content-Disposition:", "attachment; filename=" + fName);
	response.setHeader("Content-Transfer-Encoding", "binary");
	response.setHeader("Pragma", "no-cache");
	response.setHeader("Expires", "0");

	BufferedInputStream fin = null;
	BufferedOutputStream outs = null;

	try {
		fin = new BufferedInputStream(new FileInputStream(file));
		outs = new BufferedOutputStream(response.getOutputStream());
		int read = 0;

		while ((read = fin.read(b)) != -1) {
			outs.write(b, 0, read);
		}
	} finally {
		if (outs != null) {
			try {
				outs.close();
			} catch (IOException ioe) {
				LOGGER.debug("IOE: {}", ioe.getMessage());
			} catch (Exception ignore) {
				LOGGER.debug("IGNORED: {}", ignore.getMessage());
			}
			}
			if (fin != null) {
			try {
				fin.close();
			} catch (IOException ioe) {
				LOGGER.debug("IOE: {}", ioe.getMessage());
			} catch (Exception ignore) {
				LOGGER.debug("IGNORED: {}", ignore.getMessage());
			}
			}
		}
	}

	/**
	 * 첨부로 등록된 파일을 서버에 업로드한다.
	 *
	 * @param file
	 * @return
	 * @throws Exception

	public static HashMap<String, String> uploadFile(MultipartFile file) throws Exception {

	HashMap<String, String> map = new HashMap<String, String>();
	//Write File 이후 Move File????
	String newName = "";
	String stordFilePath = EgovProperties.getProperty("Globals.fileStorePath");
	String orginFileName = file.getOriginalFilename();

	int index = orginFileName.lastIndexOf(".");
	//String fileName = orginFileName.substring(0, _index);
	String fileExt = orginFileName.substring(index + 1);
	long size = file.getSize();

	//newName 은 Naming Convention에 의해서 생성
	newName = EgovStringUtil.getTimeStamp() + "." + fileExt;
	writeFile(file, newName, stordFilePath);
	//storedFilePath는 지정
	map.put(Globals.ORIGIN_FILE_NM, orginFileName);
	map.put(Globals.UPLOAD_FILE_NM, newName);
	map.put(Globals.FILE_EXT, fileExt);
	map.put(Globals.FILE_PATH, stordFilePath);
	map.put(Globals.FILE_SIZE, String.valueOf(size));

	return map;
	}
*/
	/**
	 * 파일을 실제 물리적인 경로에 생성한다.
	 *
	 * @param file
	 * @param newName
	 * @param stordFilePath
	 * @throws Exception
	 */
	protected static void writeFile(MultipartFile file, String newName, String stordFilePath) throws Exception {
	InputStream stream = null;
	OutputStream bos = null;
	newName = StringUtil.isNullToString(newName).replaceAll("..", "");
	stordFilePath = StringUtil.isNullToString(stordFilePath).replaceAll("..", "");
	try {
		stream = file.getInputStream();
		File cFile = new File(stordFilePath);

		if (!cFile.isDirectory())
		cFile.mkdir();

		bos = new FileOutputStream(stordFilePath + File.separator + newName);

		int bytesRead = 0;
		byte[] buffer = new byte[BUFF_SIZE];

		while ((bytesRead = stream.read(buffer, 0, BUFF_SIZE)) != -1) {
		bos.write(buffer, 0, bytesRead);
		}
	} catch (FileNotFoundException fnfe) {
		LOGGER.debug("fnfe: {}",fnfe);
	} catch (IOException ioe) {
		LOGGER.debug("ioe: {}", ioe);
	} catch (Exception e) {
		LOGGER.debug("e: {}", e);
	} finally {
		if (bos != null) {
		try {
			bos.close();
		} catch (IOException ioe) {
			LOGGER.debug("IOE: {}", ioe.getMessage());
		} catch (Exception ignore) {
			LOGGER.debug("IGNORED: {}", ignore.getMessage());
		}
		}
		if (stream != null) {
		try {
			stream.close();
		} catch (IOException ioe) {
			LOGGER.debug("IOE: {}", ioe.getMessage());
		} catch (Exception ignore) {
			LOGGER.debug("IGNORED: {}", ignore.getMessage());
		}
		}
	}
	}

	/**
	 * 서버 파일에 대하여 다운로드를 처리한다.
	 *
	 * @param response
	 * @param streFileNm
	 *            : 파일저장 경로가 포함된 형태
	 * @param orignFileNm
	 * @throws Exception
	 */
	public void downFile(HttpServletResponse response, String fileFullPath, String orignFileNm) throws Exception {
	//	String downFileName = EgovStringUtil.isNullToString(request.getAttribute("downFile")).replaceAll("..","");
	//	String orgFileName = EgovStringUtil.isNullToString(request.getAttribute("orgFileName")).replaceAll("..","");
	String downFileName = StringUtil.isNullToString(fileFullPath).replaceAll("..","");
	String orgFileName = StringUtil.isNullToString(orignFileNm).replaceAll("..","");

	File file = new File(downFileName);
	//log.debug(this.getClass().getName()+" downFile downFileName "+downFileName);
	//log.debug(this.getClass().getName()+" downFile orgFileName "+orgFileName);

	if (!file.exists()) {
		throw new FileNotFoundException(downFileName);
	}

	if (!file.isFile()) {
		throw new FileNotFoundException(downFileName);
	}

	//byte[] b = new byte[BUFF_SIZE]; //buffer size 2K.
	int fSize = (int)file.length();
	if (fSize > 0) {
		BufferedInputStream in = null;

		try {
		in = new BufferedInputStream(new FileInputStream(file));

				String mimetype = "text/html"; //"application/x-msdownload"

				response.setBufferSize(fSize);
		response.setContentType(mimetype);
		response.setHeader("Content-Disposition:", "attachment; filename=" + orgFileName);
		response.setContentLength(fSize);
		//response.setHeader("Content-Transfer-Encoding","binary");
		//response.setHeader("Pragma","no-cache");
		//response.setHeader("Expires","0");
		FileCopyUtils.copy(in, response.getOutputStream());
		} finally {
		if (in != null) {
			try {
			in.close();
			} catch (IOException ioe) {
				LOGGER.debug("IOE: {}", ioe.getMessage());
			} catch (Exception ignore) {
				LOGGER.debug("IGNORED: {}", ignore.getMessage());
			}
		}
		}
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}

	/*
	String uploadPath = propertiesService.getString("fileDir");

	File uFile = new File(uploadPath, requestedFile);
	int fSize = (int) uFile.length();

	if (fSize > 0) {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(uFile));

		String mimetype = "text/html";

		response.setBufferSize(fSize);
		response.setContentType(mimetype);
		response.setHeader("Content-Disposition", "attachment; filename=\""
					+ requestedFile + "\"");
		response.setContentLength(fSize);

		FileCopyUtils.copy(in, response.getOutputStream());
		in.close();
		response.getOutputStream().flush();
		response.getOutputStream().close();
	} else {
		response.setContentType("text/html");
		PrintWriter printwriter = response.getWriter();
		printwriter.println("<html>");
		printwriter.println("<br><br><br><h2>Could not get file name:<br>" + requestedFile + "</h2>");
		printwriter.println("<br><br><br><center><h3><a href='javascript: history.go(-1)'>Back</a></h3></center>");
		printwriter.println("<br><br><br>&copy; webAccess");
		printwriter.println("</html>");
		printwriter.flush();
		printwriter.close();
	}
	//*/


	/*
	response.setContentType("application/x-msdownload");
	response.setHeader("Content-Disposition:", "attachment; filename=" + new String(orgFileName.getBytes(),"UTF-8" ));
	response.setHeader("Content-Transfer-Encoding","binary");
	response.setHeader("Pragma","no-cache");
	response.setHeader("Expires","0");

	BufferedInputStream fin = new BufferedInputStream(new FileInputStream(file));
	BufferedOutputStream outs = new BufferedOutputStream(response.getOutputStream());
	int read = 0;

	while ((read = fin.read(b)) != -1) {
		outs.write(b,0,read);
	}
	log.debug(this.getClass().getName()+" BufferedOutputStream Write Complete!!! ");

	outs.close();
		fin.close();
	//*/
	}

	public Map<String, List<MultipartFile>> getMultipartFiles(MultipartRequest multipartRequest){
		Map<String, List<MultipartFile>> fileMap = new HashMap<String, List<MultipartFile>>();

		Map<String, MultipartFile> files = multipartRequest.getFileMap();

		if(!files.isEmpty()){
			Iterator<Entry<String, MultipartFile>> itr = files.entrySet().iterator();
			while (itr.hasNext()) {

				Entry<String, MultipartFile> entry = itr.next();
				String key = entry.getKey();

				List<MultipartFile> fileList = multipartRequest.getFiles(entry.getKey());

				if(fileNullCheck(fileList)) fileMap.put(key, fileList);
			}
		}
		return fileMap;
	}

	public static void deleteFile(String filePath){
		File file = new File(filePath);
		if(file.exists()) file.delete();
	}

	public static BufferedImage resize(String filePath, int destWidth, int destHeight, Object interpolation) throws Exception {
		BufferedImage source = ImageIO.read(new File(filePath));
		if (source == null) return null;
//			throw new NullPointerException("source image is NULL!");
		if (destWidth <= 0 && destHeight <= 0)
			throw new IllegalArgumentException("destination width & height are both <=0!");
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();
		double xScale = ((double) destWidth) / (double) sourceWidth;
		double yScale = ((double) destHeight) / (double) sourceHeight;
		if (destWidth <= 0) {
			xScale = yScale;
			destWidth = (int) Math.rint(xScale * sourceWidth);
		}
		if (destHeight <= 0) {
			yScale = xScale;
			destHeight = (int) Math.rint(yScale * sourceHeight);
		}
//         GraphicsConfiguration gc = getDefaultConfiguration();
//         BufferedImage result = gc.createCompatibleImage(destWidth, destHeight, source.getColorModel().getTransparency());
//         Graphics2D g2d = null;
//         try {
//             g2d = result.createGraphics();
//             g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);
//             AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
//             g2d.drawRenderedImage(source, at);
//         } finally {
//             if (g2d != null)
//                 g2d.dispose();
//         }
		BufferedImage result = new BufferedImage(destWidth, destHeight, source.getColorModel().getTransparency());
		Graphics2D g2d = null;
		try {
			g2d = result.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);
			AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
			g2d.drawRenderedImage(source, at);
		} finally {
			if (g2d != null)
				g2d.dispose();
		}

		return result;
	}

	public boolean fileTypeCheck(Map<String, List<MultipartFile>> files, String type) throws Exception {
		boolean resultBoolean = false;
		boolean noEmptyFile = false;

		if(files == null) resultBoolean = true;
		else{
			Iterator<Entry<String, List<MultipartFile>>> itr = files.entrySet().iterator();
			List<MultipartFile> fileList;

			while (itr.hasNext()) {
				Entry<String, List<MultipartFile>> entry = itr.next();

				fileList = entry.getValue();

				for(MultipartFile file : fileList){
					String orginFileName = file.getOriginalFilename();

					//--------------------------------------
					// 원 파일명이 없는 경우 처리
					// (첨부가 되지 않은 input file type)
					//--------------------------------------
					if ("".equals(orginFileName)) continue;
					noEmptyFile = true;
					////------------------------------------

					int index = orginFileName.lastIndexOf(".");
					String fileExt = orginFileName.substring(index + 1);

					fileExt = fileExt.toLowerCase();

					String[] fileCheckList = null;
					if(type.equals(NONE)) fileCheckList = NONE_FILES;
					else fileCheckList = IMAGE_FILES;

					for( String fileCheck : fileCheckList ){
						if(fileExt.equals(fileCheck)) resultBoolean = true;
					}
				}
			}

		}

		// 첨부파일 미입력시
		if(!noEmptyFile && !resultBoolean) resultBoolean = true;

		return resultBoolean;
	}

	private boolean fileNullCheck(List<MultipartFile> fileList){
		boolean returnBoolean = false;
		for(MultipartFile file : fileList){
			if(!file.getOriginalFilename().isEmpty()){
				returnBoolean = true;
			}
		}
		return returnBoolean;
	}

	public boolean fileBySequenceCheck(Map<String, List<MultipartFile>> fileMap, String num){
		boolean returnBoolean = false;

		for (String key : fileMap.keySet()) {
			if(key.indexOf(String.format("%02d", Integer.parseInt(num)))!=-1){
				if(fileMap.get(key)!=null) returnBoolean = true;
			}
        }
		return returnBoolean;
	}

	public Map<String, List<MultipartFile>> getFileBySequence(Map<String, List<MultipartFile>> fileMap, String num){

		Map<String, List<MultipartFile>> returnFileMap = new HashMap<String, List<MultipartFile>>();

		for (String key : fileMap.keySet()) {
			if(key.indexOf(String.format("%02d", Integer.parseInt(num)))!=-1){
				//System.out.println(fileMap.get(key));
				if(fileMap.get(key)!=null) returnFileMap.put(key, fileMap.get(key));
			}
        }
		return returnFileMap;
	}
}
