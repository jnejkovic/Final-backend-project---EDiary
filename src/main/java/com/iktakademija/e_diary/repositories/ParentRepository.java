package com.iktakademija.e_diary.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.e_diary.entities.ParentEntity;

public interface ParentRepository extends CrudRepository<ParentEntity, Integer> {
	ParentEntity findByUsername(String username);

}
