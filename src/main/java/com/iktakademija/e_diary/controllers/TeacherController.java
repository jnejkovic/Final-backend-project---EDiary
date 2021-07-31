package com.iktakademija.e_diary.controllers;

import java.lang.StackWalker.Option;
import java.time.LocalDate;
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
import com.iktakademija.e_diary.dto.MarkDTO;
import com.iktakademija.e_diary.dto.TeacherDTO;
import com.iktakademija.e_diary.dto.TeacherMarkDTO;
import com.iktakademija.e_diary.dto.UpdateTeacherDTO;
import com.iktakademija.e_diary.entities.MarkEntity;
import com.iktakademija.e_diary.entities.StudentEntity;
import com.iktakademija.e_diary.entities.SubjectEntity;
import com.iktakademija.e_diary.entities.TeacherDepartmentSubject;
import com.iktakademija.e_diary.entities.TeacherEntity;
import com.iktakademija.e_diary.entities.UserEntity;
import com.iktakademija.e_diary.enumerations.ERole;
import com.iktakademija.e_diary.repositories.RoleRepository;
import com.iktakademija.e_diary.repositories.StudentRepository;
import com.iktakademija.e_diary.repositories.SubjectRepository;
import com.iktakademija.e_diary.repositories.TDSRepository;
import com.iktakademija.e_diary.repositories.TeacherRepository;
import com.iktakademija.e_diary.repositories.UserRepository;
import com.iktakademija.e_diary.security.Views;
import com.iktakademija.e_diary.services.UserServiceImp;
import com.iktakademija.e_diary.utils.Encryption;
import com.iktakademija.e_diary.utils.RESTError;

@RestController
@RequestMapping(value = "/api/v1/teacher")
public class TeacherController {

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TeacherRepository teacherRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private SubjectRepository subjectRepo;
	@Autowired
	private UserRepository userRepo;

	/**
	 * Admin can add new teacher
	 * 
	 * @param newTeacher
	 * @param result
	 * @return teacher entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> addNewTeacher(@Valid @RequestBody TeacherDTO newTeacher, BindingResult result) {

		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}

			Optional<UserEntity> teacher2 = Optional.ofNullable(userRepo.findByUsername(newTeacher.getUsername()));
			if (teacher2.isPresent()) {
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.BAD_REQUEST.value(), "Username already in use"),
						HttpStatus.BAD_REQUEST);
			}
			TeacherEntity teacher = new TeacherEntity();
			teacher.setEmail(newTeacher.getEmail());
			teacher.setLastName(newTeacher.getLastName());
			teacher.setFirstName(newTeacher.getFirstName());
			teacher.setPassword(Encryption.getPasswordEncoded(newTeacher.getPassword()));
			teacher.setRole(roleRepo.findByroleName(ERole.ROLE_TEACHER));
			teacher.setTelNumber(newTeacher.getTelNum());
			teacher.setUsername(newTeacher.getUsername());
			teacherRepo.save(teacher);
			logger.info("New teacher is added - id: "+teacher.getId()+", "+teacher.getFirstName()+" "+teacher.getLastName());
			return new ResponseEntity<TeacherEntity>(teacher, HttpStatus.CREATED);

		} catch (Exception e) {
			logger.error("Exception executing TeacherController.addNewTeacher, " + e.getMessage());
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can update existing teacher
	 * 
	 * @param teacherId
	 * @param updateTeacher
	 * @param result
	 * @return updated teacher entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{teacherId}")
	public ResponseEntity<?> updateTeacher(@PathVariable Integer teacherId,
			@Valid @RequestBody UpdateTeacherDTO updateTeacher, BindingResult result) {

		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			Optional<TeacherEntity> teacher = teacherRepo.findById(teacherId);
			if (teacher.isPresent()) {
				if (updateTeacher.getEmail() != null) {
					teacher.get().setEmail(updateTeacher.getEmail());
				}
				if (updateTeacher.getFirstName() != null) {
					teacher.get().setFirstName(updateTeacher.getFirstName());
				}
				if (updateTeacher.getLastName() != null) {
					teacher.get().setLastName(updateTeacher.getLastName());
				}

				if (updateTeacher.getPassword() != null) {
					teacher.get().setPassword(Encryption.getPasswordEncoded(updateTeacher.getPassword()));
				}
				if (updateTeacher.getTelNum() != null) {
					teacher.get().setTelNumber(updateTeacher.getTelNum());
				}
				if (updateTeacher.isActive() == true) {
					teacher.get().setActive(true);
				}
				teacherRepo.save(teacher.get());
				logger.info("Teacher is updated - id: "+teacher.get().getId()+", "+teacher.get().getFirstName()+" "+teacher.get().getLastName());
				return new ResponseEntity<TeacherEntity>(teacher.get(), HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found"),
						HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			logger.error("Exception executing TeacherController.updateTeacher, " + e.getMessage());
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can find teacher by Id
	 * 
	 * @param teacherId
	 * @return teacher entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{teacherId}")
	public ResponseEntity<?> findTeacherById(@PathVariable Integer teacherId) {
		try {
			Optional<TeacherEntity> teacher = teacherRepo.findById(teacherId);
			if (teacher.isPresent()) {
				logger.info("Teacher is seen - id: "+teacher.get().getId()+", "+teacher.get().getFirstName()+" "+teacher.get().getLastName());
				return new ResponseEntity<TeacherEntity>(teacher.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found"),
						HttpStatus.NOT_FOUND);
			}

		} catch (

		Exception e) {
			logger.error("Exception executing TeacherController.findTeacherById, " + e.getMessage());
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can deactivate teacher
	 * 
	 * @param teacherId
	 * @return deactivated teacher entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/deactivate/{teacherId}")
	public ResponseEntity<?> deactivateTeacher(@PathVariable Integer teacherId) {
		try {
			Optional<TeacherEntity> teacher = teacherRepo.findById(teacherId);
			if (teacher.isPresent()) {
				teacher.get().setActive(false);
				teacherRepo.save(teacher.get());
				logger.info("Teacher is deactivated - id: "+teacher.get().getId()+", "+teacher.get().getFirstName()+" "+teacher.get().getLastName());
				return new ResponseEntity<TeacherEntity>(teacher.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found"),
						HttpStatus.NOT_FOUND);

			}
		} catch (Exception e) {
			logger.error("Exception executing TeacherController.deactivateTeacher, " + e.getMessage());
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Admin can delete teacher
	 * 
	 * @param teacherId
	 * @return deleted teacher entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{teacherId}")
	public ResponseEntity<?> deleteTeacherById(@PathVariable Integer teacherId) {
		try {
			Optional<TeacherEntity> teacher = teacherRepo.findById(teacherId);
			if (teacher.isPresent()) {
				teacherRepo.delete(teacher.get());
				logger.info("Teacher is deleted - id: "+teacher.get().getId()+", "+teacher.get().getFirstName()+" "+teacher.get().getLastName());
				return new ResponseEntity<TeacherEntity>(teacher.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found"),
						HttpStatus.NOT_FOUND);
			}

		} catch (

		Exception e) {
			logger.error("Exception executing TeacherController.deleteTeacherById, " + e.getMessage());
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can list all teachers
	 * 
	 * @return list of all teachers
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/")
	public ResponseEntity<?> getAllTeachers() {
		try {
			List<TeacherEntity> allTeachers = (List<TeacherEntity>) teacherRepo.findAll();
			return new ResponseEntity<List<TeacherEntity>>(allTeachers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing TeacherController.getAllTeachers, " + e.getMessage());
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Add acreditation to teacher
	 * 
	 * @param teacherId
	 * @param subjectId
	 * @return teacher entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{teacherId}/subject/{subjectId}")
	public ResponseEntity<?> addAcreditation(@PathVariable Integer teacherId, @PathVariable Integer subjectId) {
		try {
			Optional<TeacherEntity> teacher = teacherRepo.findById(teacherId);
			Optional<SubjectEntity> subject = subjectRepo.findById(subjectId);
			if (!teacher.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found"),
						HttpStatus.NOT_FOUND);
			}
			if (!subject.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Subject not found"),
						HttpStatus.NOT_FOUND);
			}
			teacher.get().getAccreditation().add(subject.get());
			teacherRepo.save(teacher.get());
			logger.info("Accreditation is added for teacher - id: "+teacher.get().getId()+", "+teacher.get().getFirstName()+" "+teacher.get().getLastName());
			return new ResponseEntity<TeacherEntity>(teacher.get(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing TeacherController.addAcreditation, " + e.getMessage());
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can see teacher's accreditation
	 * 
	 * @param teacherId
	 * @return accreditation
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{teacherId}/subjects/")
	public ResponseEntity<?> getAccreditation(@PathVariable Integer teacherId) {
		try {
			Optional<TeacherEntity> teacher = teacherRepo.findById(teacherId);
			if (!teacher.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found"),
						HttpStatus.NOT_FOUND);
			}
			logger.info("Accreditation is seen for teacher - id: "+teacher.get().getId()+", "+teacher.get().getFirstName()+" "+teacher.get().getLastName());
			return new ResponseEntity<List<SubjectEntity>>(teacher.get().getAccreditation(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing TeacherController.addAcreditation, " + e.getMessage());
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}

}
