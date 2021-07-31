package com.iktakademija.e_diary.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.e_diary.entities.ClassEntity;

public interface ClassRepository extends CrudRepository<ClassEntity, Integer> {
	
	ClassEntity findByClassNum(Integer classNum);

}
