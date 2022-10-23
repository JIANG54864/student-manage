package org.fatmansoft.teach.repository;


import org.fatmansoft.teach.models.Consume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsumeRepository extends JpaRepository<Consume,Integer> {
    @Query(value = "select max(id) from Consume  ")
    Integer getMaxId();
    List<Consume> findByStudentId(Integer studentId);

}
