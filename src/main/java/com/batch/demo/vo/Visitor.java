package com.batch.demo.vo;

import com.batch.demo.vo.vo.Emp;

public interface Visitor {
    public Emp visit(Emp item);
}
