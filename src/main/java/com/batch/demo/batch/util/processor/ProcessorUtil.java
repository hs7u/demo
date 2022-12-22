package com.batch.demo.batch.util.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.batch.demo.vo.Visitable;

@Component
public class ProcessorUtil<T extends Visitable<T>> implements ItemProcessor<T, T>{

    @Autowired
    private ProcessMethod processMethod;
    
    @Override
    public T process(T item) throws Exception { 
        return item.accept(processMethod);       
    }
}
