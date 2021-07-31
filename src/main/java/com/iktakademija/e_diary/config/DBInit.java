package com.iktakademija.e_diary.config;

import java.util.List;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iktakademija.e_diary.entities.AdminEntity;
import com.iktakademija.e_diary.entities.RoleEntity;
import com.iktakademija.e_diary.entities.UserEntity;
import com.iktakademija.e_diary.enumerations.ERole;
import com.iktakademija.e_diary.repositories.AdminRepository;
import com.iktakademija.e_diary.repositories.RoleRepository;
import com.iktakademija.e_diary.repositories.UserRepository;
import com.iktakademija.e_diary.utils.Encryption;

@Component
public class DBInit {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private AdminRepository adminRepo;

	@Autowired
	private RoleRepository roleRepo;

	private RoleEntity adminRole;
	private RoleEntity teacherRole;
	private RoleEntity studentRole;
	private RoleEntity parentRole;

	@PostConstruct
	public void init() {
		roleInit();
		adminInit();

	}

	private void roleInit() {
		List<RoleEntity> roles = (List<RoleEntity>) roleRepo.findAll();
		if (roles.isEmpty()) {
			adminRole = new RoleEntity(ERole.ROLE_ADMIN);
			teacherRole = new RoleEntity(ERole.ROLE_TEACHER);
			studentRole = new RoleEntity(ERole.ROLE_STUDENT);
			parentRole = new RoleEntity(ERole.ROLE_PARENT);
			roleRepo.save(adminRole);
			roleRepo.save(teacherRole);
			roleRepo.save(studentRole);
			roleRepo.save(parentRole);
		} else {
			adminRole = roleRepo.findByroleName(ERole.ROLE_ADMIN);
			teacherRole = roleRepo.findByroleName(ERole.ROLE_TEACHER);
			studentRole = roleRepo.findByroleName(ERole.ROLE_STUDENT);
			parentRole = roleRepo.findByroleName(ERole.ROLE_PARENT);
		}
	}

	private void adminInit() {
		 UserEntity user=userRepo.findByUsername("admin");
		 if(user==null) {
			 AdminEntity admin=new AdminEntity();
			 admin.setFirstName("Admin");
			 admin.setLastName("Admin");
			 admin.setEmail("admin@school.rs");
			 admin.setUsername("admin");
			 admin.setPassword(Encryption.getPasswordEncoded("admin"));
			 admin.setRole(roleRepo.findByroleName(ERole.ROLE_ADMIN));
			 admin.setTelNumber("06011111111");
			 adminRepo.save(admin);
			 
		 }
	 }

}
