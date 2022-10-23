package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.fatmansoft.teach.models.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Integer> {
    Optional<Student> findByStudentNum(String studentNum);
    List<Student> findByStudentName(String studentName);

    //@Query(value = "select * from student  where id like %?1% ")
    //List<Student> findByStudentId(Integer studentId);

    @Query(value = "select max(id) from Student  ")
    Integer getMaxId();

    @Query(value = "from Student where ?1='' or studentNum like %?1% or studentName like %?1% ")
    List<Student>findStudentListByNumName(String numName);

    @Query(value = "select * from student  where ?1='' or studentNum like %?1% or studentName like %?1% ", nativeQuery = true)
    List<Student> findStudentListByNumNameNative(String numName);

   // @Query(value="select s.dept from Dept s where s.student.id=?1")
    //List<Course> findCourseList(Integer studentId);

}
