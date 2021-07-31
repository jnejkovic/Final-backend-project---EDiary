package com.iktakademija.e_diary.utils;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.security.Views;

@JsonView(Views.Public.class)
public class RESTError {
	
	private String message;
	private Integer code;

	public RESTError() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RESTError( Integer code, String message) {
		super();
		this.message = message;
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

}
