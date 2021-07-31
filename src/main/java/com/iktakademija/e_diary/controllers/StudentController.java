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
import com.iktakademija.e_diary.dto.StudentDTO;
import com.iktakademija.e_diary.dto.UpdateStudentDTO;
import com.iktakademija.e_diary.entities.DepartmentEntity;
import com.iktakademija.e_diary.entities.ParentEntity;
import com.iktakademija.e_diary.entities.StudentEntity;
import com.iktakademija.e_diary.entities.UserEntity;
import com.iktakademija.e_diary.enumerations.ERole;
import com.iktakademija.e_diary.repositories.DepartmentRepository;
import com.iktakademija.e_diary.repositories.RoleRepository;
import com.iktakademija.e_diary.repositories.StudentRepository;
import com.iktakademija.e_diary.repositories.UserRepository;
import com.iktakademija.e_diary.security.Views;
import com.iktakademija.e_diary.utils.Encryption;
import com.iktakademija.e_diary.utils.RESTError;

@RestController
@RequestMapping(value = "api/v1/student")
public class StudentController {

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	private StudentRepository studentRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private DepartmentRepository departmentRepo;
	@Autowired
	private UserRepository userRepo;

	/**
	 * Admin can add new student
	 * 
	 * @param newStudent
	 * @param result
	 * @return created student
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> addNewStudent(@Valid @RequestBody StudentDTO newStudent, BindingResult result) {

		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			Optional<StudentEntity> student1=Optional.ofNullable(studentRepo.findByJmbg(newStudent.getJmbg()));
			if(student1.isPresent()){
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.BAD_REQUEST.value(), "User with this jmbg already exists"),
						HttpStatus.BAD_REQUEST);
			}
			Optional<UserEntity> student2=Optional.ofNullable(userRepo.findByUsername(newStudent.getUsername()));
			if(student2.isPresent()){
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.BAD_REQUEST.value(), "Username already in use"),
						HttpStatus.BAD_REQUEST);
			}
			
				StudentEntity student = new StudentEntity();
				student.setJmbg(newStudent.getJmbg());
				student.setLastName(newStudent.getLastName());
				student.setFirstName(newStudent.getFirstName());
				student.setPassword(Encryption.getPasswordEncoded(newStudent.getPassword()));
				student.setRole(roleRepo.findByroleName(ERole.ROLE_STUDENT));
				student.setUsername(newStudent.getUsername());
				studentRepo.save(student);
				logger.info("New student is added - id: "+student.getId()+", "+student.getFirstName()+" "+student.getLastName());
				return new ResponseEntity<StudentEntity>(student, HttpStatus.CREATED);
			
		} catch (Exception e) {
			logger.error("Exception executing StudentController.addNewStudent, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can update existing student
	 * 
	 * @param studentId
	 * @param updateStudent
	 * @param result
	 * @return updated student entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{studentId}")
	public ResponseEntity<?> updateStudent(@PathVariable Integer studentId,
			@Valid @RequestBody UpdateStudentDTO updateStudent, BindingResult result) {

		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			Optional<StudentEntity> student = studentRepo.findById(studentId);
			if (student.isPresent()) {

				if (updateStudent.getFirstName() != null) {
					student.get().setFirstName(updateStudent.getFirstName());
				}
				if (updateStudent.getLastName() != null) {
					student.get().setLastName(updateStudent.getLastName());
				}
				if (updateStudent.getJmbg() != null) {
					Optional<StudentEntity> studentEntity = Optional
							.ofNullable(studentRepo.findByJmbg(updateStudent.getJmbg()));
					if (studentEntity.isPresent()) {
						return new ResponseEntity<RESTError>(new RESTError(HttpStatus.BAD_REQUEST.value(), "Jmbg already in use"),
								HttpStatus.BAD_REQUEST);
					}

					student.get().setJmbg(updateStudent.getJmbg());
				}
				if (updateStudent.getPassword() != null) {
					student.get().setPassword(Encryption.getPasswordEncoded(updateStudent.getPassword()));
				}
				if (updateStudent.isActive()==true) {
					student.get().setActive(true);
				}
				if (updateStudent.isActive()==false) {
					student.get().setActive(false);
				}

				studentRepo.save(student.get());
				logger.info("Student is updated - id: "+student.get().getId()+", "+student.get().getFirstName()+" "+student.get().getLastName());
				return new ResponseEntity<StudentEntity>(student.get(), HttpStatus.OK);
			} else
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Student not found"), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			logger.error("Exception executing StudentController.updateStudent, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admins can list data for existing student
	 * 
	 * @param studentId
	 * @return
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{studentId}")
	public ResponseEntity<?> findStudentById(@PathVariable Integer studentId) {
		try {
			Optional<StudentEntity> student = studentRepo.findById(studentId);
			if (student.isPresent()) {
				logger.info("Student is seen - id: "+student.get().getId()+", "+student.get().getFirstName()+" "+student.get().getLastName());
				return new ResponseEntity<StudentEntity>(student.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Student not found"), HttpStatus.NOT_FOUND);
			}

		} catch (

		Exception e) {
			logger.error("Exception executing AdminController.findAdminById, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can deactivate some student
	 * 
	 * @param studentId
	 * @return deactivated student
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/deactivate/{studentId}")
	public ResponseEntity<?> deactivateStudent(@PathVariable Integer studentId) {
		try {
			Optional<StudentEntity> student = studentRepo.findById(studentId);
			if (student.isPresent()) {
				student.get().setActive(false);
				studentRepo.save(student.get());
				logger.info("Student is deactivated - id: "+student.get().getId()+", "+student.get().getFirstName()+" "+student.get().getLastName());
				return new ResponseEntity<StudentEntity>(student.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Student not found"), HttpStatus.NOT_FOUND);

			}
		} catch (Exception e) {
			logger.error("Exception executing StudentController.deactivateStudent, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Admin can delet student
	 * 
	 * @param studentId
	 * @return deleted student entity
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{studentId}")
	public ResponseEntity<?> deleteStudentById(@PathVariable Integer studentId) {
		try {
			Optional<StudentEntity> student = studentRepo.findById(studentId);
			if (student.isPresent()) {
				studentRepo.delete(student.get());
				logger.info("Student is deleted - id: "+student.get().getId()+", "+student.get().getFirstName()+" "+student.get().getLastName());
				return new ResponseEntity<StudentEntity>(student.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Student not found"), HttpStatus.NOT_FOUND);
			}

		} catch (

		Exception e) {
			logger.error("Exception executing StudentController.deleteStudentById, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Admin can list all students
	 * 
	 * @return list of all students
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/")
	public ResponseEntity<?> getAllStudents() {
		try {
			List<StudentEntity> allStudents = (List<StudentEntity>) studentRepo.findAll();
			return new ResponseEntity<List<StudentEntity>>(allStudents, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing StudentController.getAllStudents, " + e.getMessage());
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
	@RequestMapping(method = RequestMethod.PUT, value = "/{studentId}/department/{departmentId}")
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
			if(department.get().getStudents().contains(student.get())&&student.get().getDepartment()!=null) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.BAD_REQUEST.value(), "Department already contain that user."), HttpStatus.BAD_REQUEST);
			}
			student.get().setDepartment(department.get());
			studentRepo.save(student.get());
			logger.info("Student - id: "+student.get().getId()+", "+student.get().getFirstName()+" "+student.get().getLastName()+" is added to department id "+departmentId);
			return new ResponseEntity<StudentEntity>(student.get(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing StudentController.addStudnetsToDepartment, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * List all students in one department
	 * 
	 * @param departmentId
	 * @return
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/department/{departmentId}")
	public ResponseEntity<?> getAllStudentsByDepartment(@PathVariable Integer departmentId) {
		try {
			Optional<DepartmentEntity> department = departmentRepo.findById(departmentId);
			if (!department.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Department not found"), HttpStatus.NOT_FOUND);
			}

			List<StudentEntity> allStudents = studentRepo.findByDepartment(department.get());
			return new ResponseEntity<List<StudentEntity>>(allStudents, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing StudentController.getAllStudentsByDepartment, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	/**
	 * Admin can list parents for some student
	 * @param studentId
	 * @return list of parents
	 */
	@JsonView(Views.Admin.class)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{studentId}/parent/")
	public ResponseEntity<?> getParentForStudent(@PathVariable Integer studentId) {
		try {
			Optional<StudentEntity> student = studentRepo.findById(studentId);
			if (!student.isPresent()) {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.NOT_FOUND.value(), "Student not found"), HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<List<ParentEntity>>(student.get().getParents(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception executing StudentController.getParentForStudent, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}
}
