package com.iktakademija.e_diary.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.e_diary.entities.SubjectEntity;
import com.iktakademija.e_diary.entities.TeacherDepartmentSubject;
import com.iktakademija.e_diary.entities.TeacherEntity;

public interface TDSRepository extends CrudRepository<TeacherDepartmentSubject, Integer> {
	
	TeacherDepartmentSubject findByTeacherIdAndSubjectIdAndDepartmentId(Integer teacherId, Integer subjectId, Integer departmentId);
	List<TeacherDepartmentSubject> findByTeacher(TeacherEntity teacher);
	List<TeacherDepartmentSubject> findByTeacherIdAndDepartmentId(Integer teacherId, Integer departmentId);
	

}
