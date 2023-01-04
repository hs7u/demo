package com.batch.demo.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.batch.demo.vo.vo.BaseVo;

@Primary
public interface BaseRepository<T extends BaseVo, E extends Serializable> extends JpaRepository<T, E> {
    public abstract List<T> findAll();
    @Query(value = "SELECT COUNT(0) FROM EMP E WHERE E.EMP_NAME LIKE 'test%'", nativeQuery = true)
    public int getCountAllEmp();
    @Modifying
    @Query(value = "DELETE FROM EMP E WHERE E.EMP_NAME LIKE 'test%'", nativeQuery = true)
    public void deleteAllEmp();
}
