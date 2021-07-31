package com.iktakademija.e_diary.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iktakademija.e_diary.entities.AdminEntity;
import com.iktakademija.e_diary.entities.DepartmentEntity;
import com.iktakademija.e_diary.entities.StudentEntity;

public interface StudentRepository extends CrudRepository<StudentEntity, Integer> {
	
	StudentEntity findByJmbg(String jmbg);
	List<StudentEntity> findByDepartment(DepartmentEntity department);
	StudentEntity findByUsername(String username);


}
