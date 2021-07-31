package com.iktakademija.e_diary.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.e_diary.entities.MarkEntity;
import com.iktakademija.e_diary.entities.TeacherEntity;

public interface MarkRepository extends CrudRepository<MarkEntity, Integer> {

	List<MarkEntity> findByTeacher(TeacherEntity teacher);
}
