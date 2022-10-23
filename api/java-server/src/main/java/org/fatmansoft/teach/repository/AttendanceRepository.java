package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance,Integer> {
    @Query(value = "select max(id) from Attendance  ")
    Integer getMaxId();
    List<Attendance> findByStudentId(Integer studentId);
    List<Attendance> findByCourseCourseNum(String courseNum);
    @Query(value="select s.course from Attendance s where s.student.id=?1")
    List<Course> findCourseList(Integer studentId);
    @Query(value = "select * from attendance  where ?1='' or courseId like %?1% or studentId like %?1% ", nativeQuery = true)
    List<Attendance> findAttendanceListByNumNameNative(String numName);

}
//package org.fatmansoft.teach.repository;

//ublic interface AttendanceRepository {
//}
