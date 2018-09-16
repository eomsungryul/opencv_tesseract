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

public class findContours {

	public static void main(String[] args) throws Exception {
		// 1. system load
	    System.load( "C:\\opencv_java341.dll");
	    

	    Mat cropedImg = Imgcodecs.imread("C:\\img_test\\img_test.jpg");
//	    Mat cropedImg = Imgcodecs.imread("C:\\img_test\\test2.png");
	    
//	    Mat resizeimage = resizeImg(cropedImg, 1752,542);
	    contourImg(cropedImg);
//	    String filePath = cropImg("C:\\img_test\\test.jpg");
//	    String filePath = cropImg("C:\\img_test\\test2.png");
	    System.out.println("comp"); 
		
	}

	public static Mat resizeImg(Mat orgImg,int x,int y) throws Exception {
	    System.out.println("resizeImg"); 
	    Mat resizeimage = new Mat();
	    Size sz = new Size(x,y);
	    //리사이즈 및 해상도 찾기 
	    Imgproc.resize( orgImg, resizeimage, sz );
	    System.out.println("resize"+resizeimage.cols() + " : " +resizeimage.height());
    	//과정 저장
		Imgcodecs.imwrite("C:\\img_test\\work\\1.resizeimage.png",resizeimage); //1.그레이스케일
		return resizeimage;
	}
	

	private static String contourImg(Mat cropedImg) {
	    
	    Mat grayDest = new Mat();
        Imgproc.cvtColor(cropedImg, grayDest, Imgproc.COLOR_RGB2GRAY);

        Mat morphGrident = new Mat();
        Mat kernel = new Mat(1,3, CvType.CV_8UC1); //TODO 이값을 조정하면서 테스트해야함.
        Imgproc.morphologyEx(grayDest, morphGrident, Imgproc.MORPH_GRADIENT, kernel);
	    Imgcodecs.imwrite("C:\\img_test\\work\\2.morphGrident.png",morphGrident); //2.모핑 그라디언트

        Mat gradThresh = new Mat();  //흑백전환을 위한 매트릭스
//	    Imgproc.threshold(morphGrident, gradThresh,200,255, Imgproc.THRESH_BINARY); 
        Imgproc.adaptiveThreshold(morphGrident, gradThresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 12);  
	      Imgcodecs.imwrite("C:\\img_test\\work\\3.gradThresh.png", gradThresh); //3. 임계값 흑백전환
	    
	    Mat hierarchy = new Mat();    //윤곽선 찾기(finding constour)를 위한 매트릭스 
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Imgproc.findContours(gradThresh, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
	    int index=0;
        if(contours.size()>0) {
            for(int idx = 0; idx < contours.size(); idx++) {

                Rect rect = Imgproc.boundingRect(contours.get(idx));
  		      	//윤곽찾기 결과확인용 시작
            	Imgproc.rectangle(cropedImg, new Point(rect.br().x - rect.width, rect.br().y - rect.height)
                        , rect.br()
                        , new Scalar(0, 255, 0), 1);
            	//윤곽찾기 결과확잉뇽 끝
                if (rect.height > 100 && rect.width > 50   
                		//&&!(rect.width >= 512 - 5 && rect.height >= 512 - 5)
                		){
                	//이미지 잘라내서 저장하기
                	Mat croped = new Mat(cropedImg, rect);
                	String file = "C:\\img_test\\work\\finished_"+index+".png";
      		      	Imgcodecs.imwrite(file,croped);
                }
            }
        }

	    //과정 저장
	    try{
		     Imgcodecs.imwrite( "C:\\img_test\\work\\cropedImg1.png", cropedImg );
		      Imgcodecs.imwrite("C:\\img_test\\work\\1.gray.png",grayDest); //1.그레이스케일
		      //Imgcodecs.imwrite("C:\\img_test\\test\\GaussianBlur.png",gauDestination);
		      //Imgcodecs.imwrite("C:\\img_test\\test\\sobelDestination.png",sobelDestination);
		      //Imgcodecs.imwrite("C:\\img_test\\test\\morphClose.png", morphClose);
		      //Imgcodecs.imwrite("C:\\img_test\\test\\5.removeLines.png", removeLines); //4. line remove
		      	
	    }catch(Exception e){
	      System.out.println(e);
	    }
		
		return null;
	}


	
	
	
}
