package com.iktakademija.e_diary.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SubjectDTO {
	
	@NotNull(message = "Subject name must be provided")
	@Size(min=3, max=20, message="Subject name must be between {min} and {max} characters long.")
	private String subjectName;
	@NotNull(message = "Subject description must be provided")
	@Size(min=3, max=50, message="Subject description must be between {min} and {max} characters long.")
	private String subjectDescription;
	@NotNull(message="Type of subject must be provided")
	private Boolean isMandatory;
	@NotNull(message="Class id must be provided")
	private Integer classId;
	public SubjectDTO() {
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
