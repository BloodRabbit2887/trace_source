package qichen.code.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.Admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(value = {"saltValue","password","updateTime"})
public class AdminDTO extends Admin {
	
	public static final Byte STATUS_OK = (byte)1; 												// 正常状态
	public static final Byte STATUS_LOCK = (byte)2; 										// 等待认证
	
	public static final Byte TYPE_PLATFORM = (byte)1; 											// 超级管理员

	private Integer roleId;
	private String roleName;
	private Integer notInRoleId;
	
	private String query;
	
	public String adminTypeText;
	private String tokenId;
	
	private List<MenuDTO> adminMenuList;

	public static final Map<Byte,String> MAP_TYPE = new HashMap<Byte,String>();
	static {
		MAP_TYPE.put((byte)1, "平台管理员");
	}

}
