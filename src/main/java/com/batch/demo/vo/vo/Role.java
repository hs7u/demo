package com.batch.demo.vo.vo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "ROLE")
@EqualsAndHashCode(callSuper=false)
@ToString
@Data
public final class Role extends BaseVo {
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ROLE_ID", nullable = false)
    private Long roleId;
    @Basic
    @Column(name = "ROLE_NAME", nullable = false, length = 100)
    private String roleName;
    @Basic
    @Column(name = "DESCRIPTION", length = 100)
    private String description;

    public Role defaultRole(String roleName) {
        this.roleName = roleName;
        return this;
    }
}