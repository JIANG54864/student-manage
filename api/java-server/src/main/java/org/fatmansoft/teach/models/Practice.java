package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "practice",
        uniqueConstraints = {
        })

public class Practice {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    public Integer getId() {return id;}

    public void setId(Integer id) {this.id = id;}

    private String pracname;
    public String getPracname() {return pracname;}
    public void setPracname(String pracname) {this.pracname = pracname;}

    private String practype;
    public String getPractype() {return practype;}
    public void setPractype(String practype) {this.practype = practype;}

    private String praclevel;
    public String getPraclevel() {return praclevel;}
    public void setPraclevel(String praclevel) {this.praclevel = praclevel;}

    private String pracwin;
    public String getPracwin() {return pracwin;}
    public void setPracwin(String pracwin) {this.pracwin = pracwin;}

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
