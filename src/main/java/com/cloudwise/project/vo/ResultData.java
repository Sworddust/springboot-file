package com.cloudwise.project.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResultData<T> {
    private String code;
    private String message;
    private T data;
}
