package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.models.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework,Integer> {
    @Query(value = "select max(id) from Homework  ")
    Integer getMaxId();
    List<Homework> findByStudentId(Integer studentId);
    List<Homework> findByCourseCourseNum(String courseNum);
    @Query(value="select s.course from Homework s where s.student.id=?1")
    List<Course> findCourseList(Integer studentId);

}
//package org.fatmansoft.teach.repository;

//public interface HomeworkRepository {
//}
