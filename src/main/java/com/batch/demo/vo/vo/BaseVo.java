package com.batch.demo.vo.vo;

import java.io.Serializable;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@MappedSuperclass
@Data
public class BaseVo implements Serializable  {
    @Id 
    @GeneratedValue
    private java.util.UUID uuid;
}
