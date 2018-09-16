package com.tessModule.tess.convert.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.tessModule.tess.cmmn.util.FileMngUtil;
import com.tessModule.tess.cmmn.util.OpencvUtil;
import com.tessModule.tess.cmmn.util.ScrtyUtil;
import com.tessModule.tess.cmmn.util.TesseractUtil;
import com.tessModule.tess.common.message.EncodingResourceBundleMessageSource;
import com.tessModule.tess.common.properties.LoadProperties;
import com.tessModule.tess.convert.service.ConvertService;
import com.tessModule.tess.convert.vo.Convert;

/**
 * Handles requests for the application home page.
 */
@Controller
public class ConvertController {

	@Resource(name = "convertService")
	private ConvertService convertService;
	
	/** fileUtil */
	@Resource(name = "fileMngUtil")
	private FileMngUtil fileUtil;
	
	/** opencvUtil */
	@Resource(name = "opencvUtil")
	private OpencvUtil opencvUtil;

	/** tesseractUtil */
	@Resource(name = "tesseractUtil")
	private TesseractUtil tesseractUtil;

	/** opencvUtil */
	@Resource(name = "scrtyUtil")
	private ScrtyUtil scrtyUtil;

	@Resource(name = "configProps")
    Properties configProps; 
	
	@Inject
    @Named("messageSource")
    private EncodingResourceBundleMessageSource messageSource;

	private static final Logger logger = LoggerFactory.getLogger(ConvertController.class);
	
	/**
	 * 헬스앱에서 TESSERACT서버로 이미지>텍스트 변환을 요청합니다.
	 * @throws Exception 
	 */
	@RequestMapping(value = "/converter/", method = RequestMethod.POST)
	public HashMap<String, String> converter(HttpServletRequest request, @Validated @RequestBody Convert convert) throws Exception {
		
		HashMap<String, String> map = new HashMap<String, String>();
		int status = 1; // 상태는 1이 정상 처리 완료이다.
		String statusMsg = new String(); // 상태는 1이 정상 처리 완료이다.
		
		// 1. 리퀘스트에서 인증키를 받는다 
		if ("null".equals(convert.getUser_id())||"null".equals(convert.getGym_id())||"null".equals(convert.getInput_file_location())||"null".equals(convert.getApi_key())) {
			String[] statusArgs = {new StringBuffer("user_id=").append(convert.getUser_id()).append("&gym_id=").append(convert.getGym_id())
					.append("&input_file_location=").append(convert.getInput_file_location()).append("&api_key=").append(convert.getApi_key()).toString()};
			statusMsg = messageSource.getMessageInternal("pameterNotFound", statusArgs);
			status = -2;
		} else {
			// 2. userid와 apikey를 더하고 sha256으로 암호화 하여 리퀘스트의 인증키와 비교한다
			String serverAuthKey = scrtyUtil.encryptApi(LoadProperties.API_KEY_VALUE, Integer.toString(convert.getUser_id()));
			
			if(serverAuthKey.equals(convert.getApi_key())) {
				// 2-1. 인증키와 맞을 시 체성분분석기 변환 테이블에 저장하고, 변환테이블의 변환 ID를 response 값으로 담아낸다.
				convert.setConvert_id(convertService.insert(convert)); 
			}else{
				//2-2. 인증키가 잘못됐다면 인증 실패 메시지를 리턴한다.
				String[] statusArgs = {"convert_id"};
				map.put("msg", messageSource.getMessageInternal("failAuthorization", statusArgs)); 
				status = -1;
			}
		}
		
		map.put("convert_id", new Integer(convert.getConvert_id()).toString());
		map.put("status", new Integer(status).toString());
		map.put("msg", statusMsg); 
		
		return map;
	}
	
	
	/**
	 * 헬스앱에서 TESSERACT서버로 이미지>텍스트 변환의 상태를 확인합니다.
	 * @throws Exception 
	 */
	@RequestMapping(value = "/convertstatus/", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, String> convertstatus(HttpServletRequest request,@RequestParam Map<String, Object> param) throws Exception{
		HashMap<String, String> map = new HashMap<String, String>();
		
		int status = 1; // 상태는 1이 정상 처리 완료이다.
		String statusMsg = new String(); // 상태는 1이 정상 처리 완료이다.
		
		// 1. 리퀘스트에서 변환 ID를 받는다
		String convertId = String.valueOf(param.get("convertId"));
		
		if ("null".equals(convertId)) {
			String[] statusArgs = {new StringBuffer("convertId=").append(convertId).toString()};
			statusMsg = messageSource.getMessageInternal("pameterNotFound", statusArgs);
			status = -1;
		}else{
			/*// 2. 디비에서 해당 변환 ID로 상태값을 가져온다. (2: 완료, 1: 진행중 , 0: 대기중, -1~-999: 실패)*/
			Convert convert = new Convert();
			
			//convert = convertService.get(convert.setConvert_status(convertId));
			
			if(convert == null){
				String[] statusArgs = {new StringBuffer("convertId=").append(convertId).toString()};
				statusMsg = messageSource.getMessageInternal("noDataFound", statusArgs);
				status = -2;
			}else{
				// 2-1. 상태값이  2 이면 디비에서 해당 체성분 분석기 결과값을 조회하고 response값으로 담아 전송한다. (인터페이스 api에 따른 json형태의 response값)
				if(convert.getConvert_status() == 2){
					
					
					
				}else { // 2-2. 상태값이  1,0,-1~-999 라면 상태코드값을 담아 response값으로 담아 전송한다. (인터페이스 api에 따른 json형태의 response값)
					status = convert.getConvert_status();
				}
			}
		}
		
		map.put("status", new Integer(status).toString());
		map.put("msg", statusMsg); 
		
		return map;
	}
	
	
	
	/**
	 * 헬스앱에서 TESSERACT서버로 이미지>텍스트 변환의 상태를 확인합니다.
	 * @throws Exception 
	
	@RequestMapping(value = "/tconvertstatus/", method = RequestMethod.POST)
	public String tconvertstatus(
			MultipartHttpServletRequest multiRequest,
			HttpServletRequest request,
			Locale locale, 
			Model model
			) {
		
		
		Map<String, List<MultipartFile>> files = fileUtil.getMultipartFiles(multiRequest);
		
		int x = Integer.parseInt( request.getParameter("x").toString());
		int y = Integer.parseInt( request.getParameter("y").toString());
		
		//		//TODO 2. 서비스 보내기
//		moduleService.convertImg2Text(paramMap);
		
		try {
			//file upload;
			String filePath = fileUtil.parseFileInf(files, "", "");
			//openCv 
//			List<String> fileList= opencvUtil.givenTessBaseApi_whenImageOcrd_thenTextDisplayed(filePath);
			List<String> fileList= opencvUtil.givenTessBaseApi_whenImageOcrd_thenTextDisplayed(filePath,x,y);
			//tesseract
			tesseractUtil.tessractAnalysis(fileList);
		} catch (Exception e) {
//			e.printStackTrace();
//			e.getClass();
		}
		return "home";
	} */
	
	
}
