package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Sum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SumRepository extends JpaRepository<org.fatmansoft.teach.models.Sum,Integer>{
    @Query(value = "select max(id) from Sum  ")
    Integer getMaxId();

    @Query(value="from Sum where student.id=?1" )
    List<Sum> findByStudent(Integer studentId);

    @Query(value = "from Sum where ?1='' or studentNum like %?1% or name like %?1% ")
    List<Sum> findSumListByNumName(String numName);

    @Query(value = "select * from sum  where ?1='' or studentNum like %?1% or name like %?1% ", nativeQuery = true)
    List<Sum> findSumListByNumNameNative(String numName);
}
