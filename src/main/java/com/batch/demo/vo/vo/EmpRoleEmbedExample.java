package com.batch.demo.vo.vo;
// package com.batch.demo.vo;

// import java.io.Serializable;

// import jakarta.persistence.AttributeOverride;
// import jakarta.persistence.AttributeOverrides;
// import jakarta.persistence.CascadeType;
// import jakarta.persistence.Column;
// import jakarta.persistence.Embeddable;
// import jakarta.persistence.Embedded;
// import jakarta.persistence.Entity;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.OneToOne;
// import jakarta.persistence.Table;
// import lombok.Data;
// import lombok.EqualsAndHashCode;

// @Entity
// @Table(name = "EMP_ROLE")
// @EqualsAndHashCode(callSuper=false)
// @Data
// public class EmpRoleEmbedExample extends BaseVo {
    
//     @Embedded
//     @AttributeOverrides({
//         @AttributeOverride( name = "uuid", column = @Column(name = "uuid",insertable=false, updatable=false)),
//         @AttributeOverride( name = "empId", column = @Column(name = "emp_id")),
//         @AttributeOverride( name = "roleId", column = @Column(name = "role_id"))
//     })
//     private CompositePK pk;

//     @Embeddable
//     @Data
//     public static class CompositePK implements Serializable {
//         private java.util.UUID uuid;
//         private Long empId;
//         private Long roleId;
//     }

//     @OneToOne(cascade = CascadeType.ALL)
//     @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID", insertable = false, updatable = false)
//     private Role role;
// }
