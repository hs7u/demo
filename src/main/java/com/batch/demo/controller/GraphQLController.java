package com.batch.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import com.batch.demo.repository.BaseRepository;
import com.batch.demo.vo.vo.Emp;
import com.batch.demo.vo.vo.Role;

@Controller
public class GraphQLController {

    @Autowired
    private BaseRepository<Emp, UUID> empRepo;
	@Autowired
    private BaseRepository<Role, UUID> roleRepo;

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
}
