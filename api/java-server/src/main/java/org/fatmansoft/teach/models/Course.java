package org.fatmansoft.teach.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "course",
        uniqueConstraints = {
        })
public class Course  {
    @Id
    private Integer id;
    @NotBlank
    @Size(max = 20)
    private String courseNum;

    @Size(max = 50)
    private String courseName;
    private Integer credit;
    @Size(max = 50)
    private String preCourse;
    private String textbookName;

    //   @Size(max = 50)
    private String textbookVersion;//教材版本
    // @Size(max = 2)
    private String courseware;//课件


    private String material;//参考资料

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(String courseNum) {
        this.courseNum = courseNum;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public String getPreCourse() {
        return preCourse;
    }

    public void setPreCourse(String preCourse) {
        this.preCourse = preCourse;
    }

    public String getTextbookName() {
        return textbookName;
    }

    public void setTextbookName(String textbookName) {
        this.textbookName = textbookName;
    }

    public String getTextbookVersion() {
        return textbookVersion;
    }

    public void setTextbookVersion(String textbookVersion) {
        this.textbookVersion = textbookVersion;
    }

    public String getCourseware() {
        return courseware;
    }

    public void setCourseware(String courseware) {
        this.courseware = courseware;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

}
