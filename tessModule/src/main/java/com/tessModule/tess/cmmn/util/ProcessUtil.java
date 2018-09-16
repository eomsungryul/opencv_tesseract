package com.tessModule.tess.cmmn.util;

import java.io.File;
import java.util.Properties;

import javax.annotation.Resource;

import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Component;

import com.tessModule.tess.cmmn.service.StatusLogVO;
import com.tessModule.tess.convert.service.ConvertService;

@Component("processUtil")
public class ProcessUtil {

	@Resource(name="convertService")
	private ConvertService convertService;

	/*@Resource(name = "verProps")
	private Properties verProps; */
	
	@Resource(name = "configProps")
	private Properties configProps; 
	
	
	/**
	 * app 버전에 따라 진행을 계속 할지 말지에 대한 여부를 체크함.
	 *
	 * @param flag 암호화될 패스워드
	 * @param id salt로 사용될 사용자 ID 지정
	 * @return
	 * @throws Exception
	 */
	public boolean isProcess(boolean flag, StatusLogVO errorLog) throws Exception {
		//flag가 그냥 true일 경우 통과
		if (flag) {
			
			return flag;
		}else {
			
			//flag가 false일 경우 에러 로그를 저장하고, 버전에 따라 진행을 계속할지 말지 결정 
			//String appVersion = verProps.getProperty("version");
			String appVersion = "";
			
			//에러로그를 저장함.
			convertService.insertErrorLog(errorLog);
			
			//에러가 난 Mat파일을 에러폴더에 저장함.

			String storePathString = configProps.getProperty("imgErrorPath");
			String subPath = File.separator + StringUtil.getTimeStamp().substring(0,6) + File.separator + errorLog.getConvertId();

			File saveFolder = new File(storePathString+subPath);

			if (!saveFolder.exists() || saveFolder.isFile()) {
				saveFolder.mkdirs();
			}
			
			if(errorLog.getErrorMat()!=null) {
				
				//TODO 에러파일경로\\년도달\\변환ID\\에러 함수이름.jpg형태로 만듬 추후에 변경 가능
				Imgcodecs.imwrite(storePathString+subPath+File.separator+errorLog.getRequestMethod()+".jpg",errorLog.getErrorMat());
				
			}else if(errorLog.getErrorMat()==null && "-103".equals(errorLog.getExceptionCode())){
				
				//TODO 테서렉트 에러 일경우 opencv에서 만든 이미지들을 모두 옮겨야한다.

				String workingPath = configProps.getProperty("imgWorkingPath");
				//폴더 참조 
				File original_dir = new File(configProps.getProperty("imgWorkingPath")+ File.separator + errorLog.getConvertId()); 
				//절대경로
				File move_dir = saveFolder;
				
				if (!move_dir.exists() || move_dir.isFile()) {
					move_dir.mkdirs();
				}
				if(original_dir.exists()) { 
					
					//폴더의 내용물 확인 -> 폴더 & 파일.. 
					File[] fileNames = original_dir.listFiles(); 
					
					for(int i=0; i< fileNames.length; i++) { 
						if(fileNames[i].isFile()) { 
							if(fileNames[i].exists()) { 
								if(original_dir.exists()) { } File MoveFile = new File(move_dir, fileNames[i].getName()); 
							// 이동될 파일 경로 및 파일 이름 
							fileNames[i].renameTo(MoveFile); //변경(이동) 
							}
						}
					}
				}
			}
			
			if(Double.valueOf( appVersion ) < 1) {
				
				//개발 버전일 경우
				return true;
			}else {
				//개발 버전이 아닐 경우
			}
			
			return flag;
		}
	}



}
