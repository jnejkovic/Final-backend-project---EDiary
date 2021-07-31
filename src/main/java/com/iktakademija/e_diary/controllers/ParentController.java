package com.iktakademija.e_diary.controllers;

import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.dto.ParentDTO;
import com.iktakademija.e_diary.dto.UpdateParentDTO;
import com.iktakademija.e_diary.entities.ParentEntity;
import com.iktakademija.e_diary.entities.StudentEntity;
import com.iktakademija.e_diary.entities.UserEntity;
import com.iktakademija.e_diary.enumerations.ERole;
import com.iktakademija.e_diary.repositories.ParentRepository;
import com.iktakademija.e_diary.repositories.RoleRepository;
import com.iktakademija.e_diary.repositories.StudentRepository;
import com.iktakademija.e_diary.repositories.UserRepository;
import com.iktakademija.e_diary.security.Views;
import com.iktakademija.e_diary.utils.Encryption;
import com.iktakademija.e_diary.utils.RESTError;

@RestController
@RequestMapping(value = "/api/v1/parent")
public class ParentController {
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ParentRepository parentRepo;
	@Autowired
	private StudentRepository studentRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private UserRepository userRepo;

	/**
	 * Admin can add new parent
	 * 
	 * @param newParent
	 * @param result
	 * @return created parent entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> addNewParent(@Valid @RequestBody ParentDTO newParent, BindingResult result) {

		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}

			Optional<UserEntity> parent2 = Optional.ofNullable(userRepo.findByUsername(newParent.getUsername()));
			if (parent2.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.BAD_REQUEST.value(), "Username already in use"),
						HttpStatus.BAD_REQUEST);
			}

			ParentEntity parent = new ParentEntity();
			parent.setEmail(newParent.getEmail());
			parent.setLastName(newParent.getLastName());
			parent.setFirstName(newParent.getFirstName());
			parent.setPassword(Encryption.getPasswordEncoded(newParent.getPassword()));
			parent.setRole(roleRepo.findByroleName(ERole.ROLE_PARENT));
			parent.setTelNum(newParent.getTelNum());
			parent.setUsername(newParent.getUsername());
			parentRepo.save(parent);
			logger.info("New parent added - id: "+parent.getId()+", "+parent.getFirstName()+" "+parent.getLastName());
			return new ResponseEntity<ParentEntity>(parent, HttpStatus.CREATED);

		} catch (Exception e) {
			logger.error("Exception executing ParentController.addNewParent, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can edit existing parent
	 * 
	 * @param parentId
	 * @param updateParent
	 * @param result
	 * @return updated parent entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{parentId}")
	public ResponseEntity<?> updateParent(@PathVariable Integer parentId,
			@Valid @RequestBody UpdateParentDTO updateParent, BindingResult result) {

		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			Optional<ParentEntity> parent = parentRepo.findById(parentId);
			if (parent.isPresent()) {
				if (updateParent.getEmail() != null) {
					parent.get().setEmail(updateParent.getEmail());
				}
				if (updateParent.getFirstName() != null) {
					parent.get().setFirstName(updateParent.getFirstName());
				}
				if (updateParent.getLastName() != null) {
					parent.get().setLastName(updateParent.getLastName());
				}

				if (updateParent.getPassword() != null) {
					parent.get().setPassword(Encryption.getPasswordEncoded(updateParent.getPassword()));
				}
				if (updateParent.getTelNum() != null) {
					parent.get().setTelNum(updateParent.getTelNum());
				}
				parentRepo.save(parent.get());
				logger.info("Parent is updated - id: "+parent.get().getId()+", "+parent.get().getFirstName()+" "+parent.get().getLastName());
				return new ResponseEntity<ParentEntity>(parent.get(), HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Parent not found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			logger.error("Exception executing ParentController.updateParent, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can find parent by id
	 * 
	 * @param parentId
	 * @return parent entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{parentId}")
	public ResponseEntity<?> findParentById(@PathVariable Integer parentId) {
		try {
			Optional<ParentEntity> parent = parentRepo.findById(parentId);
			if (parent.isPresent()) {
				logger.info("Parent is seen - id: "+parent.get().getId()+", "+parent.get().getFirstName()+" "+parent.get().getLastName());
				return new ResponseEntity<ParentEntity>(parent.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Parent not found"), HttpStatus.NOT_FOUND);
			}

		} catch (

		Exception e) {
			logger.error("Exception executing ParentController.findParentById, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can delete parent
	 * 
	 * @param parentId
	 * @return deleted parent entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{parentId}")
	public ResponseEntity<?> deleteParentById(@PathVariable Integer parentId) {
		try {
			Optional<ParentEntity> parent = parentRepo.findById(parentId);
			if (parent.isPresent()) {
				parentRepo.delete(parent.get());
				logger.info("Parent is deleted - id: "+parent.get().getId()+", "+parent.get().getFirstName()+" "+parent.get().getLastName());
				return new ResponseEntity<ParentEntity>(parent.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Parent not found"), HttpStatus.NOT_FOUND);
			}

		} catch (

		Exception e) {
			logger.error("Exception executing ParentController.deleteParentById, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can list all parents
	 * 
	 * @return list of all parents
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/")
	public ResponseEntity<?> getAllParents() {
		try {
			List<ParentEntity> allParents = (List<ParentEntity>) parentRepo.findAll();
			return new ResponseEntity<List<ParentEntity>>(allParents, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing ParentController.getAllParents, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can add children to parent
	 * 
	 * @param parentId
	 * @param studentId
	 * @return
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{parentId}/student/{studentId}")
	public ResponseEntity<?> connectStudentWithParent(@PathVariable Integer parentId, @PathVariable Integer studentId) {
		try {
			Optional<ParentEntity> parent = parentRepo.findById(parentId);
			Optional<StudentEntity> student = studentRepo.findById(studentId);
			if (!student.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Student not found"), HttpStatus.NOT_FOUND);
			}
			if (!parent.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Parent not found"), HttpStatus.NOT_FOUND);
			}
			if (parent.get().getChildren().contains(student.get())) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.BAD_REQUEST.value(), "This children is already added to parent."),
						HttpStatus.BAD_REQUEST);
			}
			parent.get().getChildren().add(student.get());
			parentRepo.save(parent.get());
			logger.info("Parent - id: "+parent.get().getId()+", "+parent.get().getFirstName()+" "+parent.get().getLastName()+" is connected with student id"+studentId);
			return new ResponseEntity<ParentEntity>(parent.get(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing ParentController.connectStudentWithParent, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * List children for parent
	 * 
	 * @param parentId
	 * @return
	 */
	@JsonView(Views.Private.class)
	@Secured({ "ROLE_ADMIN", "ROLE_PARENT" })
	@RequestMapping(method = RequestMethod.GET, value = "/{parentId}/children/")
	public ResponseEntity<?> getChildrenForParent(@PathVariable Integer parentId) {
		try {
			Optional<ParentEntity> parent = parentRepo.findById(parentId);
			if (!parent.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Parent not found"), HttpStatus.NOT_FOUND);
			}
			logger.info("Childre for parent - id: "+parent.get().getId()+", "+parent.get().getFirstName()+" "+parent.get().getLastName()+" are seen");
			return new ResponseEntity<List<StudentEntity>>(parent.get().getChildren(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing ParentController.getChildrenForParent, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}
}
