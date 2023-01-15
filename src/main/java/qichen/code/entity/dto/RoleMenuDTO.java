package qichen.code.entity.dto;

import qichen.code.entity.RoleMenu;

public class RoleMenuDTO extends RoleMenu {
	private static final long serialVersionUID = 1L;
	
	private Integer roleId;
	private Integer[] menuIds;
	private String[] checkStates;

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
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
