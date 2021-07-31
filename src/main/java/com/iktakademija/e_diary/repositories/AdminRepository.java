package com.iktakademija.e_diary.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.e_diary.entities.AdminEntity;

public interface AdminRepository extends CrudRepository<AdminEntity, Integer> {
	
	AdminEntity findByUsername(String username);

}
