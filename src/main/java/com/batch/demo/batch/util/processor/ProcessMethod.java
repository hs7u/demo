package com.batch.demo.batch.util.processor;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import com.batch.demo.repository.BaseRepository;
import com.batch.demo.vo.Visitor;
import com.batch.demo.vo.vo.Emp;
import com.batch.demo.vo.vo.EmpRole;
import com.batch.demo.vo.vo.Role;

@Component
public class ProcessMethod implements Visitor{

    @Autowired
    private BaseRepository<Role, UUID> roleRepo;

    @Override
    public Emp visit(Emp item) {
        EmpRole emprole = new EmpRole();
        Role roleExam = new Role();
        item.setEmpId(java.util.UUID.randomUUID());
        roleExam.setRoleName("USER");
        Role curRole = roleRepo.findOne(Example.of(roleExam)).get();
        emprole.setEmpId(item.getEmpId());
        emprole.setRoleId(curRole.getRoleId());
        emprole.setRole(curRole);
        item.setEmpRoles(Arrays.asList(emprole));
        return item;
    }
}
