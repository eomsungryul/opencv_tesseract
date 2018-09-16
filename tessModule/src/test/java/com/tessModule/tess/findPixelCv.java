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

public class findPixelCv {
	
//	def get_holes(image, thresh):
//	    gray = cv.cvtColor(image, cv.COLOR_BGR2GRAY)
//
//	    im_bw = cv.threshold(gray, thresh, 255, cv.THRESH_BINARY)[1]
//	    im_bw_inv = cv.bitwise_not(im_bw)
//
//	    contour, _ = cv.findContours(im_bw_inv, cv.RETR_CCOMP, cv.CHAIN_APPROX_SIMPLE)
//	    for cnt in contour:
//	        cv.drawContours(im_bw_inv, [cnt], 0, 255, -1)
//
//	    nt = cv.bitwise_not(im_bw)
//	    im_bw_inv = cv.bitwise_or(im_bw_inv, nt)
//	    return im_bw_inv
//
//
//	def remove_background(image, thresh, scale_factor=.25, kernel_range=range(1, 15), border=None):
//	    border = border or kernel_range[-1]
//
//	    holes = get_holes(image, thresh)
//	    small = cv.resize(holes, None, fx=scale_factor, fy=scale_factor)
//	    bordered = cv.copyMakeBorder(small, border, border, border, border, cv.BORDER_CONSTANT)
//
//	    for i in kernel_range:
//	        kernel = cv.getStructuringElement(cv.MORPH_ELLIPSE, (2*i+1, 2*i+1))
//	        bordered = cv.morphologyEx(bordered, cv.MORPH_CLOSE, kernel)
//
//	    unbordered = bordered[border: -border, border: -border]
//	    mask = cv.resize(unbordered, (image.shape[1], image.shape[0]))
//	    fg = cv.bitwise_and(image, image, mask=mask)
//	    return fg
//
//
//	img = cv.imread('koAl2.jpg')
//	nb_img = remove_background(img, 230)	
	

	public static void test(String filePath) throws Exception {
	    System.out.println(System.getProperty("java.library.path")); 
	    System.load( "C:\\_app\\spring-tool-suite-3.9.4.RELEASE-e4.7.3a-win32-x86_64\\workspace\\git\\tessModule\\opencv_lib\\x64\\opencv_java341.dll");
	    // 이미지 읽기(openCV)
	    long start = System.currentTimeMillis();
	    Mat cvImg = Imgcodecs.imread(filePath);
	    
	    int Totalintensity = 0;
//	    for(int i = 0; i < cvImg.rows(); i++) {
//	    	for(int j = 0; j< cvImg.cols(); j++) {
//	    		
//	    	}
//	    }

		double[] rgb = cvImg.get(0, 0);
		System.out.println("red:"+rgb[0]+"green:"+rgb[1]+"blue:"+rgb[2]);
		cvImg.put(0, 0, new double[]{255, 255, 0});//sets the pixel to yellow

	      Imgcodecs.imwrite("C:\\img_test\\img77.jpg",cvImg); //1.그레이스케일
	    System.out.println("Totalintensity :)"+Totalintensity);
//        Mat out = new Mat(floodFill|floodFill_inv);  //흑백전환을 위한 매트릭스
	    
    	//과정 저장
        
	}
	

	public static void main(String[] args) throws Exception {
		// 1. system load
	    System.load( "C:\\_app\\spring-tool-suite-3.9.4.RELEASE-e4.7.3a-win32-x86_64\\workspace\\git\\tessModule\\opencv_lib\\x64\\opencv_java341.dll");
	    
	    test("C:\\img_test\\img7.jpg");
	    System.out.println("comp");
		
	}
	
	
	


	
	
	
}
