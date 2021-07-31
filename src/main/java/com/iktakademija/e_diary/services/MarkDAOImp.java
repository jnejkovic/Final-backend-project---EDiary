package com.iktakademija.e_diary.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import com.iktakademija.e_diary.entities.DepartmentEntity;
import com.iktakademija.e_diary.entities.MarkEntity;
import com.iktakademija.e_diary.entities.SubjectEntity;
import com.iktakademija.e_diary.entities.TeacherEntity;

@Service
public class MarkDAOImp implements MarkDAO {
	@PersistenceContext
	private EntityManager em;

	@Override
	public List<MarkEntity> findMarkBySubject(TeacherEntity teacher, DepartmentEntity department) {

		

		/*
		 * select m.* from mark m left join student st on m.student=st.user_id left join
		 * department d on st.department=d.id left join teacher_department_subject tds
		 * on d.id=tds.department where tds.teacher=9 and tds.department=13;
		 */
		
		String sql="SELECT m FROM MarkEntity m LEFT JOIN FETCH m.student st LEFT JOIN FETCH st.department d LEFT JOIN FETCH d.teacherDepartmentSubject tds WHERE tds.teacher=:teacher and tds.department=:department";
		// invoke the SQL statement
		Query query = em.createQuery(sql);
		query.setParameter("teacher", teacher);
		query.setParameter("department", department);
		List<MarkEntity> retVal = query.getResultList();
		return retVal;
	}

}
