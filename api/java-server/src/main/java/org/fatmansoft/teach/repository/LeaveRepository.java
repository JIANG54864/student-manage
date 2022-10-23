//package org.fatmansoft.teach.repository;

//public interface LeaveRepository {
//}
package org.fatmansoft.teach.repository;


import org.fatmansoft.teach.models.Leave;
import org.fatmansoft.teach.models.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave,Integer> {
    @Query(value = "select max(id) from Leave  ")
    Integer getMaxId();
    List<Leave> findByStudentId(Integer studentId);

}