package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "sum",
        uniqueConstraints = {
        })

public class Sum {
        @Id
        private Integer id;
        @OneToOne
        @JoinColumn(name="studentId")
        private Student student;

        @Size(max = 20)
        private String studentNum;
        @Size(max = 2)
        private String sex;
        @Size(max = 200)
        private String name;
        private String solAc;//社会工作能力：社会实践、校外实习
        private Integer solAcScore;//社会工作能力得分
        private String act;//文体活动：体育活动、文艺演出
        private Integer actScore;//文体得分
        private String Sci;//科研能力：竞赛、科技、创新。培训
        private Integer sciScore;//科研得分
        private String stuScore;//成绩信息
        private Integer Sum;
        private Double summ;//综合得分

        public String getAct() {return act;}

        public void setAct(String act) {this.act = act;}

        public Integer getActScore() {return actScore;}

        public void setActScore(Integer actScore) {this.actScore = actScore;}

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getSci() {
                return Sci;
        }

        public void setSci(String sci) {
                Sci = sci;
        }

        public Integer getSciScore() {
                return sciScore;
        }

        public void setSciScore(Integer sciScore) {
                sciScore = sciScore;
        }


        public String getStuScore() {
                return stuScore;
        }

        public void setStuScore(String stuScore) {
                this.stuScore = stuScore;
        }

        public String getSex() {
                return sex;
        }

        public void setSex(String sex) {
                this.sex = sex;
        }

        public String getSolAc() {
                return solAc;
        }

        public void setSolAc(String solAc) {
                this.solAc = solAc;
        }

        public Integer getSolAcScore() {
                return solAcScore;
        }

        public void setSolAcScore(Integer solAcScore) {
                this.solAcScore = solAcScore;
        }

        public Student getStudent() {
                return student;
        }

        public void setStudent(Student student) {
                this.student = student;
        }

        public Integer getSum() {
                return Sum;
        }

        public void setSum(Integer sum) {
                Sum = sum;
        }

        public String getStudentNum() {return studentNum;}

        public void setStudentNum(String studentNum) {
                this.studentNum = studentNum;
        }

        public Double getSumm() {return summ;}

        public void setSumm(Double summ) {this.summ = summ;}
}
