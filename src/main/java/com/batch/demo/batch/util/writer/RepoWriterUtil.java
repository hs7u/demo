package com.batch.demo.batch.util.writer;

import java.io.Serializable;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.batch.demo.repository.BaseRepository;
import com.batch.demo.vo.vo.BaseVo;

@Component
public class RepoWriterUtil<T extends BaseVo, E extends Serializable> implements ItemWriter<T> {

    @Autowired
    private BaseRepository<T, E> repo;

    @Override
    public void write(Chunk<? extends T> chunk) throws Exception {
        repo.saveAll(chunk);        
    }
    
}
