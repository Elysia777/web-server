package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.StudentActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentActivityRepository extends JpaRepository<StudentActivity,Integer> {
    @Query(value = "select max(activityId) from Activity")
    Integer getMaxId();
    @Query(value = "from StudentActivity sa where ?1='' or sa.activity.activityName like %?1% ")
    List<StudentActivity> findStudentActivityListByActivityName(String activityName);
    @Query(value = "from StudentActivity sa where ?1='' or sa.student.person.num like %?1% ")
    List<StudentActivity> findByStudentNum(String num);

}
