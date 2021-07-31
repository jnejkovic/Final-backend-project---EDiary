package com.iktakademija.e_diary.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.e_diary.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
	
	 UserEntity findByUsername(String username);

}
