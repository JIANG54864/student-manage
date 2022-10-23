package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.ContactWay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.fatmansoft.teach.models.PreAdmission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreAdmissionRepository extends JpaRepository<org.fatmansoft.teach.models.PreAdmission,Integer>{

    @Query(value = "select max(id) from PreAdmission  ")
    Integer getMaxId();

    @Query(value="from PreAdmission where student.id=?1" )
    List<PreAdmission> findByStudent(Integer studentId);

    List<PreAdmission> findByStudentId(Integer studentId);

    //@Query(value = "from PreAdmission where ?1='' or studentNum like %?1% or name like %?1% ")
    //List<PreAdmission> findPreAdmissionListByNumName(String numName);

    //@Query(value = "select * from student  where ?1='' or studentNum like %?1% or studentName like %?1% ", nativeQuery = true)
    //List<Student> findStudentListByNumNameNative(String numName);

}
