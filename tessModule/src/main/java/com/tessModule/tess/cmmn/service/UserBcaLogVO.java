package com.tessModule.tess.cmmn.service;

import java.io.Serializable;

import org.opencv.core.Mat;

public class UserBcaLogVO implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1194829977135145766L;
	
	
	//변환 ID
	private String convertId = "";
	
	//카ㅣ테
	private String categoryMapId = "";
	
	//사용자 ID
	private String userId = "";
	
	//변환 ID
	private String bcaAttrValue = "";
	
	public String getCategoryMapId() {
		return categoryMapId;
	}

	public void setCategoryMapId(String categoryMapId) {
		this.categoryMapId = categoryMapId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBcaAttrValue() {
		return bcaAttrValue;
	}

	public void setBcaAttrValue(String bcaAttrValue) {
		this.bcaAttrValue = bcaAttrValue;
	}

	public String getConvertId() {
		return convertId;
	}

	public void setConvertId(String convertId) {
		this.convertId = convertId;
	}

	
	

}
