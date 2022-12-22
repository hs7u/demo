package com.batch.demo.batch.util.reader;

public abstract class BaseSetting<T> {
    public String delimiter;
    public Class<T> clazz;
    public String filePath;
    public Boolean strict;
    public Integer linesToSkip;
    public Class<? extends CsvColumns> columns;
}
