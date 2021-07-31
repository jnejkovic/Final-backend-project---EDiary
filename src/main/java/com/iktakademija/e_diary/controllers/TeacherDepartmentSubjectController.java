package com.iktakademija.e_diary.controllers;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.dto.UpdateTDS;
import com.iktakademija.e_diary.entities.DepartmentEntity;
import com.iktakademija.e_diary.entities.SubjectEntity;
import com.iktakademija.e_diary.entities.TeacherDepartmentSubject;
import com.iktakademija.e_diary.entities.TeacherEntity;
import com.iktakademija.e_diary.repositories.DepartmentRepository;
import com.iktakademija.e_diary.repositories.SubjectRepository;
import com.iktakademija.e_diary.repositories.TDSRepository;
import com.iktakademija.e_diary.repositories.TeacherRepository;
import com.iktakademija.e_diary.security.Views;
import com.iktakademija.e_diary.utils.RESTError;

@RestController
@RequestMapping(value = "api/v1/teacherdepartmentsubject")
public class TeacherDepartmentSubjectController {

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TeacherRepository teacherRepo;
	@Autowired
	private DepartmentRepository departmentRepo;
	@Autowired
	private SubjectRepository subjectRepo;
	@Autowired
	private TDSRepository tdsRepo;

	/**
	 * Admin can add new TeacherDepartmentSubjectEntity
	 * 
	 * @param teacherId
	 * @param departmentId
	 * @param subjectId
	 * @return created TeacherDepartmentSubjectEntity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> addNewTDS(@RequestParam Integer teacherId, @RequestParam Integer departmentId,
			@RequestParam Integer subjectId) {
		try {
			TeacherDepartmentSubject tds = new TeacherDepartmentSubject();
			Optional<TeacherEntity> teacher = teacherRepo.findById(teacherId);
			if (!teacher.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found"),
						HttpStatus.NOT_FOUND);
			}
			Optional<DepartmentEntity> department = departmentRepo.findById(departmentId);
			if (!department.isPresent()) {
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Department not found"), HttpStatus.NOT_FOUND);
			}
			Optional<SubjectEntity> subject = subjectRepo.findById(subjectId);
			if (!subject.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Subject not found"),
						HttpStatus.NOT_FOUND);
			}
			if (teacher.get().getAccreditation().contains(subject.get())
					&& subject.get().getClassEntity() == department.get().getClassEntity()
					&& tdsRepo.findByTeacherIdAndSubjectIdAndDepartmentId(teacher.get().getId(), subject.get().getId(),
							department.get().getId()) == null) {
				tds.setTeacher(teacher.get());
				tds.setDepartment(department.get());
				tds.setSubject(subject.get());
				tdsRepo.save(tds);
				logger.info("Teacher Department Subject link is created - id: " + tds.getId());
				return new ResponseEntity<TeacherDepartmentSubject>(tds, HttpStatus.CREATED);
			}
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.BAD_REQUEST.value(), "Bad accreditation"),
					HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("Exception executing TeacherDepartmentSubjectController.addNewTDS, " + e.getMessage());
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Admin can update existing TeacherDepartmentSubjectEntity
	 * 
	 * @param tdsId
	 * @param updateTDS
	 * @return updated TeacherDepartmentSubjectEntity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{tdsId}")
	public ResponseEntity<?> updateTDS(@PathVariable Integer tdsId, @RequestBody UpdateTDS updateTDS) {
		try {
			Optional<TeacherDepartmentSubject> tds = tdsRepo.findById(tdsId);
			if (!tds.isPresent()) {
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "TeacherDepartmentSubject doesn't exist"),
						HttpStatus.NOT_FOUND);
			}
			if (updateTDS.getDepartmentId() != null) {
				Optional<DepartmentEntity> department = departmentRepo.findById(updateTDS.getDepartmentId());
				if (!department.isPresent()) {
					return new ResponseEntity<RESTError>(
							new RESTError(HttpStatus.NOT_FOUND.value(), "Department not found"), HttpStatus.NOT_FOUND);
				}
				if (tds.get().getTeacher().getAccreditation().contains(tds.get().getSubject())
						&& tds.get().getSubject().getClassEntity() == department.get().getClassEntity()) {
					tds.get().setDepartment(department.get());
				} else {
					return new ResponseEntity<RESTError>(
							new RESTError(HttpStatus.BAD_REQUEST.value(), "Bad accreditation"), HttpStatus.BAD_REQUEST);
				}
			}
			if (updateTDS.getSubjectId() != null) {
				Optional<SubjectEntity> subject = subjectRepo.findById(updateTDS.getSubjectId());
				if (!subject.isPresent()) {
					return new ResponseEntity<RESTError>(
							new RESTError(HttpStatus.NOT_FOUND.value(), "Subject not found"), HttpStatus.NOT_FOUND);
				}
				if (tds.get().getTeacher().getAccreditation().contains(subject.get())
						&& subject.get().getClassEntity() == tds.get().getDepartment().getClassEntity()) {
					tds.get().setSubject(subject.get());
				} else {
					return new ResponseEntity<RESTError>(
							new RESTError(HttpStatus.BAD_REQUEST.value(), "Bad accreditation"), HttpStatus.BAD_REQUEST);
				}
			}
			if (updateTDS.getTeacherId() != null) {
				Optional<TeacherEntity> teacher = teacherRepo.findById(updateTDS.getTeacherId());
				if (!teacher.isPresent()) {
					return new ResponseEntity<RESTError>(
							new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found"), HttpStatus.NOT_FOUND);
				}
				if (teacher.get().getAccreditation().contains(tds.get().getSubject())
						&& tds.get().getSubject().getClassEntity() == tds.get().getDepartment().getClassEntity()) {
					tds.get().setTeacher(teacher.get());
				} else {
					return new ResponseEntity<RESTError>(
							new RESTError(HttpStatus.BAD_REQUEST.value(), "Bad accreditation"), HttpStatus.BAD_REQUEST);
				}
			}
			tdsRepo.save(tds.get());
			logger.info("Teacher Department Subject link is updated - id: " + tds.get().getId());
			return new ResponseEntity<TeacherDepartmentSubject>(tds.get(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing TeacherDepartmentSubjectController.addNewTDS, " + e.getMessage());
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Admin can find TeacherDepartmentSubjectEntity by id
	 * 
	 * @param tdsId
	 * @return TeacherDepartmentSubjectEntity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{tdsId}")
	public ResponseEntity<?> findById(@PathVariable Integer tdsId) {
		try {
			Optional<TeacherDepartmentSubject> tds = tdsRepo.findById(tdsId);
			if (!tds.isPresent()) {
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "TeacherDepartmentSubject doesn't exist"),
						HttpStatus.NOT_FOUND);
			}
			logger.info("Teacher Department Subject link is seen - id: " + tds.get().getId());
			return new ResponseEntity<TeacherDepartmentSubject>(tds.get(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing TeacherDepartmentSubjectController.findById, " + e.getMessage());
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can get list of TeacherDepartmentSubjectEntity
	 * 
	 * @return list of TeacherDepartmentSubjectEntity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/")
	public ResponseEntity<?> findAll() {
		try {
			List<TeacherDepartmentSubject> tds = (List<TeacherDepartmentSubject>) tdsRepo.findAll();
			return new ResponseEntity<List<TeacherDepartmentSubject>>(tds, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing TeacherDepartmentSubjectController.findById, " + e.getMessage());
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can delete TeacherDepartmentSubjectEntity
	 * 
	 * @param tdsId
	 * @return deleted TeacherDepartmentSubjectEntity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{tdsId}")
	public ResponseEntity<?> deleteById(@PathVariable Integer tdsId) {
		try {
			Optional<TeacherDepartmentSubject> tds = tdsRepo.findById(tdsId);
			if (!tds.isPresent()) {
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "TeacherDepartmentSubject doesn't exist"),
						HttpStatus.NOT_FOUND);
			}
			tdsRepo.delete(tds.get());
			logger.info("Teacher Department Subject link is deleted - id: " + tds.get().getId());
			return new ResponseEntity<TeacherDepartmentSubject>(tds.get(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing TeacherDepartmentSubjectController.deleteById, " + e.getMessage());
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
