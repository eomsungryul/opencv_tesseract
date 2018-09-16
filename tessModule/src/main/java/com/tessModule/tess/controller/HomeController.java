package com.tessModule.tess.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tessModule.tess.cmmn.service.BcaService;
import com.tessModule.tess.cmmn.service.StatusLogVO;
import com.tessModule.tess.cmmn.service.UserBcaLogVO;
import com.tessModule.tess.cmmn.util.FileMngUtil;
import com.tessModule.tess.cmmn.util.ImgMngUtil;
import com.tessModule.tess.cmmn.util.JsonUtil;
import com.tessModule.tess.cmmn.util.OpencvUtil;
import com.tessModule.tess.cmmn.util.ProcessUtil;
import com.tessModule.tess.cmmn.util.ScrtyUtil;
import com.tessModule.tess.cmmn.util.TesseractUtil;
import com.tessModule.tess.convert.service.ConvertService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	@Resource(name="bcaService")
	private BcaService bcaService;

	@Resource(name="convertService")
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

	/** imgMngUtil */
	@Resource(name = "imgMngUtil")
	private ImgMngUtil imgMngUtil;

	/** jsonUtil */
	@Resource(name = "jsonUtil")
	private JsonUtil jsonUtil;

	/** jsonUtil */
	@Resource(name = "processUtil")
	private ProcessUtil processUtil;

	@Value("#{configProps['imgPath']}")
	protected String imgPath;

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 * @throws Exception 
	 */
	@RequestMapping(value = "/testtest", method = RequestMethod.GET)
	public String test(Locale locale, Model model) throws Exception {

		StatusLogVO sttsLog = new StatusLogVO();
		
		
		convertService.insertErrorLog(sttsLog);		
		
		return "home";
	}
	
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	
	/**
	 * Simply selects the home view to render by returning its name.
	 * @throws Exception 
	 */
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String img2textTest(Locale locale, Model model) throws Exception {
		
		// 앱에서 리프레시 하는 시간에 맞춰서 쿼츠의 시간을 조정한다.
		// 추후 모든 과정을 가지고 시간 체크 해야함
		String filePath = "";
		String status = "";
		Map<String, Object> workVal;
		List<Map<String, Object>> img2textResult = null;
		List<Map<String, Object>> workList;
		Map<String, Object> errorVal = null;
		
		Map<String, Object> modelInfo;
		List<Map<String, Object>> modelCategoryList;
		List<Map<String, Object>> categoryItemList;
		JSONArray categoryItemMatchList;
		JSONArray opencvImgConvertList = null;
		boolean flag = false;
		StatusLogVO errorLog = new StatusLogVO();
		UserBcaLogVO uBcaVO = new UserBcaLogVO();
		
		Map<String, Object> searchParam = new HashMap<String, Object>();
		try {
			
			// 1. DB에서 체성분분석기 변환 테이블(bca_convert)데이터를 조회하여 작업 대기중( status=0 )인 변환ID를 가져온다.
			searchParam.put("convertStatus",0);
			workList = convertService.selectConvert(searchParam);
			 
			for(int i = 0; i < workList.size(); i++){
				
				workVal = workList.get(i);
				filePath = imgPath+workList.get(i).get("INPUT_FILE_LOCATION").toString();
				errorLog.setConvertId(workList.get(i).get("CONVERT_ID").toString());
				
			// TODO 테스트 할 시에는 주석 후 테스트	
			// 2. 조회된 변환ID 리스트들을  작업 진행중(status = 1) 상태로 DB update처리한다. 업데이트 처리 후 for문을 이용하여 하나씩 작업을 수행한다. 

//				 searchParam.put("convertId",workList.get(i).get("CONVERT_ID").toString());
//				 searchParam.put("convertStatus",1);
//				 convertService.updateConvert(searchParam);
				
			// 3. 이미지 경로값을 이용하여  기울기 보정을 수행한다. 
				Mat orgImg = Imgcodecs.imread( filePath );
				flag = opencvUtil.reviseGradient( orgImg, errorLog );
				
				if(processUtil.isProcess(flag,errorLog)) {
					
					// 4. 조회된 변환ID 리스트의 이미지 경로값을 이용하여 이미지의 체성분 결과지 테두리 인식을 수행한다. 
					flag = opencvUtil.recognEdge( orgImg, errorLog );
					
					if(processUtil.isProcess(flag, errorLog)) {

						// 5. 테두리 인식과 기울기 보정을 마친 이미지 경로값을 이용하여 해당 이미지의 해상도를 글자 검출을 하기위한 디폴트 해상도로 변경한다. 
						//	    그때 파일서버\work\변환ID 폴더내에 저장 이후로 모든 이미지 파일들은 파일서버\work\변환ID 폴더에 저장한다.  
						flag = opencvUtil.resizeImg(orgImg, 3024, 4032, errorLog);
							
						if(processUtil.isProcess(flag, errorLog)) {
							
							// 6. 조회된 변환ID 리스트값 중 헬스장 ID를 이용하여 헬스장 체성분 분석기 테이블(gym_bca)의 체성분분석기 모델 정보를 조회한다.
							modelInfo = bcaService.selectGymBca(Integer.parseInt(workVal.get("GYM_ID").toString()));
								
							// 7. 조회된 체성분분석기 모델정보를 이용하여 체성분분석기 모델별 분류 테이블의 (bca_attr_coordinate) 해당 모델정보의 리스트를 가져온다. 
							//	    체성분분석기 모델별 분류 테이블의 리스트는 인바디 양식지 이미지를 분류별로 자를때 사용한다.
							modelCategoryList = bcaService.selectCategory(modelInfo.get("BCA_MODEL_CD").toString());
			
							// 8. 체성분분석기 모델별 분류 테이블의 리스트의 	모델코드와 분류코드를 를 이용하여 체성분분석기 분류별 항목(bca_categry_attr)테이블을 조회한다.
							//	    체성분분석기 분류별 항목 리스트는 분류별로 잘라진 이미지 안의 항목명을 가져올때 사용한다.
							categoryItemList = bcaService.selectItem(modelInfo.get("BCA_MODEL_CD").toString());
							
							// 9. 모델별 분류 테이블 가져온 결과값리스트와 분류별 항목리스트의 분류코드를 이용하여 JSONArray를 생성한다. (ex. [{분류1={분류값,항목리스트=[{},{}]}},{분류2={...}}] )
							//	     이 리스트는 글자영역검출 작업 시에 그 글자와 리스트의 항목이 순서대로 매핑을 해주는 역할을 한다. 
							categoryItemMatchList = categoryItemMatch(modelCategoryList,categoryItemList);
								
							// 10. 해상도 변경된 이미지를 opencv로 이용하여 분석한다.
							flag = opencvUtil.analysisOpencv(orgImg, categoryItemMatchList, opencvImgConvertList, errorLog);
							
							if(processUtil.isProcess(flag, errorLog)) {
								
								// 11. openCv에서 잘려진 이미지 리스트(opencvImgConvertList)를 이용하여 테서렉트를 통해 이미지 > 텍스트 분석작업을 한다.
								flag = tesseractUtil.analysisTesseract(opencvImgConvertList, img2textResult, errorLog);

								if(processUtil.isProcess(flag, errorLog)) {
									// 12. 분석작업을 마친 값을 이용하여 사용자 체성분분석기 결과(USER_BCA_LOG) 테이블에 저장한다.
									for(Map<String,Object> res: img2textResult) {
										convertService.insertUserBcaLog(uBcaVO);
									}
								}else {
									break;
								}
									
							}else {
								break;
							}
								
						}else {
							break;
						}
						
					}else {
						break;
					}
					
				}else {
					break;
				}
				 
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return "home";
	}
	

	private JSONArray categoryItemMatch(List<Map<String, Object>> modelCategoryList, List<Map<String, Object>> categoryItemList) {

		JSONArray modelCategoryArr = jsonUtil.getJsonArrayFromList(modelCategoryList);
		JSONArray categoryItemArr = jsonUtil.getJsonArrayFromList(categoryItemList);
		JSONArray matchArr = new JSONArray();
		JSONArray resultArr = new JSONArray();
		JSONObject categotyObj = new JSONObject();
		JSONObject itemObj = new JSONObject();
		
		//TODO 차후 코드의  형이 바뀔 시에  바껴야
		long ctryCodeNm = 0;
		long itemCodeNm = 0;
		
		for (int i=0; i<modelCategoryArr.size(); i++) {
			
			categotyObj = ( JSONObject ) modelCategoryArr.get(i);
			ctryCodeNm = (long) categotyObj.get("BCA_CATEGORY_CODE");
			
			for(int j=0; j<categoryItemArr.size(); j++) {
				itemObj = ( JSONObject ) categoryItemArr.get(j);
				itemCodeNm = (long) itemObj.get("BCA_CATEGORY_CODE");
				
				if(ctryCodeNm==itemCodeNm) {
					matchArr.add(itemObj);
				}
			}
			categotyObj.put("BCA_ITEM_ARRAY",matchArr);
			resultArr.add(categotyObj);
		}

		return resultArr;
	}
    
    
    



	
}
