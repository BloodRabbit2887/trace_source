package qichen.code.model;


import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;

public final class ResponseBean {

	private int status = 0;
	private Object data;
	private String error;

	public ResponseBean() {
	}

	public ResponseBean(ResException exception){
		this.error=exception.getMessage();
		this.status=exception.getCode();
	}

	public ResponseBean(BusinessException exception){
		this.error=exception.getMessage();
		this.status=exception.getCode();
	}

	public ResponseBean(Object data) {
		this.data = data;
	}

	public ResponseBean(Object data, int status) {
		this.status = status;
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
