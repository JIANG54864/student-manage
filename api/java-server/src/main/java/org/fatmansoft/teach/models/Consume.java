//package org.fatmansoft.teach.models;

//public class Consume {
//}
package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.xml.crypto.Data;
import java.util.Date;

@Entity
@Table(	name = "consume",
        uniqueConstraints = {
        })
public class Consume {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    // @ManyToOne
    // @JoinColumn(name = "courseId")
    // private Course course;

    private String consumeReason;//消费方面

    private Date consumeTime;//消费时间

    private String sum;//消费金额

    private String banlance;//是否通过许可


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

    public String getConsumeReason() {
        return consumeReason;
    }

    public void setConsumeReason(String consumeReason) {
        this.consumeReason = consumeReason;
    }

    public Date getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(Date consumeTime) {
        this.consumeTime = consumeTime;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getBanlance() {
        return banlance;
    }

    public void setBanlance(String banlance) {
        this.banlance = banlance;
    }

}