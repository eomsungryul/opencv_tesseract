package com.tessModule.tess.cmmn.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tessModule.tess.cmmn.util.FileMngUtil;
import com.tessModule.tess.cmmn.util.ImgMngUtil;
import com.tessModule.tess.cmmn.util.JsonUtil;
import com.tessModule.tess.cmmn.util.OpencvUtil;
import com.tessModule.tess.cmmn.util.ProcessUtil;
import com.tessModule.tess.cmmn.util.ScrtyUtil;
import com.tessModule.tess.cmmn.util.TesseractUtil;
import com.tessModule.tess.common.message.EncodingResourceBundleMessageSource;
import com.tessModule.tess.convert.service.ConvertService;
import com.tessModule.tess.convert.service.impl.ConvertImageImpl;
import com.tessModule.tess.convert.vo.Convert;

@Service("quartzService")
public class QuartzService {
	
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
	
	@Inject
    @Named("messageSource")
    private EncodingResourceBundleMessageSource messageSource;

	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzService.class);

	/**
	 * 체성분 분석기의 이미지를 읽어 텍스트로 변환하는 컨트롤러
	 *
	 * @param
	 * @return
	 * @throws Exception
	 */
	public void img2text() throws Exception{

		LOGGER.debug("====================START SCHEDULE=================");
		Convert convert = new Convert();
		
		
		// 앱에서 리프레시 하는 시간에 맞춰서 쿼츠의 시간을 조정한다.
		// 추후 모든 과정을 가지고 시간 체크 해야함
		
		/*String filePath = "";
		Map<String, Object> workVal;
		List<Map<String, Object>> img2textResult = null;
		List<Map<String, Object>> workList;
		
		Map<String, Object> modelInfo;
		List<Map<String, Object>> modelCategoryList;
		List<Map<String, Object>> categoryItemList;
		JSONArray categoryItemMatchList;
		JSONArray opencvImgConvertList = null;
		boolean flag = false;
		StatusLogVO errorLog = new StatusLogVO();
		UserBcaLogVO uBcaVO = new UserBcaLogVO();*/
		
		
		try {
			// 1. DB에서 체성분분석기 변환 테이블(bca_convert)데이터를 조회하여 작업 대기중( status=0 )인 변환ID를 가져온다.
			convert.setConvert_status(0);
			List<Convert> queueConvertList = this.convertService.list(convert);
			 
			for(int i = 0; i < queueConvertList.size(); i++){
				Convert queueConvert = (Convert)queueConvertList.get(i);
				
				// 2. 조회된 변환ID 리스트들을  작업 진행중(status = 1) 상태로 DB update처리한다. 업데이트 처리 후 for문을 이용하여 하나씩 작업을 수행한다. 
				queueConvert.setConvert_status(1);
				//this.convertService.update(queueConvert); 
				
				// 3. 이미지 경로값을 이용하여  기울기 보정을 수행한다. 
				Mat orgImg = new ConvertImageImpl().readImageToMat(queueConvert);
				
			}
				/*if(orgImg!=null){
					flag = this.opencvUtil.reviseGradient( orgImg, errorLog );
				}else{
					messageSource.getMessageInternal();
				}
				
				
				
				
				filePath = this.imgPath+workList.get(i).get("INPUT_FILE_LOCATION").toString();
				errorLog.setConvertId(workList.get(i).get("CONVERT_ID").toString());
				
				
				// 3. 이미지 경로값을 이용하여  기울기 보정을 수행한다. 
				Mat orgImg = Imgcodecs.imread( filePath );
				flag = this.opencvUtil.reviseGradient( orgImg, errorLog );
				
				if(this.processUtil.isProcess(flag,errorLog)) {
					
					// 4. 조회된 변환ID 리스트의 이미지 경로값을 이용하여 이미지의 체성분 결과지 테두리 인식을 수행한다. 
					flag = this.opencvUtil.recognEdge( orgImg, errorLog );
					
					if(processUtil.isProcess(flag, errorLog)) {

						// 5. 테두리 인식과 기울기 보정을 마친 이미지 경로값을 이용하여 해당 이미지의 해상도를 글자 검출을 하기위한 디폴트 해상도로 변경한다. 
						//	    그때 파일서버\work\변환ID 폴더내에 저장 이후로 모든 이미지 파일들은 파일서버\work\변환ID 폴더에 저장한다.  
						flag = this.opencvUtil.resizeImg(orgImg, 3024, 4032, errorLog);
							
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
				 
			}*/

		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		LOGGER.debug("====================END SCHEDULE=================");
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
