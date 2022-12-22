package com.batch.demo.batch.util.reader;

import java.io.FileNotFoundException;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.UrlResource;
import org.springframework.util.ResourceUtils;

public class FlatFileItemReaderUtil<T> {

    // // Create FlatFileItemReader
    public FlatFileItemReader<T> createFlatFileItemReader(BaseSetting<T> settintClass) throws FileNotFoundException{
        FlatFileItemReader<T> flatFileItemReader = new FlatFileItemReader<>(); // ItemReader 的實作類別
		flatFileItemReader.setResource(new UrlResource(ResourceUtils.getURL(settintClass.filePath))); // 設定資料來源
		flatFileItemReader.setLinesToSkip(settintClass.linesToSkip);
		flatFileItemReader.setLineMapper(createLineMapper(settintClass));
        return flatFileItemReader;
    }
    
    private LineMapper<T> createLineMapper(BaseSetting<T> settintClass){
        DefaultLineMapper<T> defaultLineMapper = new DefaultLineMapper<>(); // LineMapper 的實作類別
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

		lineTokenizer.setDelimiter(settintClass.delimiter); // 資料間隔
		lineTokenizer.setStrict(settintClass.strict); // 資料行長驗證 若不能保證每行都有資料則關閉

        // 設定要蒐集的欄位 可用欄位名 或 index 創建FieldSet
		// lineTokenizer.setNames(this.names);
        setFields(settintClass.columns, lineTokenizer);

        // 依靠Java 命名規則跟上面創建的FieldSet 來創建目標型態物件 的屬性 在這是T
		BeanWrapperFieldSetMapper<T> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(settintClass.clazz);

		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);

		return defaultLineMapper;
    }    

    private <E extends CsvColumns> void setFields(Class<E> columns, DelimitedLineTokenizer lineTokenizer){
        int enumLength = columns.getEnumConstants().length;
        String[] colNames = new String[enumLength];
        int[] colIndex = new int[enumLength];
        int i = 0;
        for (E c : columns.getEnumConstants()) {
            colNames[i] = c.name();
            colIndex[i] = c.getIndex();
            i++;
        }
        lineTokenizer.setIncludedFields(colIndex);
        lineTokenizer.setNames(colNames);
    }
}
