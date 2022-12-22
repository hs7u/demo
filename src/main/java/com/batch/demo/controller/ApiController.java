package com.batch.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @Autowired
    private JobLauncher jobLauncher;
    
    @Qualifier("example")
    @Autowired
    private Job job;
           
    @GetMapping("/csrfToken")
    public CsrfToken csrfToken(CsrfToken csrfToken) {return csrfToken;}    
    @GetMapping("/myRole")
    public String[] myRole(){
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }   
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/launch")
    public Map<String, Object> load() throws Exception {
    	
    	System.out.println("Batch Starting...");
    	// create a Map to put a new JobParameter
        Map<String, JobParameter<?>> parameters = new HashMap<String, JobParameter<?>>();
        parameters.put("dateTime", new JobParameter<>(System.currentTimeMillis(),Long.class));
        // JobParameters的參數會在整個JOB執行過程中被共用,
    	JobParameters jobParameters = new JobParameters(parameters);
        // Every time the batch is run, a new JobExecution is created
        // JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        
        System.out.println("JobExecution: " + jobExecution.getStatus());
        Map<String, Object> finalStatus = new HashMap<>();
        finalStatus.put("CreateTime", jobExecution.getCreateTime());
        finalStatus.put("EndTime", jobExecution.getEndTime());
        finalStatus.put("Status", jobExecution.getStatus());

        return finalStatus;
    }
}
