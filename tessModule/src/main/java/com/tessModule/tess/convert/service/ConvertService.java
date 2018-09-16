package com.tessModule.tess.convert.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tessModule.tess.cmmn.service.StatusLogVO;
import com.tessModule.tess.cmmn.service.UserBcaLogVO;
import com.tessModule.tess.convert.vo.Convert;

public interface ConvertService {

	public int insert(Convert convert) throws Exception;
	public List<Convert> list(Convert convert) throws Exception;
	public Convert get(Convert convert) throws Exception;
	public int update(Convert convert) throws Exception;
	public List<Map<String, Object>> selectConvert(Map<String, Object> searchParam);
	public void insertUserBcaLog(UserBcaLogVO uBcaVO);
	
	
	
	
	
	
	
	
	
	/*
	
	
	
	
public String convertImg2Text(HashMap<String, String> paramMap) throws Exception;

	

	public List<Map<String, Object>> selectConvert(Map<String, Object> searchParam) throws Exception;

	public void updateConvert(Map<String, Object> workVal) throws Exception;

	public void insertUserBcaLog(UserBcaLogVO uBcaVO) throws Exception;

	public void insertErrorLog(StatusLogVO errorLog) throws Exception;*/

}
