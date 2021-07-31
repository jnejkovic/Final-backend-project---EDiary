package com.iktakademija.e_diary.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.e_diary.entities.RoleEntity;
import com.iktakademija.e_diary.enumerations.ERole;

public interface RoleRepository extends CrudRepository<RoleEntity, Integer> {
	public RoleEntity findByroleName(ERole name);
}
