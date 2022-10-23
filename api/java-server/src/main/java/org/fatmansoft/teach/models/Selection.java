package org.fatmansoft.teach.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(	name = "selection",
        uniqueConstraints = {
        })
public class Selection {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    private String attribute;

    private Date stime;

    private Date etime;

    private String weeknum;

    private String week;

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

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Date getStime() {
        return stime;
    }

    public void setStime(Date stime) {
        this.stime = stime;
    }

    public Date getEtime() {
        return etime;
    }

    public void setEtime(Date etime) {
        this.etime = etime;
    }

    public String getWeeknum() {
        return weeknum;
    }

    public void setWeeknum(String weeknum) {
        this.weeknum = weeknum;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }


}
