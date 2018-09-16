package com.tessModule.tess.cmmn.service;

import java.io.Serializable;

import org.opencv.core.Mat;

public class StatusLogVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 635466811417794934L;
	
	
	public StatusLogVO() {
		
	}
	
	public StatusLogVO(
			int exceptionCode ,
			String exceptionMesseage , 
			String exceptionErrors ,
			String requestMethod ,
			String requestUri ,
			String requestBody ,
			String convertId ,
			boolean flag,
			Mat errorMat
			) {
		this.exceptionMesseage = exceptionMesseage;
		this.exceptionCode = exceptionCode;
		this.exceptionErrors = exceptionErrors;
		this.requestMethod = requestMethod;
		this.requestUri = requestUri;
		this.requestBody = requestBody;
		this.convertId = convertId;
		this.flag = flag;
		this.errorMat = errorMat;
		
	}
	
	//에러메세지
	private String exceptionMesseage = "";
	
	//에러 코드
	private int exceptionCode = 0;	
	//에러 상세 메세지
	private String exceptionErrors = "";
	
	//요청 메소드
	private String requestMethod = "";
	
	//요청 url
	private String requestUri = "";
	
	//요청 내용
	private String requestBody = "";

	//요청 내용
	private Mat errorMat;
	
	//변환 ID
	private String convertId = "";
	
	//변환 ID
	private int logId = 0;

	//변환 ID
	private boolean flag = false;

	public String getExceptionErrors() {
		return exceptionErrors;
	}

	public void setExceptionErrors(String exceptionErrors) {
		this.exceptionErrors = exceptionErrors;
	}

	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getConvertId() {
		return convertId;
	}

	public void setConvertId(String convertId) {
		this.convertId = convertId;
	}

	public String getExceptionMesseage() {
		return exceptionMesseage;
	}

	public void setExceptionMesseage(String exceptionMesseage) {
		this.exceptionMesseage = exceptionMesseage;
	}

	public int getExceptionCode() {
		return exceptionCode;
	}

	public void setExceptionCode(int exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getRequestUri() {
		return requestUri;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public Mat getErrorMat() {
		return errorMat;
	}

	public void setErrorMat(Mat errorMat) {
		this.errorMat = errorMat;
	}
	

}
