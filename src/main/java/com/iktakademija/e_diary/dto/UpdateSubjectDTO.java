package com.iktakademija.e_diary.dto;

import javax.validation.constraints.Size;

public class UpdateSubjectDTO {
	@Size(min=3, max=20, message="Subject name must be between {min} and {max} characters long.")
	private String subjectName;
	@Size(min=3, max=50, message="Subject description must be between {min} and {max} characters long.")
	private String subjectDescription;
	private Boolean isMandatory;
	private Integer classId;
	public UpdateSubjectDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getSubjectDescription() {
		return subjectDescription;
	}
	public void setSubjectDescription(String subjectDescription) {
		this.subjectDescription = subjectDescription;
	}
	public Boolean getIsMandatory() {
		return isMandatory;
	}
	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	public Integer getClassId() {
		return classId;
	}
	public void setClassId(Integer classId) {
		this.classId = classId;
	}
	
	
}
