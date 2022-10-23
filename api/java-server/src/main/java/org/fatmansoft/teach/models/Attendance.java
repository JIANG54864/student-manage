package org.fatmansoft.teach.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(	name = "attendance",
        uniqueConstraints = {
        })
public class Attendance {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    private String situation;//是否正常上课，上课情况

    private String reason;//缺勤原因

    private Date time;//上课时间

    private String leave;//是否请假

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLeave(){
        return leave;
    }

    public void setLeave(String leave){
        this.leave = leave;
    }
}

//package org.fatmansoft.teach.models;

//public class Attendance {
//}
