package com.iktakademija.e_diary.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
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
import com.iktakademija.e_diary.dto.SubjectDTO;
import com.iktakademija.e_diary.dto.UpdateSubjectDTO;
import com.iktakademija.e_diary.entities.ClassEntity;
import com.iktakademija.e_diary.entities.SubjectEntity;
import com.iktakademija.e_diary.entities.TeacherEntity;
import com.iktakademija.e_diary.repositories.ClassRepository;
import com.iktakademija.e_diary.repositories.SubjectRepository;
import com.iktakademija.e_diary.security.Views;
import com.iktakademija.e_diary.utils.RESTError;

@RestController
@RequestMapping(value = "/api/v1/subject")

public class SubjectController {

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SubjectRepository subjectRepo;
	@Autowired
	private ClassRepository classRepo;

	/**
	 * Admin can add new subject
	 * 
	 * @param newSubject
	 * @param result
	 * @return created subject
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/")

	public ResponseEntity<?> addNewSubject(@Valid @RequestBody SubjectDTO newSubject, BindingResult result) {

		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}

			SubjectEntity subject = new SubjectEntity();
			subject.setSubjectName(newSubject.getSubjectName());
			subject.setSubjectDescription(newSubject.getSubjectDescription());
			subject.setIsMandatory(newSubject.getIsMandatory());
			Optional<ClassEntity> classEntity = classRepo.findById(newSubject.getClassId());
			if (!classEntity.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(2, "Class is not found"), HttpStatus.NOT_FOUND);
			}
			subject.setClassEntity(classEntity.get());

			subjectRepo.save(subject);
			logger.info("New subject is added "+subject.getId()+", "+subject.getSubjectName());
			return new ResponseEntity<SubjectEntity>(subject, HttpStatus.CREATED);

		} catch (Exception e) {
			logger.error("Exception executing SubjectController.addNewSubject, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(1, "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Admin can update existing subject
	 * 
	 * @param subjectId
	 * @param updateSubject
	 * @param result
	 * @return updated subject entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{subjectId}")
	public ResponseEntity<?> updateSubject(@PathVariable Integer subjectId,
			@Valid @RequestBody UpdateSubjectDTO updateSubject, BindingResult result) {

		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			Optional<SubjectEntity> subject = subjectRepo.findById(subjectId);
			if (subject.isPresent()) {
				if (updateSubject.getSubjectName() != null) {
					subject.get().setSubjectName(updateSubject.getSubjectName());
				}
				if (updateSubject.getSubjectDescription() != null) {
					subject.get().setSubjectDescription(updateSubject.getSubjectDescription());
				}
				if (updateSubject.getIsMandatory() != null) {
					subject.get().setIsMandatory(updateSubject.getIsMandatory());
				}
				if (updateSubject.getClassId() != null) {
					Optional<ClassEntity> classEntity = classRepo.findById(updateSubject.getClassId());
					if (!classEntity.isPresent()) {
						return new ResponseEntity<RESTError>(new RESTError(2, "Class is not found"),
								HttpStatus.NOT_FOUND);
					}
					subject.get().setClassEntity(classEntity.get());
				}

				subjectRepo.save(subject.get());
				logger.info("Subject is updated "+subject.get().getId()+", "+subject.get().getSubjectName());
				return new ResponseEntity<SubjectEntity>(subject.get(), HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(3, "Subject not found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			logger.error("Exception executing SubjectController.updateSubject, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(1, "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * admin can delete some subject
	 * 
	 * @param subjectId
	 * @return deleted subject entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{subjectId}")
	public ResponseEntity<?> deleteSubjectById(@PathVariable Integer subjectId) {
		try {
			Optional<SubjectEntity> subject = subjectRepo.findById(subjectId);
			if (subject.isPresent()) {
				subjectRepo.delete(subject.get());
				logger.info("Subject is deleted "+subject.get().getId()+", "+subject.get().getSubjectName());
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(3, "Subject not found"), HttpStatus.BAD_REQUEST);
			}

		} catch (

		Exception e) {
			logger.error("Exception executing SubjectController.deleteSubjectById, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(1, "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can find subject by Id
	 * 
	 * @param subjectId
	 * @return founded subject entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{subjectId}")
	public ResponseEntity<?> findSubjectById(@PathVariable Integer subjectId) {
		try {
			Optional<SubjectEntity> subject = subjectRepo.findById(subjectId);
			if (subject.isPresent()) {
				logger.info("Subject is seen "+subject.get().getId()+", "+subject.get().getSubjectName());
				return new ResponseEntity<SubjectEntity>(subject.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(3, "Subject not found"), HttpStatus.BAD_REQUEST);
			}

		} catch (

		Exception e) {
			logger.error("Exception executing SubjectController.findSubjectById, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(1, "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can list all subjects
	 * 
	 * @return list of all subjects
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/")
	public ResponseEntity<?> getAllSubjects() {
		try {
			List<SubjectEntity> allSubjects = (List<SubjectEntity>) subjectRepo.findAll();
			return new ResponseEntity<List<SubjectEntity>>(allSubjects, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing SubjectController.getAllSubjects, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(1, "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can list teachers for subject
	 * 
	 * @param subjectId
	 * @return list of teachers
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{subjectId}/teachers/")
	public ResponseEntity<?> getTeachersForSubject(@PathVariable Integer subjectId) {
		try {
			Optional<SubjectEntity> subject = subjectRepo.findById(subjectId);
			if (!subject.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(3, "Subject not found"), HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<List<TeacherEntity>>(subject.get().getTeachers(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing SubjectController.getTeachersForSubject, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(1, "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}

}
