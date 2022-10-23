package org.fatmansoft.teach.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(	name = "homework",
        uniqueConstraints = {
        })
public class Homework {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    private String teacher;//任课老师

    private String content;//课程内容

    private Date subtime;//作业提交时间

    private String fsituation;//完成情况

    private String homeworkNum;//第几次作业

    private String homeworkLevel;//作业程度：优秀、良好、及格和不及格

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

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public Date getSubtime() {
        return subtime;
    }

    public void setSubtime(Date subtime) {
        this.subtime = subtime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHomeworkLevel(){
        return homeworkLevel;
    }

    public void setHomeworkLevel(String homeworkLevel){
        this.homeworkLevel = homeworkLevel;
    }

    public String getFsituation(){
        return fsituation;
    }

    public void setFsituation(String fsituation){
        this.fsituation = fsituation;
    }

    public String getHomeworkNum(){
        return homeworkNum;
    }

    public void setHomeworkNum(String homeworkNum){
        this.homeworkNum = homeworkNum;
    }
}
//package org.fatmansoft.teach.models;

//public class Homework {
//}
