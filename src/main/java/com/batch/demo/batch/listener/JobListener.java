package com.batch.demo.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JobListener {
    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        log.info("before job execute: " + jobExecution.getJobInstance().getJobName());
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        log.info("after job execute: " + jobExecution.getJobInstance().getJobName());
    }
}
