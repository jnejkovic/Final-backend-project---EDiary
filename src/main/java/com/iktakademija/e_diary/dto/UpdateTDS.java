package com.iktakademija.e_diary.dto;

public class UpdateTDS {

	private Integer teacherId;
	private Integer subjectId;
	private Integer departmentId;

	public UpdateTDS() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Integer teacherId) {
		this.teacherId = teacherId;
	}

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

}
