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
import com.iktakademija.e_diary.dto.ClassDTO;
import com.iktakademija.e_diary.entities.ClassEntity;
import com.iktakademija.e_diary.entities.SubjectEntity;
import com.iktakademija.e_diary.repositories.ClassRepository;
import com.iktakademija.e_diary.repositories.SubjectRepository;
import com.iktakademija.e_diary.security.Views;
import com.iktakademija.e_diary.utils.RESTError;

@RestController
@RequestMapping(value = "/api/v1/class")
public class ClassController {

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ClassRepository classRepo;
	@Autowired
	private SubjectRepository subjectRepo;

	/**
	 * Admin can add new class
	 * @param newClass
	 * @param result
	 * @return created ClassEntity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> addClass(@Valid @RequestBody ClassDTO newClass, BindingResult result) {

		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			Optional<ClassEntity> optionalClass = Optional.ofNullable(classRepo.findByClassNum(newClass.getClassNum()));
			if (optionalClass.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.BAD_REQUEST.value(), "Class already exists"), HttpStatus.BAD_REQUEST);
			} else {
				ClassEntity classEntity = new ClassEntity();
				classEntity.setClassNum(newClass.getClassNum());
				classRepo.save(classEntity);
				logger.info("Class id - "+classEntity.getId()+" is created");
				return new ResponseEntity<ClassEntity>(classEntity, HttpStatus.CREATED);
			}
		} catch (Exception e) {
			logger.error("Exception executing ClassController.addClass, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	/**
	 * Admin can get all classes
	 * @return list of classes
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET, value="/")
	public ResponseEntity<?> getAllClasses(){
		try {
			List<ClassEntity> allClasses=(List<ClassEntity>) classRepo.findAll();
			logger.info("All classes are seen");
			return new ResponseEntity<List<ClassEntity>>(allClasses, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing ClassController.getAllClasses, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}

}
