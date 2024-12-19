package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity,Integer> {
    @Query(value = "select max(activityId) from Activity")
    Integer getMaxId();
    @Query(value = "from Activity where ?1='' or activityName like %?1% ")
    List<Activity> findActivityListByActivityName(String activityName);
    @Query(value = "from Activity where ?1='' or activityName like %?1% ")
    Optional<Activity> findActivityByActivityName(String activityName);

    @Override
    Optional<Activity> findById(Integer activityId);
}
