package com.iktakademija.e_diary.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.e_diary.entities.ClassEntity;
import com.iktakademija.e_diary.entities.DepartmentEntity;

public interface DepartmentRepository extends CrudRepository<DepartmentEntity, Integer> {
	List<DepartmentEntity> findByClassEntity(ClassEntity classEntity);

}
