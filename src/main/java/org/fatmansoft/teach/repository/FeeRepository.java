package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Fee;
import org.fatmansoft.teach.models.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeeRepository extends JpaRepository<Fee,Integer> {
    Optional<Fee> findByStudentStudentIdAndDay(Integer studentId, String day);
    @Query(value = "from Fee f where :numName='' or f.student.person.num like %:numName% or f.student.person.name like %:numName%")
    List<Fee> findFeeListByStudentNumName(@Param("numName") String numName);


    @Query(value= "from Fee where student.studentId=?1 order by day")
    List<Fee> findListByStudent(Integer studentId);


}
