package com.batch.demo.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

import com.batch.demo.vo.vo.BaseVo;

@Primary
public interface BaseRepository<T extends BaseVo, E extends Serializable> extends JpaRepository<T, E> {
    public abstract List<T> findAll();
}
