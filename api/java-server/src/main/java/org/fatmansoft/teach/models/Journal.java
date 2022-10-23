
package org.fatmansoft.teach.models;

        import javax.persistence.*;
        import java.util.Date;

@Entity
@Table(	name = "journal",
        uniqueConstraints = {
        })
public class Journal {
    @Id
    private Integer id;

  //  @ManyToOne
   // @JoinColumn(name = "leaveId")
    //private Leave leave;

   // @ManyToOne
   // @JoinColumn(name = "courseId")
  //  private Course course;

    private String consumeRecord;

    private String leaveRecord;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConsumeRecord() {
        return consumeRecord;
    }

    public void setConsumeRecord(String consumeRecord) {
        this.consumeRecord = consumeRecord;
    }

    public String getLeaveRecord() {
        return leaveRecord;
    }

    public void setLeaveRecord(String leaveRecord) {
        this.leaveRecord = leaveRecord;
    }
}
