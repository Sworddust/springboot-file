package com.cloudwise.project.vo;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class ResultMessage {
	private final static int EXCEPTION_CODE = 300;
	private final static String EXCEPTION_MSG = "服务异常";
	private int code;
	private String msg;
	private Object data;
	private Object world;

	public ResultMessage(int code, String msg, Object data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
	
	public static ResultMessage getException() {
		ResultMessage result = new ResultMessage(EXCEPTION_CODE, EXCEPTION_MSG, null);
		return result;
	}
}
