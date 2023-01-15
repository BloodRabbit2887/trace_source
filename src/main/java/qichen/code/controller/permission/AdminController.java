package qichen.code.controller.permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.AdminMenuDTO;
import qichen.code.entity.dto.MenuDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.ResponseBean;
import qichen.code.service.IAdminMenuService;
import qichen.code.service.IAdminService;
import qichen.code.service.IMenuService;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller("adminsController")
@RequestMapping("/api/v1/admin/admins")
public class AdminController {

	@Autowired
	private IAdminService adminService;

	@Autowired
	private IMenuService menuService;

	@Autowired
	private IAdminMenuService adminMenuService;


	@ResponseBody
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseBean list(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "query", required = false) String query) {
		AdminDTO filter = new AdminDTO();
		filter.setQuery(query);
		filter.setAdminName(name);
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
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public ResponseBean getAdmin(@RequestParam(value = "id") Integer id) {
		AdminDTO adminDTO = adminService.getAdmin(id);
		if (adminDTO==null){
			throw new BusinessException(ResException.ADMIN_LOGIN_ERR);
		}
		ResponseBean responseBean = new ResponseBean();
		responseBean.setData(adminDTO);
		return responseBean;
	}

	@ResponseBody
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseBean add(@RequestParam(value = "adminNO") String adminNo,
							@RequestParam(value = "adminName",required = false) String adminName,
							@RequestParam(value = "password") String password,
							@RequestParam(value = "roleId",required = false) Integer roleId) {
		AdminDTO adminDTO = new AdminDTO();
		adminDTO.setAdminNO(adminNo);
		adminDTO.setPassword(password);
		if (!StringUtils.isEmpty(adminName) && adminName.length()>0){
			adminDTO.setAdminName(adminName);
		}
		if (roleId!=null){
			adminDTO.setRoleId(roleId);
		}
		adminService.add(adminDTO);

		return new ResponseBean();
	}

	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseBean update(@RequestParam(value = "id") Integer id,
							   @RequestParam(value = "adminNO",required = false) String adminNo,
							   @RequestParam(value = "adminName",required = false) String adminName,
							   @RequestParam(value = "password",required = false) String password,
							   @RequestParam(value = "roleId",required = false) Integer roleId) {
		// ?id=&adminNO=&adminName=&password=&roleId=
		AdminDTO adminDTO = new AdminDTO();
		adminDTO.setId(id);
		if (!StringUtils.isEmpty(adminNo) && adminNo.length()>0){
			adminDTO.setAdminNO(adminNo);
		}
		if (!StringUtils.isEmpty(adminName) && adminName.length()>0){
			adminDTO.setAdminName(adminName);
		}
		if (!StringUtils.isEmpty(password) && password.length()>0){
			adminDTO.setPassword(password);
		}
		if (roleId!=null){
			adminDTO.setRoleId(roleId);
		}
		adminService.updateDto(adminDTO);
		return new ResponseBean();
	}

	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseBean delete(@RequestParam(value = "id") Integer id) {
		adminService.removeById(id);
		return new ResponseBean();
	}

	@ResponseBody
	@RequestMapping(value = "/menus/add", method = RequestMethod.POST)
	public ResponseBean addMenu(@RequestBody AdminMenuDTO adminMenu) {
		adminMenuService.add(adminMenu);
		MenuDTO filter = new MenuDTO();
		filter.setAdminId(adminMenu.getAdminId());
		menuService.setAccess(filter);
		return new ResponseBean();
	}

	@ResponseBody
	@RequestMapping(value = "/menus/delete", method = RequestMethod.POST)
	public ResponseBean deleteMenu(@RequestParam("menuId") Integer menuId, @RequestParam("adminId") Integer adminId) {
		AdminMenuDTO adminMenuDTO = new AdminMenuDTO();
		adminMenuDTO.setAdminId(adminId);
		adminMenuDTO.setMenuID(menuId);
		adminMenuService.removeById(adminMenuDTO.getAdminId());
		return new ResponseBean();
	}

}
