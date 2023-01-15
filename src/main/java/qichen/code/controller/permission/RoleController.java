package qichen.code.controller.permission;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.dto.*;
import qichen.code.model.ResponseBean;
import qichen.code.service.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/api/v1/admin/roles")
public class RoleController {
	@Autowired
	private IMenuService menuService;
	
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IAdminService adminService;
	@Autowired
	private IAdminRoleService adminRoleService;
	@Autowired
	private IRoleMenuService roleMenuService;

	@ResponseBody
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseBean list(@RequestParam(value = "page", defaultValue = "1") int page,
							 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
							 @RequestParam(value = "status", defaultValue = "1") String status,
							 @RequestParam(value = "name",  required = false) String name,
							 @RequestParam(value = "query", required = false) String query) {
		RoleDTO filter = new RoleDTO();
		filter.setQuery(query);
		filter.setName(name);
		filter.setStatus(status);
		List<RoleDTO> list = roleService.listByPage(filter, page, pageSize);
		int count = roleService.listCount(filter);

		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalCount", count);

		ResponseBean responseBean = new ResponseBean();
		responseBean.setData(map);

		return responseBean;
	}

	@ResponseBody
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public ResponseBean get(
			@RequestParam(value = "id",  required = false) Integer id
			) {
		RoleDTO roleDTO = roleService.get(id);
		ResponseBean responseBean = new ResponseBean();
		responseBean.setData(roleDTO);

		return responseBean;
	}

	@ResponseBody
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseBean add(@RequestBody RoleDTO roleDTO) {
		roleService.add(roleDTO);

		return new ResponseBean();
	}

	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseBean update(
			@RequestBody RoleDTO roleDTO) {
		roleService.updateDto(roleDTO);

		return new ResponseBean();
	}

	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseBean delete(
			@RequestParam(value = "id",  required = false) Integer id
			) {
		RoleDTO roleDTO = new RoleDTO();
		roleDTO.setId(id);
		roleDTO.setStatus("9");
		roleService.updateDto(roleDTO);

		return new ResponseBean();
	}

	@ResponseBody
	@RequestMapping(value = "/admins", method = RequestMethod.GET)
	public ResponseBean listAdmins(@RequestParam("id") Integer roleId,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		AdminDTO filter = new AdminDTO();
		filter.setRoleId(roleId);

		List<AdminDTO> list = adminService.listDtoByPage(filter, page, pageSize);
		int count = adminService.listCount(filter);

		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalCount", count);

		ResponseBean responseBean = new ResponseBean();
		responseBean.setData(map);

		return responseBean;
	}

	@ResponseBody
	@RequestMapping(value = "/admins-not", method = RequestMethod.GET)
	public ResponseBean listAdminsNot(@RequestParam("id") Integer roleId,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		AdminDTO filter = new AdminDTO();
		filter.setNotInRoleId(roleId);

		List<AdminDTO> list = adminService.listDtoByPage(filter, page, pageSize);
		int count = adminService.listCount(filter);

		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalCount", count);

		ResponseBean responseBean = new ResponseBean();
		responseBean.setData(map);

		return responseBean;
	}

	@ResponseBody
	@RequestMapping(value = "/admins", method = RequestMethod.POST)
	public ResponseBean addAdmin(@RequestParam("roleId") Integer roleId,
			@RequestParam(value = "adminId", required = true) Integer adminId) {
		AdminRoleDTO adminRoleDTO = new AdminRoleDTO();
		adminRoleDTO.setAdminID(adminId);
		adminRoleDTO.setRoleID(roleId);

		adminRoleService.addDto(adminRoleDTO);

		return new ResponseBean();
	}

	@ResponseBody
	@RequestMapping(value = "/admins/delete", method = RequestMethod.POST)
	public ResponseBean deleteAdmin(@RequestParam("roleId") Integer roleId, 
			@RequestParam("adminId") Integer adminId) {
		adminRoleService.removeById(adminId);
		return new ResponseBean();
	}

	@ResponseBody
	@RequestMapping(value = "/menus/add", method = RequestMethod.POST)
	public ResponseBean addMenu(@RequestBody RoleMenuDTO roleMenuDTO) {
		roleMenuService.add(roleMenuDTO);

		MenuDTO filter = new MenuDTO();
		filter.setAdminId(roleMenuDTO.getRoleId());
		menuService.setAccess(filter);

		return new ResponseBean();
		
	}

	

}
