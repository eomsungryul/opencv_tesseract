package com.tessModule.tess;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.plaf.synth.SynthSeparatorUI;

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

public class detectRect_BK_20180627_1637 {
	

	public static void test( int x ,int y) throws Exception {
	    System.out.println(System.getProperty("java.library.path")); 
	    System.load( "C:\\_app\\spring-tool-suite-3.9.4.RELEASE-e4.7.3a-win32-x86_64\\workspace\\git\\tessModule\\opencv_lib\\x64\\opencv_java341.dll");
	    // 이미지 읽기(openCV)
	    long start = System.currentTimeMillis();
	    Mat cvImg = Imgcodecs.imread("C:\\img_test\\img_test2.jpg");

	    Mat grayDest = new Mat();
        Imgproc.cvtColor(cvImg, grayDest, Imgproc.COLOR_RGB2GRAY);

        Mat morphGrident = new Mat();
        Mat kernel = new Mat(1,3, CvType.CV_8UC1); //TODO 이값을 조정하면서 테스트해야함.
        Imgproc.morphologyEx(grayDest, morphGrident, Imgproc.MORPH_GRADIENT, kernel);

        Mat gradThresh = new Mat();  //흑백전환을 위한 매트릭스
        Imgproc.adaptiveThreshold(morphGrident, gradThresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 12);  
        
	    Mat canny = new Mat();
	    Imgproc.Canny(gradThresh, canny, x, y);
	    
	    Mat hierarchy = new Mat();    //윤곽선 찾기(finding constour)를 위한 매트릭스 
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Imgproc.findContours(canny, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
	    int index=0;
        if(contours.size()>0) {
            for(int idx = 0; idx < contours.size(); idx++) {
                Rect rect = Imgproc.boundingRect(contours.get(idx));
                if (rect.height > 1500 && rect.width > 1500   
                		//&&!(rect.width >= 512 - 5 && rect.height >= 512 - 5)
                		){
      		      	//윤곽찾기 결과확인용 시작

                	//이미지 잘라내서 저장하기
                	Mat croped = new Mat(cvImg, rect);
                	String file = "C:\\img_test\\new\\finished_"+index+".png";
      		      	Imgcodecs.imwrite(file,croped);
                	Imgproc.rectangle(cvImg, new Point(rect.br().x - rect.width, rect.br().y - rect.height)
                            , rect.br()
                            , new Scalar(0, 255, 0), 1);
                	//윤곽찾기 결과확잉뇽 끝
                }

            }
        }
	    
    	//과정 저장
	    try{

		      Imgcodecs.imwrite("C:\\img_test\\work\\1.gray.png",grayDest); //1.그레이스케일
		      Imgcodecs.imwrite("C:\\img_test\\work\\2.morphGrident.png",morphGrident); //2.모핑 그라디언트
		      Imgcodecs.imwrite("C:\\img_test\\work\\3.gradThresh.png", gradThresh); //3. 임계값 흑백전환
		      Imgcodecs.imwrite("C:\\img_test\\work\\canny.png",canny); //1.canny
		      Imgcodecs.imwrite("C:\\img_test\\work\\cvImg.png",cvImg); //2.cvImg
	    }catch(Exception e){
	      System.out.println(e);
	    }
        
	}
	
	
	
	
	


	public static void resizeImg(Mat orgImg,int x,int y) throws Exception {
	    System.out.println("resizeImg"); 
	    Mat resizeimage = new Mat();
	    Size sz = new Size(x,y);
	    //리사이즈 및 해상도 찾기 
	    Imgproc.resize( orgImg, resizeimage, sz );
	    System.out.println("resize"+resizeimage.cols() + " : " +resizeimage.height());
    	//과정 저장
		Imgcodecs.imwrite("C:\\img_test\\work\\1.resizeimage.png",resizeimage); //1.그레이스케일
	}
	
	
	public static Mat grayDest(Mat cvImg) throws Exception {
	    System.out.println("grayDest"); 
	    Mat grayDest = new Mat();
        Imgproc.cvtColor(cvImg, grayDest, Imgproc.COLOR_RGB2GRAY);
    	//과정 저장
		Imgcodecs.imwrite("C:\\img_test\\work\\2.gray.png",grayDest); //1.그레이스케일
		return grayDest;
    }
	

	public static Mat gaussianBlur(Mat cvImg,int sizeX, int sizeY,int sigmaX ) throws Exception {
	    long start = System.currentTimeMillis();
	    Mat gsBlr = new Mat();
	    Size sz = new Size(sizeX,sizeY);
	    Imgproc.GaussianBlur(cvImg, gsBlr, sz, sigmaX);
    	//과정 저장
	    long end = System.currentTimeMillis();

	    System.out.println( "gaussianBlur_x_"+sizeX+"y_"+sizeY+"sgmx_"+sigmaX+" - 실행 시간 : " + ( end - start )/1000.00000 );
		Imgcodecs.imwrite("C:\\img_test\\work\\gsBlr_"+"x_"+sizeX+"y_"+sizeY+"sgmx_"+sigmaX+".png",gsBlr); // 가우시간 블러
		
		return gsBlr;
    }
	

	private static Mat cannyImg(Mat cvImg, int i, int j) {
	    long start = System.currentTimeMillis();
	    Mat cannyImg = new Mat();
	    Imgproc.Canny(cvImg, cannyImg, i, j);
    	//과정 저장
	    long end = System.currentTimeMillis();

	    System.out.println( "cannyImg_x_"+i+"y_"+j+" - 실행 시간 : " + ( end - start )/1000.00000 );
		Imgcodecs.imwrite("C:\\img_test\\work\\cannyImg_"+"x_"+i+"y_"+j+".png",cannyImg); // 캐니
		return cannyImg;
	}
	
	


	public static String recognEdge(String filePath) throws Exception {
		
	    // 이미지 읽기(openCV)
	    Mat cvImg = Imgcodecs.imread(filePath);
	    
	    System.out.println("cvImg"+cvImg.cols()+" : "+cvImg.height());

	    // 2. 해상도 맞추기 
//		resizeImg(3024,4032);
		// 3. 흑백
	    Mat grayImg = grayDest(cvImg);
	    // 4. 가우시안블러 
	    // sigmaX 값을 늘릴 수록 더 흐려짐 시간은 거의 비슷  사이즈는 클수록 오래 걸리고 흐려짐
	    Mat blurImg = gaussianBlur(grayImg,11,11,0);

	    // 5. canny 
	    // j값을 높이면 점점 흐려진다 
	    Mat canntImg = cannyImg(blurImg,100,200);

	    Mat hierarchy = new Mat();    //윤곽선 찾기(finding constour)를 위한 매트릭스 
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Imgproc.findContours(canntImg, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
	    Imgcodecs.imwrite("C:\\img_test\\work\\contours.png",canntImg); //contours
	    
	    if(contours.size()>0) {

            for(int idx = 0; idx < contours.size(); idx++) {
                Rect rect = Imgproc.boundingRect(contours.get(idx));
            	Imgproc.drawContours(cvImg,contours, idx, new Scalar(0, 255, 0), 5);
                if (rect.height > 1500 && rect.width > 1500   
                		//&&!(rect.width >= 512 - 5 && rect.height >= 512 - 5)
                		){
      		      	//윤곽찾기 결과확인용 시작
//                	Imgproc.get
                	//이미지 잘라내서 저장하기
                	Mat croped = new Mat(cvImg, rect);
                	String file = "C:\\img_test\\work\\finished_"+idx+".png";
      		      	Imgcodecs.imwrite(file,croped);
                	//윤곽찾기 결과확잉뇽 끝
                	
                }

            }
        }
	      Imgcodecs.imwrite("C:\\img_test\\work\\comp.png",canntImg); //contours
	      Imgcodecs.imwrite("C:\\img_test\\work\\0.grayImg.png",grayImg); //0.이미지 잘라내기
	      Imgcodecs.imwrite("C:\\img_test\\work\\1.blurImg.png",blurImg); //1.그레이스케일
	      Imgcodecs.imwrite("C:\\img_test\\work\\2.canntImg.png",canntImg); //2.모핑 그라디언트
	      Imgcodecs.imwrite("C:\\img_test\\work\\4.cvImg.png", cvImg); //4. 윤곡 찾기
	    
	    return "suc";
	}

	
	
	public static String reviseGradient( String filePath ) {
		Mat orgImg = Imgcodecs.imread( filePath );
		//흑백조 
	    Mat grayImg = Imgcodecs.imread( filePath, Imgcodecs.IMREAD_GRAYSCALE );

	    Imgcodecs.imwrite( "C:\\img_test\\work\\grayImg.png", grayImg );
	    
	    //adaptive threshold
	    Imgproc.adaptiveThreshold(grayImg, grayImg, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 40);
//	    Imgproc.threshold( grayImg, grayImg, 200, 255, Imgproc.THRESH_BINARY );

	    Imgcodecs.imwrite( "C:\\img_test\\work\\grayImg2.png", grayImg );
	    //색상 반전
	    Core.bitwise_not( grayImg, grayImg );
	    Imgcodecs.imwrite( "C:\\img_test\\work\\grayImg3.png", grayImg );
	    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
	    
	    //침식 수행 - 주변의 작은 픽셀들을 없애고 픽셀을 깔끔하게 만듬.
	    Imgproc.erode(grayImg, grayImg, element);
	    Imgcodecs.imwrite( "C:\\img_test\\work\\grayImg4.png", grayImg );
	    
	    //find

	    Mat hierarchy = new Mat();    //윤곽선 찾기(finding constour)를 위한 매트릭스 
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Imgproc.findContours(grayImg, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
	    
	    if(contours.size()>0) {

            for(int idx = 0; idx < contours.size(); idx++) {
                Rect rect = Imgproc.boundingRect(contours.get(idx));
            	Imgproc.drawContours(grayImg,contours, idx, new Scalar(0, 255, 0), 5);
                if (rect.height > 1500 && rect.width > 1500   
                		//&&!(rect.width >= 512 - 5 && rect.height >= 512 - 5)
                		){
      		      	//윤곽찾기 결과확인용 시작
//                	Imgproc.get
                	//이미지 잘라내서 저장하기
                	Mat croped = new Mat(orgImg, rect);
                	String file = "C:\\img_test\\work\\finished_"+idx+".png";
      		      	Imgcodecs.imwrite(file,croped);
                	//윤곽찾기 결과확잉뇽 끝
                	
                }

            }
        }
	    
	    
	    
	    
	    
	    //흰색 픽셀들을 찾는다. 
	    Mat wLocMat = Mat.zeros(grayImg.size(),grayImg.type());
	    Core.findNonZero(grayImg, wLocMat);

	    //Create an empty Mat and pass it to the function
	    MatOfPoint matOfPoint = new MatOfPoint( wLocMat );

	    //Translate MatOfPoint to MatOfPoint2f in order to user at a next step
	    MatOfPoint2f mat2f = new MatOfPoint2f();
	    matOfPoint.convertTo(mat2f, CvType.CV_32FC2);

	    //Get rotated rect of white pixels
	    RotatedRect rotatedRect = Imgproc.minAreaRect( mat2f );

	    System.out.println(rotatedRect.angle);
	    
	    Point[] vertices = new Point[4];
	    rotatedRect.points(vertices);
//	    System.out.println("1");
//	    System.out.println("rotatedRect.size.width :"+rotatedRect.size.width);
//	    System.out.println("rotatedRect.size.height :"+rotatedRect.size.height);
//	    System.out.println("rotatedRect.size.x :"+rotatedRect.angle);
//	    System.out.println("vertices :"+ vertices[0].x);
	    // [{207.75306583419638, 4041.6545517185514}, {160.71580452365856, 636.8805712793454}, 
	    // {2510.6685650251784, 604.4157607814486}, {2557.7058263357167, 4009.1897412206545}]
	    //좌상 좌하 우상 우하
	    
	    List<MatOfPoint> boxContours = new ArrayList<>();
	    boxContours.add(new MatOfPoint(vertices));
	    
	    Imgproc.drawContours( grayImg, boxContours, 0, new Scalar(128, 128, 128), 1);
	    
	    if(boxContours.size()>0) {
            for(int idx = 0; idx < boxContours.size(); idx++) {
            	

            	MatOfPoint2f pts1 = new MatOfPoint2f(vertices);
            	MatOfPoint2f pts2 = new MatOfPoint2f(
                         new Point(0, 0),
                         new Point(450-1,0),
                         new Point(0,450-1),
                         new Point(450-1,450-1));    
            	Mat warpMat = Imgproc.getPerspectiveTransform(pts1, pts2);
            	

            	Mat destImage = new Mat();
            	Imgproc.warpPerspective(orgImg, destImage, warpMat, orgImg.size());
        	    Imgcodecs.imwrite( "C:\\img_test\\work\\destImage.png", destImage );
            }
        }

	    System.out.println("boxContours : "+boxContours.size());
	    for (int i=0;i<vertices.length;i++) {
	    	Imgproc.circle(orgImg, vertices[i], 20, new Scalar(128, 128, 128), -1);
	    	System.out.println("vertices "+i+": "+vertices[i]);
	    }
	    Imgcodecs.imwrite( "C:\\img_test\\work\\0.circleImg.png", orgImg );
	    
	    double resultAngle = rotatedRect.angle;
	    if (rotatedRect.size.width > rotatedRect.size.height)
	    {
	        rotatedRect.angle += 90.f;
	    }
	    
	    Point center = new Point(orgImg.width()/2, orgImg.height()/2);
	    Mat rotImage = Imgproc.getRotationMatrix2D(center, rotatedRect.angle, 1.0);
	    //1.0 means 100 % scale
	    Size size = new Size(orgImg.width(), orgImg.height());
	    Imgproc.warpAffine(orgImg, orgImg, rotImage, size, Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS);
	    Imgcodecs.imwrite( "C:\\img_test\\work\\computeSkew.png", orgImg );
	    
	    
		return "C:\\img_test\\work\\computeSkew.png";
	    
	}
	

    // [{207.75306583419638, 4041.6545517185514}, {160.71580452365856, 636.8805712793454}, 
    // {2510.6685650251784, 604.4157607814486}, {2557.7058263357167, 4009.1897412206545}]
    //좌상 좌하 우상 우하
	public static String transform( Point[] vertices ) {
		Rect wd = new Rect();
		
		
		return "dd";
	}

	public static void main(String[] args) throws Exception {
		// 1. system load
	    System.load( "C:\\_app\\spring-tool-suite-3.9.4.RELEASE-e4.7.3a-win32-x86_64\\workspace\\git\\tessModule\\opencv_lib\\x64\\opencv_java341.dll");
	    
	    String filePath = reviseGradient("C:\\img_test\\img8.jpg");
	    String filePath2 = recognEdge(filePath);
	    System.out.println("comp");
		
	}


	
	
	
}
