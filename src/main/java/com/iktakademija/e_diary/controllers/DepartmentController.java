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
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.dto.DepartmentDTO;
import com.iktakademija.e_diary.dto.UpdateDepartmentDTO;
import com.iktakademija.e_diary.entities.ClassEntity;
import com.iktakademija.e_diary.entities.DepartmentEntity;
import com.iktakademija.e_diary.entities.StudentEntity;
import com.iktakademija.e_diary.entities.TeacherEntity;
import com.iktakademija.e_diary.repositories.ClassRepository;
import com.iktakademija.e_diary.repositories.DepartmentRepository;
import com.iktakademija.e_diary.repositories.StudentRepository;
import com.iktakademija.e_diary.repositories.TeacherRepository;
import com.iktakademija.e_diary.security.Views;
import com.iktakademija.e_diary.utils.RESTError;

@RestController
@RequestMapping(value = "/api/v1/department")
public class DepartmentController {
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	@Autowired
	private TeacherRepository teacherRepo;
	@Autowired
	private ClassRepository classRepo;
	@Autowired
	private DepartmentRepository departmentRepo;
	@Autowired
	private StudentRepository studentRepo;

	/**
	 * Admin can add new department
	 * 
	 * @param newDepartment
	 * @return created department
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> addNewDepartment(@RequestBody DepartmentDTO newDepartment) {

		try {
			DepartmentEntity department = new DepartmentEntity();
			
			department.setName(newDepartment.getName());
			if (newDepartment.getClassEntity() != null) {
				Optional<ClassEntity> classEntity = classRepo.findById(newDepartment.getClassEntity().getId());
				if (classEntity.isPresent()) {
					department.setClassEntity(classEntity.get());
				} else
					return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Class not found"), HttpStatus.NOT_FOUND);
			}

			if (newDepartment.getClassElder() != null) {
				Optional<TeacherEntity> classElder = teacherRepo.findById(newDepartment.getClassElder().getId());
				if (classElder.isPresent() && classElder.get().isActive() == true) {
					department.setClassElder(classElder.get());
				} else
					return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found"), HttpStatus.NOT_FOUND);
			}
			departmentRepo.save(department);
			logger.info("Department created - id: "+department.getId());
			return new ResponseEntity<DepartmentEntity>(department, HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error("Exception executing DepartmentController.addNewDepartment, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Admin can list all departments
	 * 
	 * @return list of all departments
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/")
	public ResponseEntity<?> getAllDepartments() {
		try {
			List<DepartmentEntity> allDepartments = (List<DepartmentEntity>) departmentRepo.findAll();
			logger.info("All department are seen");
			return new ResponseEntity<List<DepartmentEntity>>(allDepartments, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing DepartmentController.getAllDepartments, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Admin can get department by id
	 * 
	 * @param departmentId
	 * @return department entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{departmentId}")
	public ResponseEntity<?> getDepartmentsById(@PathVariable Integer departmentId) {
		try {
			Optional<DepartmentEntity> department = departmentRepo.findById(departmentId);
			if (department.isPresent()) {
				logger.info("Department is seen - id: "+department.get().getId());
				return new ResponseEntity<DepartmentEntity>(department.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Department not found"), HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error("Exception executing DepartmentController.getDepartmentById, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	/**
	 * Admin can get all department by class
	 * @param classId
	 * @return list of departments
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET, value="/class/{classId}")
	public ResponseEntity<?> getDepartmentByClass(@PathVariable Integer classId){
		try {
			Optional<ClassEntity> classEntity=classRepo.findById(classId);
			if(classEntity.isPresent()) {
				return new ResponseEntity<List<DepartmentEntity>>(departmentRepo.findByClassEntity(classEntity.get()), HttpStatus.OK);
			}
			else return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Class not found"), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Exception executing DepartmentController.getDepartmentByClass, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can update existing department
	 * 
	 * @param departmentId
	 * @param updateDepartment
	 * @return updated department
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{departmentId}")
	public ResponseEntity<?> updateDepartment(@PathVariable Integer departmentId,
			@RequestBody UpdateDepartmentDTO updateDepartment) {
		try {
			Optional<DepartmentEntity> department = departmentRepo.findById(departmentId);
			if (department.isPresent()) {
				if (updateDepartment.getName() != null) {
					department.get().setName(updateDepartment.getName());
				}
				if (updateDepartment.getClassEntity() != null) {
					Optional<ClassEntity> classEntity = classRepo.findById(updateDepartment.getClassEntity().getId());
					if (classEntity.isPresent()) {
						department.get().setClassEntity(classEntity.get());
					} else
						return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Class not found"), HttpStatus.NOT_FOUND);
				}
				if (updateDepartment.getClassElder() != null) {
					Optional<TeacherEntity> teacher = teacherRepo.findById(updateDepartment.getClassElder().getId());
					if (teacher.isPresent()) {
						department.get().setClassElder(teacher.get());
					} else
						return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found"),
								HttpStatus.NOT_FOUND);
				}
				departmentRepo.save(department.get());
				logger.info("Department is updated - id: "+department.get().getId());
				return new ResponseEntity<DepartmentEntity>(department.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Department not found"), HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error("Exception executing DepartmentController.updateDepartment, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can delete existing department
	 * 
	 * @param departmentId
	 * @return deleted DepartmentEntity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{departmentId}")
	public ResponseEntity<?> deleteDepartmentById(@PathVariable Integer departmentId) {
		try {
			Optional<DepartmentEntity> department = departmentRepo.findById(departmentId);
			if (department.isPresent()) {
				departmentRepo.delete(department.get());
				logger.info("Department is deleted - id: "+department.get().getId());
				return new ResponseEntity<DepartmentEntity>(department.get(), HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Department not found"), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Exception executing DepartmentController.deleteDepartmentById, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can add student into department
	 * 
	 * @param studentId
	 * @param departmentId
	 * @return department entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "addstudent/{studentId}/into/{departmentId}")
	public ResponseEntity<?> addStudentsToDepartment(@PathVariable Integer studentId,
			@PathVariable Integer departmentId) {
		try {
			Optional<StudentEntity> student = studentRepo.findById(studentId);
			Optional<DepartmentEntity> department = departmentRepo.findById(departmentId);
			if (!student.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Student not found"), HttpStatus.NOT_FOUND);
			}
			if (!department.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Department not found"), HttpStatus.NOT_FOUND);
			}
			if(department.get().getStudents().size()>25) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.BAD_REQUEST.value(), "The department is full"), HttpStatus.BAD_REQUEST);
			}
			department.get().getStudents().add(student.get());
			departmentRepo.save(department.get());
			studentRepo.save(student.get());
			logger.info("Student - id: "+studentId+", "+student.get().getFirstName()+" "+student.get().getLastName()+" is added to department id "+departmentId);
			return new ResponseEntity<DepartmentEntity>(department.get(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing DepartmentController.addStudnetsToDepartment, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	/**
	 * Admin can add Class Elder to the department
	 * @param teacherId
	 * @param departmentId
	 * @return department entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.PUT, value="/teacher/{teacherId}/department/{departmentId}")
	public ResponseEntity<?> addClassElderToDepartment(@PathVariable Integer teacherId, @PathVariable Integer departmentId){
		try {
			Optional<TeacherEntity> teacher = teacherRepo.findById(teacherId);
			Optional<DepartmentEntity> department = departmentRepo.findById(departmentId);
			if (!teacher.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found"), HttpStatus.NOT_FOUND);
			}
			if (!department.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Department not found"), HttpStatus.NOT_FOUND);
			}
			department.get().setClassElder(teacher.get());
			departmentRepo.save(department.get());
			logger.info("Teacher - id: "+teacherId+", "+teacher.get().getFirstName()+" "+teacher.get().getLastName()+" is added as class elder to department id "+departmentId);
			return new ResponseEntity<DepartmentEntity>(department.get(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing DepartmentController.addStudnetsToDepartment, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
