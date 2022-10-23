package org.fatmansoft.teach.repository;


import org.fatmansoft.teach.models.ContactWay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.fatmansoft.teach.models.SocialRel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocialRelRepository extends JpaRepository<org.fatmansoft.teach.models.SocialRel,Integer>{
    @Query(value = "select max(id) from SocialRel  ")
    Integer getMaxId();

    @Query(value="from SocialRel where student.id=?1" )
    List<SocialRel> findByStudent(Integer studentId);

    //@Query(value = "from SocialRel where ?1='' or studentNum like %?1% or name like %?1% ")
   // List<SocialRel> findSocialRelListByNumName(String numName);

    List<SocialRel> findByStudentId(Integer studentId);
}
