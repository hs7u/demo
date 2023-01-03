package com.batch.demo.vo.vo;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message extends BaseVo{
    private String empName;
    private String message;
    private OffsetDateTime dateTime;
}
