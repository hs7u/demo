package com.batch.demo.batch.listener;

import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RetryAndSkipListener {
    @OnSkipInRead
    public void onSkipInRead(Exception exception){
        logErrorData(exception.getMessage(),"on Skip In Read");
    }
    @OnSkipInProcess
    public <T> void onSkipInProcess(T item, Throwable throwable){
        logErrorData(item.toString(),"on Skip In Process");
    }
    @OnSkipInWrite
    public <S> void onSkipInWrite(S item, Throwable throwable){
        logErrorData(item.toString(),"on Skip In Write");
    }

    public void logErrorData(String itemStr, String step){
            String basic = "An error occured while processing the data. Below was the faulty input.";
            log.info(step + ": " + basic + itemStr);        
    }
    
}
