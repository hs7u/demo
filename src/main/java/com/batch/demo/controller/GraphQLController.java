package com.batch.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import com.batch.demo.batch.util.processor.ProcessMethod;
import com.batch.demo.repository.BaseRepository;
import com.batch.demo.security.EmpPrincipal;
import com.batch.demo.vo.vo.Emp;
import com.batch.demo.vo.vo.Role;

@Controller
public class GraphQLController {

    @Autowired
    private BaseRepository<Emp, UUID> empRepo;
	@Autowired
    private BaseRepository<Role, UUID> roleRepo;
    @Autowired
    private ProcessMethod processor;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @QueryMapping
    public Emp getEmpInfo(@Argument("empName") String empName, @Argument("password") String password){
        Emp emp = new Emp();
        emp.setEmpName(empName);
        emp.setPassword(password);
        Example<Emp> example = Example.of(emp);
        return empRepo.findOne(example).orElse(null);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @QueryMapping
    public List<Role> getRoles(){
        return roleRepo.findAll();
    }

    @MutationMapping
    public Emp register(@Argument("empName") String empName, @Argument("password") String password){
        Emp emp = new Emp();
        emp.setEmpName(empName);
        // 存加密過的才能讓security 驗證        
        emp.setPassword(passwordEncoder.encode(password));
        emp = processor.visit(emp);
        return empRepo.save(emp);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @MutationMapping
    public Emp updatePassword(@Argument("empId") UUID empId, @Argument("empName") String empName, 
        @Argument("password") String password, @Argument("newPassword") String newPassword){
        EmpPrincipal principal = (EmpPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Emp emp = principal.getEmp();
        if( emp != null && emp.getEmpId().equals(empId) && emp.getEmpName().equals(empName) 
            && passwordEncoder.matches(password, emp.getPassword())){
            // 存加密過的才能讓security 驗證                    
            emp.setPassword(passwordEncoder.encode(newPassword));
            empRepo.save(emp);
            return emp;
        }
        return null;
    }
}
