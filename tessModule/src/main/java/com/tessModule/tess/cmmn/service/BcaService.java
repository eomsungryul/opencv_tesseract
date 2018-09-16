package com.tessModule.tess.cmmn.service;

import java.util.List;
import java.util.Map;

public interface BcaService {
	public Map<String, Object> selectGymBca(int param) throws Exception;

	public List<Map<String, Object>> selectCategory(String string) throws Exception;

	public List<Map<String, Object>> selectItem(String string) throws Exception;
}
