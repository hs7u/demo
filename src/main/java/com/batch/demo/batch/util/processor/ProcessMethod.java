package com.batch.demo.batch.util.processor;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.batch.demo.repository.BaseRepository;
import com.batch.demo.util.VoUtil;
import com.batch.demo.vo.Visitor;
import com.batch.demo.vo.vo.Emp;
import com.batch.demo.vo.vo.EmpRole;
import com.batch.demo.vo.vo.Role;

@Component
public class ProcessMethod implements Visitor{

    @Autowired
    private BaseRepository<Role, UUID> roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Emp visit(Emp item) {   
        UUID uuid = java.util.UUID.randomUUID();
        if(item.getEmpId() == null)
            item.setEmpId(uuid);
        // 存加密過的才能讓security 驗證
        if (!VoUtil.isEncoded(item.getPassword()))
            item.setPassword(passwordEncoder.encode(item.getPassword()));
        // 沒權限資料
        if(item.getEmpRoles() == null || item.getEmpRoles().size() == 0){
            EmpRole empRole = new EmpRole();
            empRole.setEmpId(uuid);
            empRole.setRole(new Role().defaultRole("USER"));
            if(item.getEmpRoles() == null)
                item.setEmpRoles(List.of(empRole));
            else if (item.getEmpRoles().size() == 0)
                item.addEmpRole(empRole);
        }
        
        settingEmpRole(item.getEmpRoles().stream().filter(er -> null == er.getRoleId()).toArray(EmpRole[]::new));
        return item;
    }

    private void settingEmpRole(EmpRole... empRole) {        
        for (int i = 0; i < empRole.length; i++) {
            Role role = getRoleInfo(empRole[i].getRole());
            empRole[i].setRoleId(role.getRoleId());
            empRole[i].setRole(role);
        } 
    }

    private Role getRoleInfo(Role role){
        return roleRepo.findOne(Example.of(role)).get();
    }
}
