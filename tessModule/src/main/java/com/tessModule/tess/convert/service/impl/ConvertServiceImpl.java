package com.tessModule.tess.convert.service.impl;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.tessModule.tess.convert.service.ConvertService;
import com.tessModule.tess.convert.vo.Convert;

@Service("convertService")
public class ConvertServiceImpl implements ConvertService{

	/** fileUtil *//*
	@Resource(name = "fileMngUtil")
	private FileMngUtil fileUtil;

	*//** opencvUtil *//*
	@Resource(name = "opencvUtil")
	private OpencvUtil opencvUtil;*/

	/** tesseractUtil *//*
	@Resource(name = "tesseractUtil")
	private TesseractUtil tesseractUtil;*/

	@Resource(name = "convertMapper") 
	private ConvertMapper convertMapper;
	

	@Override
	public Convert get(Convert convert) throws Exception {
		return (Convert)convertMapper.get(convert);
	}

	@Override
	public List<Convert> list(Convert convert) throws Exception {
		return convertMapper.list(convert); 
	}
	
	@Override
	public int insert(Convert convert) throws Exception {
		return convertMapper.insert(convert); 
	}
	@Override
	public int update(Convert convert) throws Exception {
		return convertMapper.update(convert); 
	}
	
	
	
	
	
	
	
	
	
	
	
	/*
	
	
	@Override
	public String convertImg2Text(HashMap<String, String> paramMap) {
		return null;
	}
	

	@Override
	public void insertUserBcaLog(UserBcaLogVO uBcaVO) throws Exception {
		convertMapper.insertUserBcaLog(uBcaVO);
	}

	@Override
	public void insertErrorLog(StatusLogVO vo) throws Exception {
		convertMapper.insertErrorLog(vo);
	}

	*/



}
