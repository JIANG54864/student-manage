package org.fatmansoft.teach.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.fatmansoft.teach.models.Activity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<org.fatmansoft.teach.models.Activity,Integer>{
    @Query(value = "select max(id) from Activity  ")
    Integer getMaxId();

    @Query(value = "from Activity where student_id=?1 ")
    List<Activity> findByStudentId(Integer studentId);

    //@Query(value="from FamilyMember where ?1='' or studentNum like %?1% or studentName like %?1% ")
    //List<FamilyMember> findByStudent(String numName);

    //@Query(value="from FamilyMember where student.id=?1" )
    //List<Activity> findByStudent(Integer studentId);

    @Query(value = "from Activity where ?1='' or activityName like %?1% or studentNum like %?1% or studentName like %?1% ")
    List<Activity> findActivityByNumName(String numName);



    //@Query(value="select s.rel from FamilyMember s where s.student.id=?1")
    //List<FamilyMember> findStudentList(Integer studentId);



    //value="from FamilyMember where student.id=?1"
}