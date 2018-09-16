package com.tessModule.tess.convert.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tessModule.tess.cmmn.service.StatusLogVO;
import com.tessModule.tess.cmmn.service.UserBcaLogVO;
import com.tessModule.tess.convert.vo.Convert;

@Repository("convertMapper")
public class ConvertMapper {
	
	@Autowired // 의존관계를 자동으로 연결(JAVA에서 제공) ==@autowired (Spring에서 제공)
    private SqlSession sqlSession; 

	public Convert get(Convert convert) throws Exception{
		return (Convert)sqlSession.selectOne("convert.select", convert);
	}
	
	public List<Convert> list(Convert convert) throws Exception {
		return sqlSession.selectList("convert.select", convert);
	}
	
	public int insert(Convert convert) throws Exception {
		return sqlSession.insert("convert.insert", convert);
	}
	public int update(Convert convert) {
		return sqlSession.update("convert.update", convert);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

	

	

	

	public void insertErrorLog(StatusLogVO vo) throws Exception  {
		sqlSession.insert("bcaExceptionLog.insert", vo);
	}

	public void insertUserBcaLog(UserBcaLogVO uBcaVO) throws Exception {
		sqlSession.insert("userBca.insert", uBcaVO);
	};
	


}
