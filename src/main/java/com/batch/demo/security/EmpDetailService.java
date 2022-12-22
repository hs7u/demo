package com.batch.demo.security;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.batch.demo.repository.BaseRepository;
import com.batch.demo.vo.vo.Emp;
import com.batch.demo.vo.vo.Role;

@Service
public class EmpDetailService implements UserDetailsService{

    @Autowired
    private BaseRepository<Emp, UUID> empRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Emp nameEx = new Emp();
        nameEx.setEmpName(username);
        Example<Emp> nameExample = Example.of(nameEx);
        Optional<Emp> optional = empRepo.findOne(nameExample);
        if(!optional.isPresent()) throw new UsernameNotFoundException(String.format("User %s not found", username));        
        Emp emp = optional.get();
        Role[] roles = emp.getEmpRoles().stream().map(er -> er.getRole()).toArray(Role[]::new);
        emp.setPassword(passwordEncoder.encode(emp.getPassword()));
        
        return new EmpPrincipal(emp, RoleHelper.castToGrantedAuthorities(roles));
    }
    
}
