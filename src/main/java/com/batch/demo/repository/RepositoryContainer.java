// package com.batch.demo.repository;

// import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.data.jpa.repository.Query;
// import org.springframework.stereotype.Component;
// import org.springframework.stereotype.Repository;

// import com.batch.demo.vo.Emp;
// import com.batch.demo.vo.EmpRole;
// import com.batch.demo.vo.EmpRole.CompositePK;
// import com.batch.demo.vo.Role;

// import lombok.Getter;

// @Repository
// public class RepositoryContainer {

//     public interface EmpRepo extends BaseRepository<Emp, Long>{
//         // example of native sql
//         // @Query(name = "EmpRepository.login", nativeQuery = true)
//         // Emp findByEmpNameAndPassword(String empName, String password);
//     };
//     public interface RoleRepo extends BaseRepository<Role, Long>{};
//     public interface EmpRoleRepo extends BaseRepository<EmpRole, CompositePK>{};

//     @Getter
//     @Component
//     public class Container{
//         @Autowired
//         public EmpRepo empRepo;
//         @Autowired
//         public RoleRepo roleRepo;
//         @Autowired 
//         public EmpRoleRepo empRoleRepo;
//     }
// }