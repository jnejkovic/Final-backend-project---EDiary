package com.iktakademija.e_diary.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.e_diary.entities.TeacherEntity;

public interface TeacherRepository extends CrudRepository<TeacherEntity, Integer> {
	TeacherEntity findByUsername(String username);
}
