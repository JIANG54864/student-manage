package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Selection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectionRepository extends JpaRepository<Selection,Integer> {
    @Query(value = "select max(id) from Selection  ")
    Integer getMaxId();
    List<Selection> findByStudentId(Integer studentId);
    List<Selection> findByCourseCourseNum(String courseNum);
    @Query(value="select s.course from Selection s where s.student.id=?1")
    List<Course> findCourseList(Integer studentId);

}
//package org.fatmansoft.teach.repository;

//public interface SelectionRepository {
//}
