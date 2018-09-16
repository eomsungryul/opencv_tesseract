package com.tessModule.tess.convert.vo;

import java.io.Serializable;
import java.sql.Date;

public class Convert implements Serializable{
	private static final long serialVersionUID = -6637438563094452954L;
	
	private int convert_id;
	private int user_id;
	private int gym_id;
	private String input_file_location;
	private int convert_status;
	private Date convert_status_process_dt;
	private Date convert_status_finish_dt;
	private String api_key;
	
	private String fileName;
	private String fileExtension;
	
	public String getApi_key() {
		return api_key;
	}
	public void setApi_key(String api_key) {
		this.api_key = api_key;
	}
	public int getConvert_id() {
		return convert_id;
	}
	public void setConvert_id(int convert_id) {
		this.convert_id = convert_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getGym_id() {
		return gym_id;
	}
	public void setGym_id(int gym_id) {
		this.gym_id = gym_id;
	}
	public String getInput_file_location() {
		return input_file_location;
	}
	public void setInput_file_location(String input_file_location) {
		this.input_file_location = input_file_location;
	}
	public int getConvert_status() {
		return convert_status;
	}
	public void setConvert_status(int convert_status) {
		this.convert_status = convert_status;
	}
	public Date getConvert_status_process_dt() {
		return convert_status_process_dt;
	}
	public void setConvert_status_process_dt(Date convert_status_process_dt) {
		this.convert_status_process_dt = convert_status_process_dt;
	}
	public Date getConvert_status_finish_dt() {
		return convert_status_finish_dt;
	}
	public void setConvert_status_finish_dt(Date convert_status_finish_dt) {
		this.convert_status_finish_dt = convert_status_finish_dt;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileExtension() {
		return fileExtension;
	}
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
}
