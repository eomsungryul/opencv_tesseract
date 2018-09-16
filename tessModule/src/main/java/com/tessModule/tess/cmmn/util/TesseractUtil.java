package com.tessModule.tess.cmmn.util;

import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept.PIX;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import com.tessModule.tess.cmmn.service.StatusLogVO;

@Component("tesseractUtil")
public class TesseractUtil {

	/*@Resource(name = "errorProps")
    Properties errorProps; */
	
	/**
	 * 11. openCv에서 잘려진 이미지 리스트를 이용하여 테서렉트를 통해 이미지 > 텍스트 분석작업을 한다.
	 * @param errorLog 
	 * @return 
	 */
	public boolean analysisTesseract(JSONArray opencvImgConvertList, List<Map<String, Object>> img2textResult, StatusLogVO errorLog) {
		
		//TODO
		// 11-1. 결과값을 담을 맵을 생성한다. 
		// 11-2. 테서렉트 tessdata 경로를 불러온다. 
		// 11-3. 숫자들을 화이트리스트에 등록한다.
		// 11-4. 반환된 결과값을 읽어 검출한다. 
		// 11-5. 결과값을 Map에 저장함. 결과값이 비어있으면 에러 처리 - 이미지 > 텍스트 분석작업 에러. 
		BytePointer outText;
		JSONObject ItemObj = new JSONObject();

        String pattern = "[0-9]{1,3}\\.[0-9]{1,2}";
		
        for(int i = 0 ; i < opencvImgConvertList.size(); i ++){
	        TessBaseAPI api = new TessBaseAPI();
	        // Initialize tesseract-ocr with English, without specifying tessdata path
	        if (api.Init("C:/_app/spring-tool-suite-3.9.4.RELEASE-e4.7.3a-win32-x86_64/workspace/tessModule/src/main/webapp", "ENG") != 0) {
	            System.err.println("Could not initialize tesseract.");
	            System.exit(1);
	        }
	        
	        ItemObj = (JSONObject) opencvImgConvertList.get(i);
	        String imgPath = ItemObj.get("imgPath").toString();
	
	        String whitelist = "0123456789.";
	        api.SetVariable("tessedit_char_whitelist", whitelist);
	        api.SetVariable("classify_bln_numeric_mode", "1");
	        // Open input image with leptonica library
	        PIX image = pixRead(imgPath);
	        api.SetImage(image);
	        // Get OCR result
	        outText = api.GetUTF8Text();
	        String string = outText.getString();
	        assertTrue(!string.isEmpty());
//	        System.out.println("OCR output:" + string);
	        // Destroy used object and release memory
	        api.End();
	        outText.deallocate();
	        pixDestroy(image);
	        


	        // TODO 테서렉트 결과값이 비어있거나, 소수점이 없을경우나 정수가 3자리 이상일 때  에러 처리함.
	        if(!pattenChk(pattern, string)||string.length()==0) {

				//테서렉트 결과값 에서 에러 발생 시 에러 처리
		    	int errorCode = -103;
				//errorLog.setExceptionMesseage(errorProps.getProperty(String.valueOf(errorCode)));
				errorLog.setExceptionCode(errorCode);
				errorLog.setExceptionErrors(errorLog.getConvertId()+" : " + string + " : ");
				errorLog.setRequestMethod(getClass().getEnclosingMethod().getName());
				errorLog.setErrorMat(null);

		        // TODO 아직 테서렉트 에러가 날 시에 계속 진행 할 지 말지에 대한 결정이 안 나있는 상태  
				// 현재는 에러가 날 시에 바로 테서렉트 분석을 중단 하고, 에러 테이블에 에러코드를 한번만 저장하고, opencv에 검출된 전체 파일들을 옮기는 작업만 실행 함
				// 추후 변경되면 변경해야된다.
				return false;
	        }
	        
        }
		
		return true;
	}
	
	
	

    public boolean pattenChk(String pattern, String value)
    {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(value);
        boolean ok = m.matches();
		return ok;
    }

	
	
	
	
	
	
	
	
    public void tessractAnalysis( List<String> fileList ) throws Exception {
	    
	    //5. tessreact로 텍스트 분석
	    System.out.println("===5. tessreact로 텍스트 분석 시작===");
	    long start = System.currentTimeMillis();
        BytePointer outText;
        for(int i = 0 ; i < fileList.size(); i ++){
	        TessBaseAPI api = new TessBaseAPI();
	        // Initialize tesseract-ocr with English, without specifying tessdata path
	        if (api.Init("C:/_app/spring-tool-suite-3.9.4.RELEASE-e4.7.3a-win32-x86_64/workspace/tessModule/src/main/webapp", "ENG") != 0) {
	            System.err.println("Could not initialize tesseract.");
	            System.exit(1);
	        }
	
	        String whitelist = "0123456789.";
	        api.SetVariable("tessedit_char_whitelist", whitelist);
	        api.SetVariable("classify_bln_numeric_mode", "1");
	        // Open input image with leptonica library
	        PIX image = pixRead(fileList.get(i));
	        api.SetImage(image);
	        // Get OCR result
	        outText = api.GetUTF8Text();
	        String string = outText.getString();
	        assertTrue(!string.isEmpty());
	        System.out.println("OCR output:" + string);
	        // Destroy used object and release memory
	        api.End();
	        outText.deallocate();
	        pixDestroy(image);
        }
        long end = System.currentTimeMillis();
	    System.out.println( "실행 시간 : " + ( end - start )/1000.00000 );
	    System.out.println("===5. tessreact로 텍스트 분석 끝===");
	    System.out.println("\r");
    }
}