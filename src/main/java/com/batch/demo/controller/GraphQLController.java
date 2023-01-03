package com.batch.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import com.batch.demo.batch.util.processor.ProcessMethod;
import com.batch.demo.repository.BaseRepository;
import com.batch.demo.security.EmpPrincipal;
import com.batch.demo.service.PubSubService;
import com.batch.demo.vo.vo.Emp;
import com.batch.demo.vo.vo.EmpRole;
import com.batch.demo.vo.vo.Message;
import com.batch.demo.vo.vo.Role;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    @Autowired
    private PubSubService service;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @QueryMapping
    public Emp getEmpInfo(@Argument("empName") String empName, @Argument("password") String password){
        Emp emp = new Emp().create(empName, password);
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
        Emp emp = new Emp().create(empName, password);
        emp = processor.visit(emp);
        return empRepo.save(emp);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Emp upgrade(@Argument("empName") String empName, @Argument("password") String password){
        Emp emp = new Emp().create(empName, password);
        Example<Emp> example = Example.of(emp);
        Emp result = empRepo.findOne(example).orElse(null);
        EmpRole empRole = new EmpRole();
        empRole.setRole(new Role().defaultRole("VIP"));
        result.addEmpRole(empRole);
        result = processor.visit(result);
        return empRepo.save(result);
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
            return empRepo.save(emp);
        }
        return null;
    }

    /******************************** PubSub ********************************/

    // @QueryMapping
    // public Mono<List<Message>> getMessages() {
    //     return this.service.getMessages().block(Duration.ofSeconds(10l));
    // }

    @MutationMapping
    public Mono<Message> createMessage(@Argument("empName") String empName, @Argument("message") String message) {
        return this.service.createMessage(empName, message);
    }

    @SubscriptionMapping
    public Flux<Message> messages(){
        return this.service.getMessageFlux();
    }
    
}
