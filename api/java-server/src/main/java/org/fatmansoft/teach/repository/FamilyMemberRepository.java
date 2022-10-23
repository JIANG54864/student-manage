package org.fatmansoft.teach.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.fatmansoft.teach.models.FamilyMember;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyMemberRepository extends JpaRepository<org.fatmansoft.teach.models.FamilyMember,Integer>{
    @Query(value = "from FamilyMember where student_id=?1 ")
    List<FamilyMember> findByStudentId(Integer studentId);

    //@Query(value="from FamilyMember where ?1='' or studentNum like %?1% or studentName like %?1% ")
    //List<FamilyMember> findByStudent(String numName);

    @Query(value="from FamilyMember where student.id=?1" )
    List<FamilyMember> findByStudent(Integer studentId);

    @Query(value = "select max(id) from FamilyMember  ")
    Integer getMaxId();

    //@Query(value="select s.rel from FamilyMember s where s.student.id=?1")
    //List<FamilyMember> findStudentList(Integer studentId);



    //value="from FamilyMember where student.id=?1"
}