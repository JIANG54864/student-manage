package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "pre_admission",
        uniqueConstraints = {
        })

public class PreAdmission {
    @Id
    private Integer id;
    @OneToOne
    @JoinColumn(name="studentId")
    private Student student;


    @Size(max = 2)
    private String sex;
    @Size(max = 50)
    private String name;
    private Integer ceeScore;
    private String highSchool;
    private Integer examReg;

    public Integer getCeeScore() {
        return ceeScore;
    }

    public void setCeeScore(Integer ceeScore) {
        this.ceeScore = ceeScore;
    }

    public Integer getExamReg() {
        return examReg;
    }

    public void setExamReg(Integer examReg) {
        this.examReg = examReg;
    }

    public String getHighSchool() {
        return highSchool;
    }

    public void setHighSchool(String highSchool) {
        this.highSchool = highSchool;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

}
