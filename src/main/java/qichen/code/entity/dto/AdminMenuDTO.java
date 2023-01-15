package qichen.code.entity.dto;

import qichen.code.entity.AdminMenu;

public class AdminMenuDTO extends AdminMenu {
	private static final long serialVersionUID = 1L;
	
	private Integer adminId;
	private Integer[] menuIds;
	private String[] checkStates;

	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

	public Integer[] getMenuIds() {
		return menuIds;
	}

	public void setMenuIds(Integer[] menuIds) {
		this.menuIds = menuIds;
	}

	public String[] getCheckStates() {
		return checkStates;
	}

	public void setCheckStates(String[] checkStates) {
		this.checkStates = checkStates;
	}

}
