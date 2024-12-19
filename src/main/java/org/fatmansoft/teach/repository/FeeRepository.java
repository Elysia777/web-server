package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Fee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FeeRepository extends JpaRepository<Fee,Integer> {
    Optional<Fee> findByPersonPersonId(Integer personId);
    Optional<Fee> findByPersonNum(String num);
    List<Fee> findByPersonName(String name);
    Optional<Fee> findByStudentStudentIdAndDay(Integer studentId, String day);

    @Query(value= "from Fee where student.studentId=?1 order by day")
    List<Fee> findListByStudent(Integer studentId);

    @Query(value = "select sum(money) from Fee where student.studentId=?1 and day like ?2%")
    Double getMoneyByStudentIdAndDate(Integer studentId,String date);

    @Query(value = "from Fee where ?1='' or person.num like %?1% or person.name like %?1% ")
    List<Fee> findFeeListByNumName(String numName);

    @Query(value="select s from Fee s, User u where u.person.personId = s.person.personId and u.userId=?1")
    Optional<Fee> findByUserId(Integer userId);

    @Query(value = "from Fee where ?1='' or person.num like %?1% or person.name like %?1% ",
            countQuery = "SELECT count(studentId) from Student where ?1='' or person.num like %?1% or person.name like %?1% ")
    Page<Fee> findFeePageByNumName(String numName,  Pageable pageable);
}
