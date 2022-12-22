package com.batch.demo.vo;

public interface Visitable<E> {
    public E accept(Visitor visitor);
}
