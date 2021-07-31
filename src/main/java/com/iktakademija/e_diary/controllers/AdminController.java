package com.iktakademija.e_diary.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.dto.AdminDTO;
import com.iktakademija.e_diary.dto.UpdateAdminDTO;
import com.iktakademija.e_diary.entities.AdminEntity;
import com.iktakademija.e_diary.entities.UserEntity;
import com.iktakademija.e_diary.enumerations.ERole;
import com.iktakademija.e_diary.repositories.AdminRepository;
import com.iktakademija.e_diary.repositories.RoleRepository;
import com.iktakademija.e_diary.repositories.UserRepository;
import com.iktakademija.e_diary.security.Views;
import com.iktakademija.e_diary.services.FileDownload;
import com.iktakademija.e_diary.utils.Encryption;
import com.iktakademija.e_diary.utils.RESTError;

@RestController
@RequestMapping(value = "/api/v1/admin")
public class AdminController {
	/*
	 * 1. internal server error 2. "User with this username or jmbg already exists"
	 * 3. "Jmbg already in use"
	 * 
	 */

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	private FileDownload fileDownload;

	@Autowired
	private AdminRepository adminRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private UserRepository userRepo;
	
	/**
	 * Admin can view list of log files
	 * @param dir
	 * @return 
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/list/{dir}")
	public ResponseEntity<?> listFiles(@PathVariable String dir) {
		try {
			return new ResponseEntity<Set<String>>(fileDownload.listFiles(dir), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing AdminController.downloadLogFile, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	/**
	 * Admin can download log files
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/download", method = RequestMethod.GET, produces = "application/octet-stream")
	public ResponseEntity<?> downloadLogFile(@RequestParam String fileName) throws IOException {
		try {
			return new ResponseEntity<byte[]>(fileDownload.downloadFile(fileName), HttpStatus.OK);
		} catch (FileNotFoundException e) {
			logger.error("Exception executing AdminController.downloadLogFile, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			logger.error("Exception executing AdminController.downloadLogFile, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin adds new admin.
	 * 
	 * @param newAdmin
	 * @param result
	 * @return created Admin Entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> addNewAdmin(@Valid @RequestBody AdminDTO newAdmin, BindingResult result) {

		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			Optional<UserEntity> admin2 = Optional.ofNullable(userRepo.findByUsername(newAdmin.getUsername()));
			if (admin2.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.BAD_REQUEST.value(), "Username already in use"),
						HttpStatus.BAD_REQUEST);
			}
			AdminEntity admin = new AdminEntity();
			admin.setEmail(newAdmin.getEmail());
			admin.setLastName(newAdmin.getLastName());
			admin.setFirstName(newAdmin.getFirstName());
			admin.setPassword(Encryption.getPasswordEncoded(newAdmin.getPassword()));
			admin.setRole(roleRepo.findByroleName(ERole.ROLE_ADMIN));
			admin.setTelNumber(newAdmin.getTelNum());
			admin.setUsername(newAdmin.getUsername());
			adminRepo.save(admin);
			logger.info("New admin is created - id:"+admin.getId()+", "+admin.getFirstName()+" "+admin.getLastName());
			return new ResponseEntity<AdminEntity>(admin, HttpStatus.CREATED);

		} catch (Exception e) {
			logger.error("Exception executing AdminController.addNewAdmin, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin updates an existing admin
	 * 
	 * @param adminId
	 * @param updateAdmin
	 * @param result
	 * @return updated Admin Entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{adminId}")
	public ResponseEntity<?> updateAdmin(@PathVariable Integer adminId, @Valid @RequestBody UpdateAdminDTO updateAdmin,
			BindingResult result) {

		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			Optional<AdminEntity> admin = adminRepo.findById(adminId);
			if (admin.isPresent()) {
				if (updateAdmin.getEmail() != null) {
					admin.get().setEmail(updateAdmin.getEmail());
				}
				if (updateAdmin.getFirstName() != null) {
					admin.get().setFirstName(updateAdmin.getFirstName());
				}
				if (updateAdmin.getLastName() != null) {
					admin.get().setLastName(updateAdmin.getLastName());
				}

				if (updateAdmin.getPassword() != null) {
					admin.get().setPassword(Encryption.getPasswordEncoded(updateAdmin.getPassword()));
				}
				if (updateAdmin.getTelNum() != null) {
					admin.get().setTelNumber(updateAdmin.getTelNum());
				}
				adminRepo.save(admin.get());
				logger.info("Admin is updated - id:"+admin.get().getId()+", "+admin.get().getFirstName()+" "+admin.get().getLastName());
				return new ResponseEntity<AdminEntity>(admin.get(), HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Admin not found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			logger.error("Exception executing AdminController.updateAdmin, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin gets info about another admin
	 * 
	 * @param adminId
	 * @return Admin Entity found by id
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{adminId}")
	public ResponseEntity<?> findAdminById(@PathVariable Integer adminId) {
		try {
			Optional<AdminEntity> admin = adminRepo.findById(adminId);
			if (admin.isPresent()) {
				logger.info("Admin saw - id:"+admin.get().getId()+", "+admin.get().getFirstName()+" "+admin.get().getLastName());
				return new ResponseEntity<AdminEntity>(admin.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Admin not found"), HttpStatus.NOT_FOUND);
			}

		} catch (

		Exception e) {
			logger.error("Exception executing AdminController.findAdminById, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can deactivate another admin if there are more than two admins
	 * 
	 * @param adminId
	 * @return deactivated admin entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/deactivate/{adminId}")
	public ResponseEntity<?> deactivateAdmin(@PathVariable Integer adminId) {
		try {
			Optional<AdminEntity> admin = adminRepo.findById(adminId);
			List<AdminEntity> allAdmins = (List<AdminEntity>) adminRepo.findAll();
			if (admin.isPresent() && allAdmins.size() >= 2) {
				admin.get().setActive(false);
				adminRepo.save(admin.get());
				logger.info("Admin - id:"+admin.get().getId()+", "+admin.get().getFirstName()+" "+admin.get().getLastName()+" is deactivated");
				return new ResponseEntity<AdminEntity>(admin.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.BAD_REQUEST.value(), "Admin not found"), HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			logger.error("Exception executing AdminController.deactivateAdmin, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Admin can delete another admin if list of admins is greather than 2
	 * 
	 * @param adminId
	 * @return deleted admin entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{adminId}")
	public ResponseEntity<?> deleteAdminById(@PathVariable Integer adminId) {
		try {
			Optional<AdminEntity> admin = adminRepo.findById(adminId);
			List<AdminEntity> allAdmins = (List<AdminEntity>) adminRepo.findAll();
			if (admin.isPresent() && allAdmins.size() >= 2) {
				adminRepo.delete(admin.get());
				logger.info("Admin - id:"+admin.get().getId()+", "+admin.get().getFirstName()+" "+admin.get().getLastName()+" is deleted");
				return new ResponseEntity<AdminEntity>(admin.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.BAD_REQUEST.value(), "Admin not found"), HttpStatus.BAD_REQUEST);
			}

		} catch (

		Exception e) {
			logger.error("Exception executing AdminController.deleteAdminById, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can get a list of admins
	 * 
	 * @return list of admins
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/")
	public ResponseEntity<?> getAllAdmins() {
		try {
			List<AdminEntity> allAdmins = (List<AdminEntity>) adminRepo.findAll();
			logger.info("All admins are seen");
			return new ResponseEntity<List<AdminEntity>>(allAdmins, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing AdminController.getAllAdmins, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}

}
