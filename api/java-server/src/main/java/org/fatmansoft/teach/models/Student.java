package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;


@Entity
@Table(	name = "student",
        uniqueConstraints = {
        })
public class Student {
    @Id
    private Integer id;

    @OneToOne
    @JoinColumn(name = "deptId")
    private Dept dept;

    @OneToOne
    @JoinColumn(name = "provinceId")
    private Province province;

    @NotBlank
    @Size(max = 20)
    private String studentNum;

    @Size(max = 50)
    private String studentName;
    @Size(max = 2)
    private String sex;
    private Integer age;
    private Integer s_class;//班级
    private Integer s_grade;//年级
    @Size(max = 50)
    private String ethnic;//民族
    private String profession;//专业
    private String instructor;//辅导员
    private String provinceName;
    private String deptName;

    @Size(max = 50)
    private String hobby;//兴趣爱好

    private Date birthday;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setDept(Dept dept) {this.dept = dept;}

    public Dept getDept() {return dept;}

    public void setProfession(String profession) {this.profession=profession;}

    public String getProfession() {return profession;}

    public void setS_class(Integer s_class) {this.s_class=s_class;}

    public Integer getS_class() { return s_class;}

    public void setS_grade(Integer s_grade) {this.s_grade=s_grade;}

    public Integer getS_grade() { return s_grade;}

    public String getEthnic() {return ethnic;}

    public void setEthnic(String ethnic) {this.ethnic = ethnic;}

    public void setInstructor(String instructor) {this.instructor = instructor;}

    public String getInstructor() {return instructor;}

    public Province getProvince() {return province;}

    public void setProvince(Province province) {this.province = province;}

    public String getHobby() { return hobby; }

    public void setHobby(String hobby) { this.hobby = hobby; }

    public String getDeptName() {return deptName;}

    public void setDeptName(String deptName) {this.deptName = deptName;}

    public String getProvinceName() {return provinceName;}

    public void setProvinceName(String provinceName) {this.provinceName = provinceName;}
}

