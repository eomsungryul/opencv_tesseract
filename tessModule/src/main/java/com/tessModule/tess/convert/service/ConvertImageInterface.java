package com.tessModule.tess.convert.service;

import org.opencv.core.Mat;

import com.tessModule.tess.convert.vo.Convert;

public interface ConvertImageInterface {
	public Mat getMat(Convert convert) throws Exception;
	
}
