package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "contact_way",
        uniqueConstraints = {
        })

public class ContactWay {
    @Id
    private Integer id;
    @OneToOne
    @JoinColumn(name="studentId")
    private Student student;


    @Size(max = 2)
    private String sex;
    @Size(max = 50)
    private String name;
    private Integer tel;
    private Integer QQNum;
    private String email;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQQNum() {
        return QQNum;
    }

    public void setQQNum(Integer QQNum) {
        this.QQNum = QQNum;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getTel() {
        return tel;
    }

    public void setTel(Integer tel) {
        this.tel = tel;
    }


    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }


}
