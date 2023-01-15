package qichen.code.entity.dto;

import qichen.code.entity.Parameter;

public class ParameterDTO extends Parameter {

	public static final Byte STATUS_OK = (byte)1;												// 正常状态
	public static final Byte STATUS_LOCK = (byte)0;    											// 注册，等待提交认证

	
	// 文件传输类型

	// 需要进行翻译成字符串的信息
	private String statusStr;

	public ParameterDTO(boolean b) {
		super(b);
	}

	// 子模块信息

	public String getStatusStr() {
		return statusStr;
	}

	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}

	public ParameterDTO() {
	}
}
