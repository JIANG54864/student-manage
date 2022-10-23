package org.fatmansoft.teach.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "dept",
        uniqueConstraints = {
        })


public class Dept {
        @Id
        private Integer id;
        @NotBlank
        @Size(max = 20)
        private String deptNum;

        @Size(max = 50)
        private String deptName;

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public String getDeptName() {
                return deptName;
        }

        public void setDeptName(String deptName) {
                this.deptName = deptName;
        }

        public String getDeptNum() {
                return deptNum;
        }

        public void setDeptNum(String deptNum) {
                this.deptNum = deptNum;
        }
}
