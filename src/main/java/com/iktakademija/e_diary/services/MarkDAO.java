package com.iktakademija.e_diary.services;

import java.util.List;

import com.iktakademija.e_diary.entities.DepartmentEntity;
import com.iktakademija.e_diary.entities.MarkEntity;
import com.iktakademija.e_diary.entities.TeacherEntity;

public interface MarkDAO {
	public List<MarkEntity> findMarkBySubject(TeacherEntity teacher, DepartmentEntity department);
}
