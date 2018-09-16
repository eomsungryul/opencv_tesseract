package com.tessModule.tess.cmmn.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("bcaMapper")
public class BcaMapper {
	
	@Autowired // 의존관계를 자동으로 연결(JAVA에서 제공) ==@autowired (Spring에서 제공)
    private SqlSession sqlSession; 

	public Map<String, Object> selectGymBca(int param) {
		return sqlSession.selectOne("gymBca.select", param);
	};
	

	public List<Map<String, Object>> selectCategory(String param) {
		return sqlSession.selectList("bcaCategoryAttr.select", param);
	}
	

	public List<Map<String, Object>> selectItem(String string) {
		return sqlSession.selectList("bcaItemAttr.select", string);
	}
	
	/**
	 * apikey 가져오기
	 * 
	 * @param Map<String, Object> storeMap
	 * @return EgovMap
	 * @exception Exception
	 */
	Map<String,Object> getApiKey(String string) throws Exception{
		return sqlSession.selectOne("code.select", string);
	}

	public int insertConvert(Map<String, Object> param) throws Exception {
		return sqlSession.insert("convert.insert", param);
	}

	public List<Map<String, Object>> selectWaitList(Map<String, Object> param) throws Exception {
		return sqlSession.selectList("convert.select", param);
	}

	public void updateConvert(Map<String, Object> workList) {
		sqlSession.update("convert.update", workList);
	}







}
