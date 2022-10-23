package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "family_member",
        uniqueConstraints = {
        })
public class FamilyMember {
    @Id
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @Size(max = 50)
    private String name;
    private String sex;
    private String rel;
    private Integer relTel;
    private String relWorkAdd;
    private String relName;

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

    public void setRel(String rel) { this.rel = rel; }

    public String getRel() {
        return rel;
    }

    public Integer getRelTel() { return relTel; }

    public void setRelTel(Integer relTel) { this.relTel = relTel; }

    public String getRelWorkAdd() { return relWorkAdd; }

    public void setRelWorkAdd(String relWorkAdd) { this.relWorkAdd = relWorkAdd; }

    public String getRelName() {return relName;}

    public void setRelName(String relName) {this.relName = relName;}
}
