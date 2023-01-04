package com.batch.demo.vo.vo;

import java.util.List;

import com.batch.demo.vo.Visitable;
import com.batch.demo.vo.Visitor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "EMP")
@EqualsAndHashCode(callSuper=false)
@ToString
@Data
public final class Emp extends BaseVo implements Visitable<Emp>{
    
    @Column(name = "EMP_ID", nullable = false)
    private java.util.UUID empId;
    @Column(name = "EMP_NAME", nullable = false, length = 100)
    private String empName;
    @Column(name = "PASSWORD", nullable = false, length = 100)
    private String password;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "EMP_ID", referencedColumnName = "EMP_ID", insertable = false, updatable = false)
	private List<EmpRole> empRoles;

    public Emp create(String empName, String password) {
        this.empName = empName;
        this.password = password;
        return this;
    }

    public void addEmpRole(EmpRole empRole) {
        this.empRoles.add(empRole);
    }

    @Override
    public Emp accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
