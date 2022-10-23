package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Practice;
import org.fatmansoft.teach.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PracticeRepository extends JpaRepository<Practice,Integer>{

    @Query(value = "select max(id) from Practice ")
    Integer getMaxId();

    @Query(value = "from Practice where ?1='' or pracname like %?1% or student.studentName like %?1% or student.studentNum like %?1% ")
    List<Practice> findPracticeBynumName(String pracname);

    @Query(value = "from Practice where student.id=?1 ")
    List<Practice> findByStudentId1(Integer studentId);

    List<Practice> findByStudentId(Integer studentId);
}