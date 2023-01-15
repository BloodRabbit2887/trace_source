package qichen.code.entity.dto;


import qichen.code.entity.Menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuDTO extends Menu {
	public static final Integer MENU_LEVEL_ONE = 1;  											
	public static final Integer MENU_LEVEL_TWO = 2;    									
	public static final Integer MENU_LEVEL_THREE = 3;
	private String menuLevelText;
	public static final Map<Integer, String> MAP_MENU_LEVEL = new HashMap<>();
	static {  
		MAP_MENU_LEVEL.put(1, "一级");  
		MAP_MENU_LEVEL.put(2, "二级");
		MAP_MENU_LEVEL.put(3, "三级");
	}
	public static final String MENU_TYPE_LIST = "1";  											
	public static final String MENU_TYPE_BUTTON = "2";    									
	private String menuTypeText;
	public static final Map<String, String> MAP_MENU_TYPE = new HashMap<>();
	static {  
		MAP_MENU_TYPE.put("1", "列表");
		MAP_MENU_TYPE.put("2", "按钮");
	}
	

	private List<MenuDTO> subMenus;
	
	private Integer adminId;
	private Byte adminType;
	private Integer roleId;
	private String text;
	private boolean checked;
	private String checkState;
	
	//vue树节点数据
	private boolean expand;
	private List<MenuDTO> children;
	
	public List<MenuDTO> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(List<MenuDTO> subMenus) {
		this.subMenus = subMenus;
	}

	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getCheckState() {
		return checkState;
	}

	public void setCheckState(String checkState) {
		this.checkState = checkState;
	}

	public List<MenuDTO> getChildren() {
		return children;
	}

	public void setChildren(List<MenuDTO> children) {
		this.children = children;
	}

	public boolean isExpand() {
		return expand;
	}

	public void setExpand(boolean expand) {
		this.expand = expand;
	}

	public String getMenuLevelText() {
		return menuLevelText;
	}

	public void setMenuLevelText(String menuLevelText) {
		this.menuLevelText = menuLevelText;
	}

	public String getMenuTypeText() {
		return menuTypeText;
	}

	public void setMenuTypeText(String menuTypeText) {
		this.menuTypeText = menuTypeText;
	}

	public Byte getAdminType() {
		return adminType;
	}

	public void setAdminType(Byte adminType) {
		this.adminType = adminType;
	}
	
	
	

}
