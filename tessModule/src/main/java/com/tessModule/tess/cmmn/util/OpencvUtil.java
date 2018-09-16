package com.tessModule.tess.cmmn.util;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.swing.ImageIcon;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tessModule.tess.cmmn.service.BcaService;
import com.tessModule.tess.cmmn.service.StatusLogVO;
import com.tessModule.tess.convert.service.ConvertService;

class Pxy{
	private int x;
	private int y;
	public Pxy() {
		super();
	}
	public Pxy(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	@Override
	public String toString() {
		return "Pxy [x=" + x + ", y=" + y + "]";
	}
}

@Component("opencvUtil")
public class OpencvUtil {

	@Resource(name="bcaService")
	private BcaService bcaService;

	@Resource(name="convertService")
	private ConvertService convertService;
	
	/** tesseractUtil */
	@Resource(name = "tesseractUtil")
	private TesseractUtil tesseractUtil;
	
//	@Value("#{configProps['imgPath']}")
//	private String imgPath;

	@Resource(name = "configProps")
    Properties configProps; 

	/*@Resource(name = "errorProps")
    Properties errorProps; */

	/**
	 * 
	 * 
	 * 
	 * @param src
	 * @param dst
	 * @param blurSizeX
	 * @param blurSizeY
	 * @param blurSigmaX
	 * @param cannyI
	 * @param cannyJ
	 * @return
	 */
	public Mat ready2Convert(Mat src, Mat dst, int blurSizeX, int blurSizeY, int blurSigmaX, int cannyI, int cannyJ) {
		try{

		// 흑백
		Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2GRAY);
		
		// 가우시안블러 
	    // sigmaX 값을 늘릴 수록 더 흐려짐 시간은 거의 비슷  사이즈는 클수록 오래 걸리고 흐려짐
	    Imgproc.GaussianBlur( dst, dst, new Size( blurSizeX, blurSizeY ), blurSigmaX);

	    // canny 
	    // j값을 높이면 점점 흐려진다 
	    Imgproc.Canny(dst, dst, cannyI, cannyJ);
	    
//		// morphologyEx
//		Mat kernel = new Mat(1,3, CvType.CV_8UC1); //TODO 이값을 조정하면서 테스트해야함.
//		Imgproc.morphologyEx(orgImg, orgImg, Imgproc.MORPH_GRADIENT, kernel);
//		
//		// adaptiveThreshold
//		Imgproc.adaptiveThreshold(orgImg, orgImg, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 12);  
	    
		}catch(Exception e){
			
			//TODO 모두 검은 화면의 이미지일 경우 에러 처리 (opencv 검색필요)
			System.out.println(e);
			
		}
		return dst;
	}
	
	
	/**
	 * 조회된 변환ID 리스트의 이미지 경로값을 이용하여 이미지의 체성분 결과지 테두리 인식을 수행한다. 
	 * @throws Exception 
	 */
	public boolean recognEdge(Mat orgImg, StatusLogVO errorLog) throws Exception {
		Mat cannyImg = new Mat(); 
	    cannyImg = ready2Convert(orgImg,cannyImg,11,11,0,100,200);
	    
	    //TODO 현재 인식이 잘된 값을 가져왔음 하지만 추후에 더 좋은 방법이 있을 시 변경 필요
	    Imgproc.threshold(cannyImg, cannyImg, 200,255, Imgproc.THRESH_BINARY); 
//        Imgproc.adaptiveThreshold(cannyImg, cannyImg, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 12);  

	    Mat hierarchy = new Mat();    //윤곽선 찾기(finding constour)를 위한 매트릭스 
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Imgproc.findContours(cannyImg, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
	    
	    int findPaper = 0;
	    if(contours.size()>0) {
            for(int idx = 0; idx < contours.size(); idx++) {
                Rect rect = Imgproc.boundingRect(contours.get(idx));
                if (rect.height > 1500 && rect.width > 1500   
                		//&&!(rect.width >= 512 - 5 && rect.height >= 512 - 5)
                		){
      		      	//윤곽찾기 결과확인용 시작
//                	Imgproc.drawContours(orgImg,contours, idx, new Scalar(0, 255, 0), 5);
                	//윤곽찾기 결과확인용 끝
                	//이미지 잘라내서 저장하기
//                	String file = "C:\\img_test\\work\\finished_"+idx+".png";
//      		      	Imgcodecs.imwrite(file,orgImg);
                	//TODO 이부근에서 사각형 에러 났음
                	Mat m = new Mat(orgImg, rect);
                	m.copyTo(orgImg);
                	findPaper++;
                }
            }
        }
	    
	    //테두리 인식이 되지 않았을 때
	    if(findPaper==0) {
	    	
	    	int errorCode = -100;
			//errorLog.setExceptionMesseage(errorProps.getProperty(String.valueOf(errorCode)));
			errorLog.setExceptionCode(errorCode);
			errorLog.setExceptionErrors(errorLog.getConvertId());
			errorLog.setRequestMethod(getClass().getEnclosingMethod().getName());
			errorLog.setErrorMat(orgImg);
			
			return false;
	    }
	    
	    
	    return true;
	}



	/**
	 * 흰색 픽셀들의  
	 * @param errorLog 
	 * @throws Exception 
	 */
	public boolean reviseGradient(Mat orgImg, StatusLogVO errorLog) throws Exception  {
		try {
	
		    Mat preConvertImg = new Mat(); 
		    preConvertImg =ready2Convert(orgImg,preConvertImg,11,11,0,100,200);
		    
		    //흰색 픽셀들을 찾는다. 
		    Mat wLocMat = Mat.zeros(preConvertImg.size(),preConvertImg.type());
		    Core.findNonZero(preConvertImg, wLocMat);
	
		    //Create an empty Mat and pass it to the function
		    MatOfPoint matOfPoint = new MatOfPoint( wLocMat );
	
		    //Translate MatOfPoint to MatOfPoint2f in order to user at a next step
		    MatOfPoint2f mat2f = new MatOfPoint2f();
		    matOfPoint.convertTo(mat2f, CvType.CV_32FC2);
	
		    //Get rotated rect of white pixels
		    RotatedRect rotatedRect = Imgproc.minAreaRect( mat2f );
	
		    Point[] vertices = new Point[4];
		    rotatedRect.points(vertices);
		    
		    List<MatOfPoint> boxContours = new ArrayList<MatOfPoint>();
		    boxContours.add(new MatOfPoint(vertices));
		    
		    Imgproc.drawContours( preConvertImg, boxContours, 0, new Scalar(128, 128, 128), 1);
		    
		    //double resultAngle = rotatedRect.angle;
		    if (rotatedRect.size.width > rotatedRect.size.height)
		    {
		        rotatedRect.angle += 90.f;
		    }
		    
		    Point center = new Point(orgImg.width()/2, orgImg.height()/2);
		    Mat rotImage = Imgproc.getRotationMatrix2D(center, rotatedRect.angle, 1.0);
		    
		    //1.0 means 100 % scale
		    Size size = new Size(orgImg.width(), orgImg.height());
		    Imgproc.warpAffine(orgImg, orgImg, rotImage, size, Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS);
		}catch(Exception e) { 
			//기울기 보정에서 에러 발생 시 에러 처리
	    	int errorCode = -101;
			//errorLog.setExceptionMesseage(errorProps.getProperty(String.valueOf(errorCode)));
			errorLog.setExceptionCode(errorCode);
			errorLog.setExceptionErrors(errorLog.getConvertId());
			errorLog.setRequestMethod(getClass().getEnclosingMethod().getName());
			errorLog.setErrorMat(orgImg);

			return false;
		}
		return true;
	}

	
	/**
	 * 테두리 인식과 기울기 보정을 마친 이미지 경로값을 이용하여 해당 이미지의 해상도를 글자 검출을 하기위한 디폴트 해상도로 변경한다. 
	 * @throws Exception 
	 */
	public boolean resizeImg(Mat orgImg ,int x ,int y, StatusLogVO errorLog) throws Exception {
		try {
			
		    //리사이즈 및 해상도 찾기 
		    Imgproc.resize( orgImg, orgImg, new Size(x,y) );
			
		}catch(Exception e) { 
			
			//리사이즈 에서 에러 발생 시 에러 처리
	    	int errorCode = -102;
			//errorLog.setExceptionMesseage(errorProps.getProperty(String.valueOf(errorCode)));
			errorLog.setExceptionCode(errorCode);
			errorLog.setExceptionErrors(errorLog.getConvertId());
			errorLog.setRequestMethod(getClass().getEnclosingMethod().getName());
			errorLog.setErrorMat(orgImg);
			return false;
		}
		return true;
	}
	
	
	/**
	 * 해상도 변경된 이미지를 opencv로 이용하여 분석한다.
	 * @param  List<Map<String, String>> workList
	 * @return List<Map<String, String>>
	 * @throws Exception 
	 */
	public boolean analysisOpencv(Mat orgImg, JSONArray categoryItemMatchList, JSONArray opencvImgConvertList, StatusLogVO errorLog) {

		// 1. JSONArray를 이용하여 for문을 사용한다. 모델별 분류 로우값의 좌표값을 이용하여 이미지 파일을 자르고, Mat 함수에 저장한다. 
		// 		   이때 자르는 순서는 모델별 분류 테이블 결과값의 좌표 순서에 따라 정해진다. 
		
		JSONObject categotyObj = new JSONObject();
		JSONArray ItemObjArr = new JSONArray();
		
		
		for (int i=0; i<categoryItemMatchList.size(); i++) {
			//분류 Obj
			categotyObj = ( JSONObject ) categoryItemMatchList.get(i);
			
			// 이미지 잘라내기 x y w h
			long x = (long) categotyObj.get("BCA_PROP_X");
			long y = (long) categotyObj.get("BCA_PROP_Y");
			long w = (long) categotyObj.get("BCA_PROP_WIDTH");
			long h = (long) categotyObj.get("BCA_PROP_HEIGHT");
			
		    Rect rectCropWeight = new Rect((int) x,(int) y,(int) w,(int) h);
//		    Rect rectCropWeight = new Rect((int)categotyObj.get("BCA_PROP_Y"), (int) categotyObj.get("BCA_PROP_X"), (int) categotyObj.get("BCA_PROP_WIDTH"), (int) categotyObj.get("BCA_PROP_HEIGHT"));
		    Mat cropedImg = new Mat(orgImg, rectCropWeight);


		    Mat grayDest = new Mat();
	        Imgproc.cvtColor(cropedImg, grayDest, Imgproc.COLOR_RGB2GRAY);

	        Mat morphGrident = new Mat();
	        Mat kernel = new Mat(1,3, CvType.CV_8UC1); //TODO 이값을 조정하면서 테스트해야함.
	        Imgproc.morphologyEx(grayDest, morphGrident, Imgproc.MORPH_GRADIENT, kernel);

	        Mat gradThresh = new Mat();  //흑백전환을 위한 매트릭스
	        Imgproc.adaptiveThreshold(morphGrident, gradThresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 12);  
		    
		    Mat hierarchy = new Mat();    //윤곽선 찾기(finding constour)를 위한 매트릭스 
	        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	        
			// 2. 잘려진 분류 이미지 파일마다 contour 작업을 실행함. 
		    Imgproc.findContours(gradThresh, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
		    
		    int index=0;

			//항목리스트 Obj
		    ItemObjArr = (JSONArray) categotyObj.get("BCA_ITEM_ARRAY");
		    int ItemCnt = ItemObjArr.size();

	        // 3. 검출된 글자 결과값들이 분류에 따른 항목수보다 많을 시 에러처리 - 이미지 글자 검출중 잘못된 검출을 함.
		    if(ItemCnt !=contours.size()) {

		    	int errorCode = -104;
				//errorLog.setExceptionMesseage(errorProps.getProperty(String.valueOf(errorCode)));
				errorLog.setExceptionCode(errorCode);
				errorLog.setExceptionErrors(errorLog.getConvertId());
//				errorLog.setRequestMethod(getClass().getEnclosingMethod().getName());
				errorLog.setErrorMat(cropedImg);

				return false;
		    }
	        if(contours.size()>0) {
	            for(int idx = 0; idx < contours.size(); idx++) {

	                Rect rect = Imgproc.boundingRect(contours.get(idx));
      		      	//윤곽찾기 결과확인용 시작
	            	Imgproc.rectangle(cropedImg, new Point(rect.br().x - rect.width, rect.br().y - rect.height)
	                        , rect.br()
	                        , new Scalar(0, 255, 0), 1);
                	//윤곽찾기 결과확잉뇽 끝
	            	
	        		// 4. 각각의 검출된 contour값의 좌표를 이용하여 이미지를 잘라내어 글자 영역 이미지로 저장한다. 
	        		//		  이때 자른 글자 영역 이미지 파일을 파일서버\work\변환ID 폴더에 저장한다. (ex. opencv_분류명_항목명.jpg 형태로 저장)
	            	
	                if (rect.height > 5 && rect.width > 30   
	                		//&&!(rect.width >= 512 - 5 && rect.height >= 512 - 5)
	                		){
	                	//이미지 잘라내서 저장하기
	                	Mat croped = new Mat(cropedImg, rect);

	        			String storePathString = configProps.getProperty("imgWorkPath");
	        			String subPath = File.separator + StringUtil.getTimeStamp().substring(0,6) + File.separator + errorLog.getConvertId();

	        			File saveFolder = new File(storePathString+subPath);

	        			if (!saveFolder.exists() || saveFolder.isFile()) {
	        				saveFolder.mkdirs();
	        			}
	        			//TODO 작업중폴더\\년도달\\변환ID\\변수이름.jpg형태로 만듬 추후에 변경 가능
	        		    Imgcodecs.imwrite(storePathString+subPath+File.separator+categotyObj.get("codeNm")+".jpg",croped); //0.이미지 잘라내기
	                }
	            }
	        }
	        
			
		}
		
		
		// 5. 이미지 저장한 경로를 JSONArray로 반환한다. 
				
		return true;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

























	public String sizeww( String filePath ) throws Exception {
    	Image img = new ImageIcon(filePath).getImage();
    	System.out.println( img.getWidth(null) + "," +img.getHeight(null) );
    	String res =  img.getWidth(null) + "," +img.getHeight(null);
    	return res;
    }
    

    
    public String findPaper( String filePath ) throws Exception {

	    Mat cvImg = Imgcodecs.imread(filePath);

        Mat grayDest = new Mat();
        Imgproc.cvtColor(cvImg, grayDest, Imgproc.COLOR_RGB2GRAY);

        Mat morphGrident = new Mat();
        Mat kernel = new Mat(1,3, CvType.CV_8UC1); //TODO 이값을 조정하면서 테스트해야함.
        Imgproc.morphologyEx(grayDest, morphGrident, Imgproc.MORPH_GRADIENT, kernel);

        Mat gradThresh = new Mat();  //흑백전환을 위한 매트릭스
        Mat hierarchy = new Mat();    //윤곽선 찾기(finding constour)를 위한 매트릭스 
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.adaptiveThreshold(morphGrident, gradThresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 12);  
    	
        List<String> fileList = new ArrayList<String>();
        Imgproc.findContours(gradThresh, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        int index=0;
        if(contours.size()>0) {
            for(int idx = 0; idx < contours.size(); idx++) {
                Rect rect = Imgproc.boundingRect(contours.get(idx));
                if (rect.height > 5 && rect.width > 30   
                		//&&!(rect.width >= 512 - 5 && rect.height >= 512 - 5)
                		){
                	//이미지 잘라내서 저장하기
                	Mat croped = new Mat(cvImg, rect);
                	String file = "C:\\img_test\\new\\finished_"+index+".png";
                	fileList.add(file); //tessreact에서 경로들을 반복하면서 체크하기 위함
      		      	Imgcodecs.imwrite(file,croped);
      		      	index++;
      		      	//윤곽찾기 결과확인용 시작
                	Imgproc.rectangle(cvImg, new Point(rect.br().x - rect.width, rect.br().y - rect.height)
                            , rect.br()
                            , new Scalar(0, 255, 0), 1);
                	//윤곽찾기 결과확잉뇽 끝
                }

            }
        }
    	//과정 저장
	    try{
		      Imgcodecs.imwrite("C:\\img_test\\paper\\1.gray.png",grayDest); //1.그레이스케일
		      Imgcodecs.imwrite("C:\\img_test\\paper\\2.morphGrident.png",morphGrident); //2.모핑 그라디언트
		      Imgcodecs.imwrite("C:\\img_test\\paper\\3.gradThresh.png", gradThresh); //3. 임계값 흑백전환
		      Imgcodecs.imwrite("C:\\img_test\\paper\\4.doc_contour.png", cvImg); //4. 윤곡 찾기
	    }catch(Exception e){
	      System.out.println(e);
	    }
    	return "suc";
    }
    
//    public List<String> givenTessBaseApi_whenImageOcrd_thenTextDisplayed( String filePath ) throws Exception {
   	public List<String> givenTessBaseApi_whenImageOcrd_thenTextDisplayed( String filePath,int x, int y ) throws Exception {
	    System.out.println("java.library.path : "+System.getProperty("java.library.path")); 
	    System.out.println("filePath : "+filePath); 
	    // 이미지 읽기(openCV)
	    System.out.println("===0.이미지 읽기 및 특정부분 잘라내기 시작===");
	    long start = System.currentTimeMillis();
//	    Mat cvImg = Imgcodecs.imread(filePath);
	    Mat cvImg = Imgcodecs.imread(filePath);
//	    Mat cvImg = Imgcodecs.imread("C:\\img_test\\result1.png");
	    // 이미지 잘라내기
	    Rect rectCropWeight = new Rect(175, 420, 465, 300);
	    Mat cropedImg1 = new Mat(cvImg, rectCropWeight);
	    long end = System.currentTimeMillis();
	    System.out.println( "실행 시간 : " + ( end - start )/1000.00000 );
	    System.out.println("===0.이미지 읽기 및 특정부분 잘라내기 끝===");
	    System.out.println("\r");
	    
	    
	    //1. grayscale (회색으로 바꾸는것) 적용
	    System.out.println("===1. grayscale 적용 시작===");
        Mat grayDest = new Mat();
        Imgproc.cvtColor(cropedImg1, grayDest, Imgproc.COLOR_RGB2GRAY);
	    System.out.println( "실행 시간 : " + ( end - start )/1000.00000 );
	    System.out.println("===1. grayscale 적용 끝===");
	    System.out.println("\r");
        
        
        //2. 모핑 그라디언트 적용 (경계를 흐리게한다.)
	    //kernel의 x,y값에 따라 경계선 블러 및 그라디언트가 적용된다.
	    System.out.println("===2. 모핑 그라디언트 적용  시작===");
	    start = System.currentTimeMillis();
        Mat morphGrident = new Mat();
        Mat kernel = new Mat(1,3, CvType.CV_8UC1); //TODO 이값을 조정하면서 테스트해야함.
        Imgproc.morphologyEx(grayDest, morphGrident, Imgproc.MORPH_GRADIENT, kernel);
	    end = System.currentTimeMillis();
	    System.out.println( "실행 시간 : " + ( end - start )/1000.00000 );
	    System.out.println("===2. 모핑 그라디언트 적용 끝===");
	    System.out.println("\r");
	    
	    /*
	    //가우시안 블러 적용
        Mat gauDestination = new Mat(destination.rows(),destination.cols(),destination.type());
        Imgproc.GaussianBlur(destination, gauDestination,new Size(3,3), 0);
        //윤곽선 검출
        Mat sobelDestination = new Mat(gauDestination.rows(),gauDestination.cols(),gauDestination.type());
        Imgproc.Sobel(gauDestination, sobelDestination, -1, 1, 0);
        //임계값 흑백전환
        Mat imgAdaptiveThreshold = new Mat(sobelDestination.rows(),sobelDestination.cols(),sobelDestination.type());
        Imgproc.adaptiveThreshold(sobelDestination,imgAdaptiveThreshold,255,0,0,99,4);
        */
	    
        //3. 임계값 흑백전환 - AdaptiveThreshold
	    System.out.println("===3. 임계값 흑백전환 시작===");
	    start = System.currentTimeMillis();
        Mat gradThresh = new Mat();  //흑백전환을 위한 매트릭스
        Mat hierarchy = new Mat();    //윤곽선 찾기(finding constour)를 위한 매트릭스 
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Imgproc.adaptiveThreshold(morphGrident, gradThresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 12);  
        Imgproc.adaptiveThreshold(morphGrident, gradThresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, x, y);  
        //TODO 인접픽셀 블럭사이즈를 3, 가중치를 12, 수치변경하면서 테스트 필요
        //인접 픽셀 블록사이즈 = 홀수만 가능 사이즈가 클 수록 더 블록이 또렷해진다
        //가중치는 클수록 연해진다.
	    end = System.currentTimeMillis();
	    System.out.println( "실행 시간 : " + ( end - start )/1000.00000 );
	    System.out.println("===3. 임계값 흑백전환 끝===");
	    System.out.println("\r");
	    
        /*
        //morphclose
        Mat morphClose = new Mat();
        Mat kernel2 = new Mat(new Size(50,25), CvType.CV_8UC1, new Scalar(255));
        Imgproc.morphologyEx(gradThresh, morphClose, Imgproc.MORPH_CLOSE, kernel2);
        */
        
	    /*
        //4.길게 늘어지는 라인 제거
        //라인을 찾아서 검게 칠해버린다.
	    System.out.println("===5.길게 늘어지는 라인 제거 시작===");
	    start = System.currentTimeMillis();
        Mat removeLines = gradThresh.clone();
        int limit = 10;
        Mat lines=new Mat();
        int threshold = 100; //선 추출 정확도
        int minLength = 80; //추출할 선의 길이
        int lineGap = 5; //5픽셀 이내로 겹치는 선은 제외
        int rho = 1;  
        Imgproc.HoughLinesP(removeLines, lines, rho, Math.PI/180, threshold, minLength, lineGap);
        for (int i = 0; i < lines.total(); i++) {
            double[] vec=lines.get(i,0);
            Point pt1, pt2;
            pt1=new Point(vec[0],vec[1]);
            pt2=new Point(vec[2],vec[3]);
            double gapY = Math.abs(vec[3]-vec[1]);
            double gapX = Math.abs(vec[2]-vec[0]);
            if(gapX>limit && limit>0) {
                //라인을 찾아서 검게 칠해버린다.
                Imgproc.line(removeLines, pt1, pt2, new Scalar(0, 0, 0), 10);
            }
        }
	    end = System.currentTimeMillis();
	    System.out.println( "실행 시간 : " + ( end - start )/1000.00000 );
	    System.out.println("===5.길게 늘어지는 라인 제거 끝===");
	    System.out.println("\r");
        */
        
        //4.윤곽 찾기 finding Contours
        //4번의 결과로, 위 과정을 거쳐 최대한 이미지 상의 픽셀들이 적절하게 뭉쳐져있으며, 불필요한 픽셀들은 제거되었음
        //윤곽 찾기로 적절하게 뭉쳐진 픽셀의 윤곽을 찾아 잘라낸다.
	    System.out.println("===4.윤곽 찾기 시작===");
	    start = System.currentTimeMillis();
        List<String> fileList = new ArrayList<String>();
        Imgproc.findContours(gradThresh, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        int index=0;
        if(contours.size()>0) {
            for(int idx = 0; idx < contours.size(); idx++) {
                Rect rect = Imgproc.boundingRect(contours.get(idx));
                if (rect.height > 5 && rect.width > 30   
                		//&&!(rect.width >= 512 - 5 && rect.height >= 512 - 5)
                		){
                	//이미지 잘라내서 저장하기
                	Mat croped = new Mat(cropedImg1, rect);
                	String file = "C:\\img_test\\new\\finished_"+index+".png";
                	fileList.add(file); //tessreact에서 경로들을 반복하면서 체크하기 위함
      		      	Imgcodecs.imwrite(file,croped);
      		      	index++;
      		      	//윤곽찾기 결과확인용 시작
                	Imgproc.rectangle(cropedImg1, new Point(rect.br().x - rect.width, rect.br().y - rect.height)
                            , rect.br()
                            , new Scalar(0, 255, 0), 1);
                	//윤곽찾기 결과확잉뇽 끝
                }

            }
        }
	    end = System.currentTimeMillis();
	    System.out.println( "실행 시간 : " + ( end - start )/1000.00000 );
	    System.out.println("===4.윤곽 찾기 끝===");
	    System.out.println("\r");
        
	    //과정 저장
	    try{
		      Imgcodecs.imwrite("C:\\img_test\\new\\0.cropedImg1.png",cropedImg1); //0.이미지 잘라내기
		      Imgcodecs.imwrite("C:\\img_test\\new\\1.gray.png",grayDest); //1.그레이스케일
		      Imgcodecs.imwrite("C:\\img_test\\new\\2.morphGrident.png",morphGrident); //2.모핑 그라디언트
		      Imgcodecs.imwrite("C:\\img_test\\new\\3.gradThresh.png", gradThresh); //3. 임계값 흑백전환
		      Imgcodecs.imwrite("C:\\img_test\\new\\4.doc_contour.png", cvImg); //4. 윤곡 찾기
	    }catch(Exception e){
	      System.out.println(e);
	    }
	    
	    return fileList;
    }

}