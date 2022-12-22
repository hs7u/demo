package com.batch.demo.batch.listener;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterProcess;
import org.springframework.batch.core.annotation.AfterRead;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.annotation.BeforeProcess;
import org.springframework.batch.core.annotation.BeforeRead;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.annotation.BeforeWrite;
import org.springframework.batch.core.annotation.OnProcessError;
import org.springframework.batch.core.annotation.OnReadError;
import org.springframework.batch.core.annotation.OnWriteError;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StepListener {
    @BeforeChunk
    public void beforeChunk(ChunkContext context) {
        log.info("before chunk excute commit count = : " + context.getStepContext().getStepExecution().getCommitCount());
    }

    @AfterChunk
    public void afterChunk(ChunkContext context) {
        log.info("after chunk excute commit count = : " + context.getStepContext().getStepExecution().getCommitCount());
    }

    @BeforeStep
    public void breforeStep(StepExecution stepExecution) {
        log.info("before step execute commit count = : " + stepExecution.getCommitCount());
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        log.info("after step execute commit count = : " + stepExecution.getCommitCount());
    }

    @BeforeRead
    public void beforeRead() {
        log.info("before read excute: ======>>>>>>");
    }

    @AfterRead
    public <T> void afterRead(T item) {
        log.info("after read excute: " + item.toString());
    }
    
    @BeforeProcess
    public <T> void beforeProcess(T item) {
        log.info("before process excute: " + item.toString());
    }

    @AfterProcess
    public <T, S> void afterProcess(T item, S result) {
        log.info("after process excute: " + item.toString() + "===>>> to ===>>>" + result.toString());
    }
    @BeforeWrite
    public <S> void beforeWrite(org.springframework.batch.item.Chunk<? extends S> itemChunk) {
        log.info("before write Chunk size = : " + itemChunk.size());
    }
    @AfterWrite
    public <S> void afterWrite(org.springframework.batch.item.Chunk<? extends S> itemChunk) {
        log.info("after write Chunk size = : " + itemChunk.size());
    }
    @OnReadError
    public void onReadError(Exception exception){
        log.info("on Read Error: " + exception.getMessage());
    }
    @OnProcessError
    public <T> void onProcessError(T item, Exception exception){
        log.info("on Process Error: " + item.toString());
    }
    @OnWriteError
    public <S> void onWriteError(Exception exception,org.springframework.batch.item.Chunk<? extends S> items){
        log.info("on Write Error: " + exception.getMessage());
        for (S s : items) {
            log.info(s.toString());
        }
    }
}
