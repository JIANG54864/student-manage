package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(	name = "social_rel",
        uniqueConstraints = {
        })

public class SocialRel {
    @Id
    private Integer id;
    @OneToOne
    @JoinColumn(name = "studentId")
    private Student student;


    @Size(max = 2)
    private String sex;
    @Size(max = 50)
    private String name;
    private String polStatus;//政治面貌
    private Date leagueTime;//入团时间
    private Date partyTime;//入党时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getLeagueTime() {
        return leagueTime;
    }

    public void setLeagueTime(Date leagueTime) {
        this.leagueTime = leagueTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getPartyTime() {
        return partyTime;
    }

    public void setPartyTime(Date partyTime) {
        this.partyTime = partyTime;
    }

    public String getPolStatus() {
        return polStatus;
    }

    public void setPolStatus(String polStatus) {
        this.polStatus = polStatus;
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
