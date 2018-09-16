package com.tessModule.tess.cmmn.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.tessModule.tess.cmmn.service.BcaService;
import com.tessModule.tess.convert.service.ConvertService;

@Service("bcaService")
public class BcaServiceImpl implements BcaService {

	@Resource(name = "bcaMapper")
	private BcaMapper bcaMapper;
	
//	@Override
//	public void img2text() throws Exception{
//		Map<String, Object> res = convertService.getApiKey("apikey");
//		System.out.println("res : "+res);
//	}

	@Override
	public Map<String, Object> selectGymBca(int param) throws Exception {
		return bcaMapper.selectGymBca(param);
	}

	@Override
	public List<Map<String, Object>> selectCategory(String string) throws Exception {
		return bcaMapper.selectCategory(string);
	}

	@Override
	public List<Map<String, Object>> selectItem(String string) throws Exception {
		return bcaMapper.selectItem(string);
	}
}
