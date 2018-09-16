package com.tessModule.tess;

import java.util.ArrayList;
import java.util.List;

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

import jdk.nashorn.internal.codegen.types.BitwiseType;

public class backgroundRemoveCv2 {
	

	public static void test(String filePath) throws Exception {
	    System.out.println(System.getProperty("java.library.path")); 
	    System.load( "C:\\opencv_java341.dll");
	    // 이미지 읽기(openCV)
	    long start = System.currentTimeMillis();
	    Mat cvImg = Imgcodecs.imread(filePath);

	    Mat grayDest = new Mat();
        Imgproc.cvtColor(cvImg, grayDest, Imgproc.COLOR_RGB2GRAY);

	    Mat th = new Mat();
        Imgproc.threshold(grayDest, th,200,255, Imgproc.THRESH_BINARY_INV);

        
//        Mat mask =  new Mat(cvImg.size(),CvType.CV_8UC1);
        Mat mask =  new Mat();
        Mat floodFill = th.clone();
        Imgproc.floodFill(floodFill, mask, new Point(0,0), new Scalar(0,255,0));

        Mat floodFill_inv = new Mat();  //흑백전환을 위한 매트릭스
        Core.bitwise_not(floodFill, floodFill_inv); 
        

//        Mat out = new Mat(floodFill|floodFill_inv);  //흑백전환을 위한 매트릭스
	    
    	//과정 저장
	    try{
		      Imgcodecs.imwrite("C:\\img_test\\backgroundTest\\1.gray.png",grayDest); //1.그레이스케일
		      Imgcodecs.imwrite("C:\\img_test\\backgroundTest\\2.threshold.png",th); //2.모핑 그라디언트
		      Imgcodecs.imwrite("C:\\img_test\\backgroundTest\\3.floodFill.png",floodFill); //2.모핑 그라디언트
		      Imgcodecs.imwrite("C:\\img_test\\backgroundTest\\4.floodFill_inv.png", floodFill_inv); //3. 임계값 흑백전환
	    }catch(Exception e){
	      System.out.println(e);
	    }
        
	}
	

	public static void main(String[] args) throws Exception {
		// 1. system load
//	    System.load( "C:\\_app\\spring-tool-suite-3.9.4.RELEASE-e4.7.3a-win32-x86_64\\workspace\\git\\tessModule\\opencv_lib\\x64\\opencv_java341.dll");
	    
//	    String filePath = reviseGradient("C:\\img_test\\img8.jpg");
//	    String filePath2 = recognEdge(filePath);
	    test("C:\\img_test\\img_test.jpg");
	    System.out.println("comp");
		
	}
	
	
	


	
	
	
}
