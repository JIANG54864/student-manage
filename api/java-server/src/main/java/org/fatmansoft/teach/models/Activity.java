package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;


@Entity
@Table(	name = "activity",
        uniqueConstraints = {
        })

public class Activity {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    private String activityName;

    private Date activityTime;
    @Size(max = 100)
    private String activitySite;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Date getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(Date activityTime) {
        this.activityTime = activityTime;
    }

    public String getActivitySite() {
        return activitySite;
    }

    public void setActivitySite(String activitySite) {
        this.activitySite = activitySite;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
