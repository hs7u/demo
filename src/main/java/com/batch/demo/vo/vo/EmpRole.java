package com.batch.demo.vo.vo;

import java.io.Serializable;

import com.batch.demo.vo.vo.EmpRole.CompositePK;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@IdClass(CompositePK.class)
@Entity
@Table(name = "EMP_ROLE")
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class EmpRole extends BaseVo {
    
    @Id
    @Column(name = "EMP_ID")
    private java.util.UUID empId;
    @Id
    @Column(name = "ROLE_ID")
    private Long roleId;
    
    @Data
    public static class CompositePK implements Serializable {
        private java.util.UUID uuid;
        private java.util.UUID empId;
        private Long roleId;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID", insertable = false, updatable = false)
    private Role role;
}
