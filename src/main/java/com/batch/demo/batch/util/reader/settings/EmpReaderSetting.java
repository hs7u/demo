package com.batch.demo.batch.util.reader.settings;

import com.batch.demo.batch.util.reader.BaseSetting;
import com.batch.demo.batch.util.reader.CsvColumns;
import com.batch.demo.vo.vo.Emp;

public class EmpReaderSetting extends BaseSetting<Emp> {
    
    public EmpReaderSetting(String filePath){
        this.delimiter = ",";
        this.clazz = Emp.class;
        this.linesToSkip = 1;
        this.strict = false;
        this.filePath = filePath;
        this.columns = EmpCsvColumns.class;
    }
  
    /**
     * 保留的欄位及其文件的index
     * 欄位名要符合目標vo
     */
    enum EmpCsvColumns implements CsvColumns {    
        empName(0),
        password(1);
    
        private final int index;
    
        EmpCsvColumns(int index) {
            this.index = index;
        }
    
        @Override
        public int getIndex() {
            return index;
        }
    }
}
