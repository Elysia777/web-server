package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Course 数据操作接口，主要实现Course数据的查询操作
 */

@Repository
public interface CourseRepository extends JpaRepository<Course,Integer> {
    @Query(value = "from Course where ?1='' or num like %?1% or name like %?1% ")
    List<Course> findCourseListByNumName(String numName);
    Optional<Course> findCourseByCourseId(Integer courseId);
    @Query(value="from Course where teacher.person.num = ?1 and (?2 ='' or num like %?2% or name like %?2%)")
    List<Course> findCourseListByTeacherNumAndNumName(String teacherNum,String numName);
}
