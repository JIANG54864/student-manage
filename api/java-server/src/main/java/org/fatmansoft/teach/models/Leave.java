//package org.fatmansoft.teach.models;

//public class Leave {
//}
package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.xml.crypto.Data;
import java.util.Date;

@Entity
@Table(	name = "leave",
        uniqueConstraints = {
        })
public class Leave {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "journalId")
    private Journal journal;

    private String leaveReason;//请假理由

    private Date outtime;//出校时间

    private Date intime;//返校时间

    private Date applytime;//申请时间

    private String pass;//是否通过许可

    private String detailouttime;

    private String detailintime;

    private String detailapplytime;

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

    public Journal getJournal() {
        return journal;
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public Date getOuttime() {
        return outtime;
    }

    public void setOuttime(Date outtime) {
        this.outtime = outtime;
    }

    public Date getIntime() {
        return intime;
    }

    public void setIntime(Date intime) {
        this.intime = intime;
    }

    public Date getApplytime() {
        return applytime;
    }

    public void setApplytime(Date applytime) {
        this.applytime = applytime;
    }
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getDetailouttime() {
        return detailouttime;
    }

    public void setDetailouttime(String detailouttime) {
        this.detailouttime = detailouttime;
    }

    public String getDetailintime() {
        return detailintime;
    }

    public void setDetailintime(String detailintime) {
        this.detailintime = detailintime;
    }

    public String getDetailapplytime() {
        return detailapplytime;
    }

    public void setDetailapplytime(String detailapplytime) {
        this.detailapplytime = detailapplytime;
    }
}