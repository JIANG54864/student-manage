package org.fatmansoft.teach.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.fatmansoft.teach.models.ContactWay;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactWayRepository extends JpaRepository<org.fatmansoft.teach.models.ContactWay,Integer>{

    @Query(value = "select max(id) from ContactWay  ")
    Integer getMaxId();

    @Query(value="from ContactWay where student.id=?1" )
    List<ContactWay> findByStudent(Integer studentId);

   // @Query(value = "from ContactWay where ?1='' or studentNum like %?1% or name like %?1% ")
    //List<ContactWay> findContactWayListByNumName(String numName);


    List<ContactWay> findByStudentId(Integer studentId);
    //@Query(value = "select * from student  where ?1='' or studentNum like %?1% or studentName like %?1% ", nativeQuery = true)
    //List<Student> findStudentListByNumNameNative(String numName);

}
