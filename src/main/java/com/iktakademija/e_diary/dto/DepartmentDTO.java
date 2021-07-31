package com.iktakademija.e_diary.dto;

import com.iktakademija.e_diary.entities.ClassEntity;
import com.iktakademija.e_diary.entities.TeacherEntity;

public class DepartmentDTO {
	
	private String name;
	private ClassEntity classEntity;
	private TeacherEntity classElder;
	public DepartmentDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ClassEntity getClassEntity() {
		return classEntity;
	}
	public void setClassEntity(ClassEntity classEntity) {
		this.classEntity = classEntity;
	}
	
	public TeacherEntity getClassElder() {
		return classElder;
	}
	public void setClassElder(TeacherEntity classElder) {
		this.classElder = classElder;
	}
	
	
	
}
