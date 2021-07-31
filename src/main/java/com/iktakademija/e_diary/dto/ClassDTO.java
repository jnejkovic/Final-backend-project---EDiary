package com.iktakademija.e_diary.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ClassDTO {
	
	@NotNull(message = "Class must be provided.")
	@Min(value = 1, message = "Class must be greather than or equal to 1")
	@Max(value = 8, message = "Class must be less or equal to 8")
	private Integer classNum;

	public ClassDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getClassNum() {
		return classNum;
	}

	public void setClassNum(Integer classNum) {
		this.classNum = classNum;
	}

}
