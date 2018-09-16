package com.tessModule.tess;

import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept.PIX;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class opencvTest {
	
	
	public static void givenTessBaseApi_whenImageOcrd_thenTextDisplayed( int x ,int y) throws Exception {
	    System.out.println(System.getProperty("java.library.path")); 
	    System.load( "C:\\_app\\workspace\\BasicTesseractExample\\opencv_lib\\x64\\opencv_java341.dll");
	    // 이미지 읽기(openCV)
	    System.out.println("===0.이미지 읽기 및 특정부분 잘라내기 시작===");
	    long start = System.currentTimeMillis();
	    Mat cvImg = Imgcodecs.imread("C:\\img_test\\result1.png");
	    // 이미지 잘라내기 x y w h
	    Rect rectCropWeight = new Rect(175, 420, 465, 300);
	    Mat cropedImg1 = new Mat(cvImg, rectCropWeight);

	    long end = System.currentTimeMillis();
	    System.out.println( "실행 시간 : " + ( end - start )/1000.00000 );
	    System.out.println("===0.이미지 읽기 및 특정부분 잘라내기 끝===");
	    System.out.println("\r");
	    
	    
	    //1. grayscale 적용
	    System.out.println("===1. grayscale 적용 시작===");
        Mat grayDest = new Mat();
        Imgproc.cvtColor(cropedImg1, grayDest, Imgproc.COLOR_RGB2GRAY);
	    System.out.println( "실행 시간 : " + ( end - start )/1000.00000 );
	    System.out.println("===1. grayscale 적용 끝===");
	    System.out.println("\r");
        
        
        //2. 모핑 그라디언트 적용
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
        Imgproc.adaptiveThreshold(morphGrident, gradThresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, x, y);  //TODO 인접픽셀 블럭사이즈를 3, 가중치를 12, 수치변경하면서 테스트 필요

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
        
        //javadoc: findContours(image, contours, hierarchy, mode, method, offset)
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
		      //Imgcodecs.imwrite("C:\\img_test\\new\\GaussianBlur.png",gauDestination);
		      //Imgcodecs.imwrite("C:\\img_test\\new\\sobelDestination.png",sobelDestination);
		      Imgcodecs.imwrite("C:\\img_test\\new\\3.gradThresh.png", gradThresh); //3. 임계값 흑백전환
		      //Imgcodecs.imwrite("C:\\img_test\\new\\morphClose.png", morphClose);
		      //Imgcodecs.imwrite("C:\\img_test\\new\\5.removeLines.png", removeLines); //4. line remove
		      Imgcodecs.imwrite("C:\\img_test\\new\\4.doc_contour.png", cvImg); //4. 윤곡 찾기
		      	
	    }catch(Exception e){
	      System.out.println(e);
	    }
	    
	    
	    //5. tessreact로 텍스트 분석
	    System.out.println("===5. tessreact로 텍스트 분석 시작===");
	    start = System.currentTimeMillis();
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
//	        assertTrue(!string.isEmpty());
	        System.out.println("OCR output:" + string);
	        // Destroy used object and release memory
	        api.End();
	        outText.deallocate();
	        pixDestroy(image);
        }
	    end = System.currentTimeMillis();
	    System.out.println( "실행 시간 : " + ( end - start )/1000.00000 );
	    System.out.println("===5. tessreact로 텍스트 분석 끝===");
	    System.out.println("\r");
    }
	public static void main(String[] args) throws Exception {
		
		opencvTest.givenTessBaseApi_whenImageOcrd_thenTextDisplayed(3,3);
		
	}
}
