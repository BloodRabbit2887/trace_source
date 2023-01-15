package qichen.code.entity.dto;


public class ParameterFilterDTO {

	public static final String KEYWORD = "`paramName`, `ext1`";
	
	
	// 过滤条件参数
	private Integer id;
	private Integer storeId;
	private Byte status;
	private Byte delTf;
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}
	
	public Byte getDelTf() {
		return delTf;
	}

	public void setDelTf(Byte delTf) {
		this.delTf = delTf;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

}
