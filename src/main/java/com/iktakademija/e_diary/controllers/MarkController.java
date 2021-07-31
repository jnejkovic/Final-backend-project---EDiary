package com.iktakademija.e_diary.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.dto.MarkDTO;
import com.iktakademija.e_diary.dto.TeacherMarkDTO;
import com.iktakademija.e_diary.dto.UpdateMarkDTO;
import com.iktakademija.e_diary.entities.DepartmentEntity;
import com.iktakademija.e_diary.entities.MarkEntity;
import com.iktakademija.e_diary.entities.ParentEntity;
import com.iktakademija.e_diary.entities.StudentEntity;
import com.iktakademija.e_diary.entities.SubjectEntity;
import com.iktakademija.e_diary.entities.TeacherDepartmentSubject;
import com.iktakademija.e_diary.entities.TeacherEntity;
import com.iktakademija.e_diary.repositories.AdminRepository;
import com.iktakademija.e_diary.repositories.DepartmentRepository;
import com.iktakademija.e_diary.repositories.MarkRepository;
import com.iktakademija.e_diary.repositories.ParentRepository;
import com.iktakademija.e_diary.repositories.StudentRepository;
import com.iktakademija.e_diary.repositories.SubjectRepository;
import com.iktakademija.e_diary.repositories.TDSRepository;
import com.iktakademija.e_diary.repositories.TeacherRepository;
import com.iktakademija.e_diary.security.Views;
import com.iktakademija.e_diary.services.EmailServiceImp;
import com.iktakademija.e_diary.services.MarkDAO;
import com.iktakademija.e_diary.services.UserServiceImp;
import com.iktakademija.e_diary.utils.RESTError;

@RestController
@RequestMapping(value = "/api/v1/mark")
public class MarkController {

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TeacherRepository teacherRepo;
	@Autowired
	private StudentRepository studentRepo;
	@Autowired
	private MarkRepository markRepo;
	@Autowired
	private SubjectRepository subjectRepo;
	@Autowired
	private UserServiceImp userServiceImp;
	@Autowired
	private TDSRepository tdsRepo;
	@Autowired
	private EmailServiceImp emailServiceImp;
	@Autowired
	private ParentRepository parentRepo;
	@Autowired
	private MarkDAO markDAO;
	@Autowired
	private DepartmentRepository departmentRepo;

	/**
	 * Admin can add new mark
	 * 
	 * @param newMark
	 * @param result
	 * @return created mark
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> addNewMarkAdmin(@Valid @RequestBody MarkDTO newMark, BindingResult result) {
		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			Optional<StudentEntity> student = studentRepo.findById(newMark.getStudentId());
			if (!student.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Student not found"), HttpStatus.NOT_FOUND);
			}
			Optional<SubjectEntity> subject = subjectRepo.findById(newMark.getSubjectId());
			if (!subject.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Subject not found"), HttpStatus.NOT_FOUND);
			}
			Optional<TeacherEntity> teacher = teacherRepo.findById(newMark.getTeacherId());
			if (!subject.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found"), HttpStatus.NOT_FOUND);
			}
			Optional<TeacherDepartmentSubject> tds = Optional
					.ofNullable(tdsRepo.findByTeacherIdAndSubjectIdAndDepartmentId(teacher.get().getId(),
							subject.get().getId(), student.get().getDepartment().getId()));
			if (!tds.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher cannot grade this student"),
						HttpStatus.NOT_FOUND);
			}
			MarkEntity mark = new MarkEntity();
			mark.setMarkValue(newMark.getMarkValue());
			mark.setMarkDescription(newMark.getMarkDescription());
			mark.setCreatedAt(LocalDate.now());

			mark.setStudent(student.get());
			mark.setTeacher(teacher.get());
			mark.setSubject(subject.get());
			markRepo.save(mark);
			logger.info("Mark id - "+mark.getId()+" is created by admin");
			return new ResponseEntity<MarkEntity>(mark, HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error("Exception executing MarkController.addNewMarkAdmin, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can change existing mark
	 * 
	 * @param markId
	 * @param updateMark
	 * @param result
	 * @return updated mark entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{markId}")
	public ResponseEntity<?> updateMarkByAdmin(@PathVariable Integer markId,
			@Valid @RequestBody UpdateMarkDTO updateMark, BindingResult result) {
		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			Optional<MarkEntity> mark = markRepo.findById(markId);
			if (!mark.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Mark not found"), HttpStatus.NOT_FOUND);
			}
			if (updateMark.getMarkValue() != null) {
				mark.get().setMarkValue(updateMark.getMarkValue());
			}
			if (updateMark.getMarkDescription() != null) {
				mark.get().setMarkDescription(updateMark.getMarkDescription());
			}
			mark.get().setUpdatedAt(LocalDate.now());
			markRepo.save(mark.get());
			logger.info("Mark id - "+mark.get().getId()+" is updated by admin");
			return new ResponseEntity<MarkEntity>(mark.get(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing MarkController.updateMarkByAdmin, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Teacher can gave mark to student
	 * 
	 * @param subjectId
	 * @param studentId
	 * @param mark
	 * @param result
	 * @return MarkEntity
	 * @throws Exception
	 */
	@JsonView(Views.Teacher.class)
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.POST, value = "/subject/{subjectId}/student/{studentId}")
	public ResponseEntity<?> assessment(@PathVariable Integer subjectId, @PathVariable Integer studentId,
			@Valid @RequestBody TeacherMarkDTO mark, BindingResult result) throws Exception {

		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			String username = userServiceImp.getCurrentUsername();
			TeacherEntity teacher = teacherRepo.findByUsername(username);
			Optional<StudentEntity> student = studentRepo.findById(studentId);
			if (!student.isPresent() || student.get().isActive() == false) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Student not found, or not active"),
						HttpStatus.NOT_FOUND);
			}
			Optional<SubjectEntity> subject = subjectRepo.findById(subjectId);
			if (!subject.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Subject not found"), HttpStatus.NOT_FOUND);
			}
			Optional<TeacherDepartmentSubject> tds = Optional
					.ofNullable(tdsRepo.findByTeacherIdAndSubjectIdAndDepartmentId(teacher.getId(), subjectId,
							student.get().getDepartment().getId()));
			if (!tds.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.UNAUTHORIZED.value(), "Teacher cannot grade this student"),
						HttpStatus.UNAUTHORIZED);
			}
			MarkEntity markEntity = new MarkEntity();
			markEntity.setMarkValue(mark.getMarkValue());
			markEntity.setMarkDescription(mark.getMarkDescription());
			markEntity.setCreatedAt(LocalDate.now());
			markEntity.setStudent(student.get());
			markEntity.setTeacher(teacher);
			markEntity.setSubject(subject.get());
			markRepo.save(markEntity);
			emailServiceImp.sendTemplateMessage(markEntity);
			logger.info("Teacher "+teacher.getFirstName()+" "+teacher.getLastName()+" create mark - "+markEntity.getId());
			return new ResponseEntity<MarkEntity>(markEntity, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing MarkController.assessment, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Teacher can update Mark he gave
	 * 
	 * @param markId
	 * @param updateMark
	 * @return updated MarkEntity
	 */
	@JsonView(Views.Teacher.class)
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.PUT, value = "/updateteacher/{markId}")
	public ResponseEntity<?> updateMarkByTeacher(@PathVariable Integer markId, @RequestBody UpdateMarkDTO updateMark) {
		try {
			String username = userServiceImp.getCurrentUsername();
			TeacherEntity teacher = teacherRepo.findByUsername(username);
			Optional<MarkEntity> mark = markRepo.findById(markId);
			if (!mark.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Mark not found"), HttpStatus.NOT_FOUND);
			}
			if (mark.get().getTeacher().getId().equals(teacher.getId())) {
				if (updateMark.getMarkValue() != null) {
					mark.get().setMarkValue(updateMark.getMarkValue());
				}
				if (updateMark.getMarkDescription() != null) {
					mark.get().setMarkDescription(updateMark.getMarkDescription());
				}
				mark.get().setUpdatedAt(LocalDate.now());
				markRepo.save(mark.get());
				logger.info("Teacher "+teacher.getFirstName()+" "+teacher.getLastName()+" update mark - "+mark.get().getId());
				return new ResponseEntity<MarkEntity>(mark.get(), HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.UNAUTHORIZED.value(), "Teacher cannot update this mark"),
						HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			logger.error("Exception executing MarkController.updateMarkByTeacher, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can find Mark by Id
	 * 
	 * @param markId
	 * @return MarkEntity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{markId}")
	public ResponseEntity<?> findMarkByIdAdmin(@PathVariable Integer markId) {
		try {
			Optional<MarkEntity> mark = markRepo.findById(markId);
			if (mark.isPresent()) {
				logger.info("Admin saw mark - "+mark.get().getId());
				return new ResponseEntity<MarkEntity>(mark.get(), HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Mark not found"), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Exception executing MarkController.findMarkByIdAdmin, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Teacher can fing Mark by Id
	 * 
	 * @param markId
	 * @return MarkEntity
	 */
	@JsonView(Views.Teacher.class)
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.GET, value = "/teachermark/{markId}")
	public ResponseEntity<?> findMarkByIdTeacher(@PathVariable Integer markId) {
		try {
			String username = userServiceImp.getCurrentUsername();
			TeacherEntity teacher = teacherRepo.findByUsername(username);
			Optional<MarkEntity> mark = markRepo.findById(markId);
			if (!mark.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Mark not found"), HttpStatus.NOT_FOUND);
			}
			if (mark.get().getTeacher().getId().equals(teacher.getId())) {
				logger.info("Teacher "+teacher.getFirstName()+" "+teacher.getLastName()+" saw mark - "+mark.get().getId());
				return new ResponseEntity<MarkEntity>(mark.get(), HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.UNAUTHORIZED.value(), "Teacher cannot see this mark"),
						HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			logger.error("Exception executing MarkController.findMarkByIdTeacher, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can delete Mark
	 * 
	 * @param markId
	 * @return deleted MarkEntity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{markId}")
	public ResponseEntity<?> deleteMarkAdmin(@PathVariable Integer markId) {
		try {
			Optional<MarkEntity> mark = markRepo.findById(markId);
			if (!mark.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Mark not found"), HttpStatus.NOT_FOUND);
			}
			markRepo.delete(mark.get());
			logger.info("Admin delete mark -  "+mark.get().getId());
			return new ResponseEntity<MarkEntity>(mark.get(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing MarkController.deleteMarkAdmin, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Teacher can delete Mark he gave
	 * 
	 * @param markId
	 * @return deleted MarkEntity
	 */
	@JsonView(Views.Teacher.class)
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.DELETE, value = "teacherdelete/{markId}")
	public ResponseEntity<?> deleteMarkTeacher(@PathVariable Integer markId) {
		try {
			String username = userServiceImp.getCurrentUsername();
			TeacherEntity teacher = teacherRepo.findByUsername(username);
			Optional<MarkEntity> mark = markRepo.findById(markId);
			if (!mark.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Mark not found"), HttpStatus.NOT_FOUND);
			}
			if (mark.get().getTeacher().getId().equals(teacher.getId())) {
				markRepo.delete(mark.get());
				logger.info("Teacher "+teacher.getFirstName()+" "+teacher.getLastName()+" deleted mark - "+mark.get().getId());
				return new ResponseEntity<MarkEntity>(mark.get(), HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.UNAUTHORIZED.value(), "Teacher cannot delete this mark"),
						HttpStatus.UNAUTHORIZED);

		} catch (Exception e) {
			logger.error("Exception executing MarkController.deleteMarkTeacher, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@JsonView(Views.Teacher.class)
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.GET, value = "/department/{departmentId}")
	public ResponseEntity<?> markBySubjectForTeacher(@PathVariable Integer departmentId) {
		try {
			String username = userServiceImp.getCurrentUsername();
			TeacherEntity teacher = teacherRepo.findByUsername(username);
			Optional<DepartmentEntity> department=departmentRepo.findById(departmentId);
			if(!department.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Department not found"), HttpStatus.NOT_FOUND);
			}
			logger.info("Teacher "+teacher.getFirstName()+" "+teacher.getLastName()+" saw all marks for his subject");
			return new ResponseEntity<List<MarkEntity>>(markDAO.findMarkBySubject(teacher, department.get()), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing MarkController.markBySubjectForTeacher, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		

	}

	/**
	 * Student can see his marks
	 * 
	 * @return list of marks
	 */
	@JsonView(Views.Private.class)
	@Secured("ROLE_STUDENT")
	@RequestMapping(method = RequestMethod.GET, value = "/student")
	public ResponseEntity<?> marksForStudent() {
		try {
			String username = userServiceImp.getCurrentUsername();

			StudentEntity student = studentRepo.findByUsername(username);
			logger.info("Student "+student.getFirstName()+" "+student.getLastName()+" saw his marks");
			return new ResponseEntity<List<MarkEntity>>(student.getMarks(), HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Exception executing MarkController.marksForStudent, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Parent can see list of marks for his children
	 * 
	 * @param index
	 * @return list of marks
	 */
	@JsonView(Views.Private.class)
	@Secured("ROLE_PARENT")
	@RequestMapping(method = RequestMethod.GET, value = "/children/{index}")
	public ResponseEntity<?> marksForChildren(@PathVariable Integer index) {
		try {
			String username = userServiceImp.getCurrentUsername();

			ParentEntity parent = parentRepo.findByUsername(username);
			List<StudentEntity> children = parent.getChildren();
			List<MarkEntity> marksForChildren = children.get(index).getMarks();
			logger.info("Parent "+parent.getFirstName()+" "+parent.getLastName()+" saw marks of his children");
			return new ResponseEntity<List<MarkEntity>>(marksForChildren, HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Exception executing MarkController.marksForChildren, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can see all marks
	 * 
	 * @return list of marks
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/")
	public ResponseEntity<?> findAllMarks() {
		try {
			List<MarkEntity> marks = (List<MarkEntity>) markRepo.findAll();
			logger.info("Admin saw all marks");
			return new ResponseEntity<List<MarkEntity>>(marks, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing MarkController.findAllMarks, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Teacher can see all marks he gave
	 * 
	 * @return list of marks
	 */
	@JsonView(Views.Teacher.class)
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.GET, value = "/teacher")
	public ResponseEntity<?> findMarkByTeacher() {
		try {
			String username = userServiceImp.getCurrentUsername();
			TeacherEntity teacher = teacherRepo.findByUsername(username);
			List<MarkEntity> marks = markRepo.findByTeacher(teacher);
			logger.info("Teacher "+teacher.getFirstName()+" "+teacher.getLastName()+" saw all marks he gave");
			return new ResponseEntity<List<MarkEntity>>(marks, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing MarkController.findMarkByTeacher, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}
}
