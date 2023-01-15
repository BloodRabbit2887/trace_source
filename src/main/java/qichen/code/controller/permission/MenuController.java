package qichen.code.controller.permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.dto.MenuDTO;
import qichen.code.model.ResponseBean;
import qichen.code.service.IMenuService;


import java.util.List;


@Controller
@RequestMapping("/api/v1/admin/menus")
public class MenuController {

	@Autowired
	private IMenuService menuService;

	@ResponseBody
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseBean list(@RequestParam(value = "adminId", required = false) Integer adminId,
							 @RequestParam(value = "roleId", required = false) Integer roleId) {
		MenuDTO filter = new MenuDTO();
		filter.setAdminId(adminId);
		filter.setRoleId(roleId);
		// List<MenuDTO> list = menuService.list1(filter);
		// List<MenuDTO> list = menuService.query(filter);
		List<MenuDTO> list = menuService.list2(filter);
		ResponseBean responseBean = new ResponseBean();
		responseBean.setData(list);

		return responseBean;
	}

	@ResponseBody
	@RequestMapping(value = "/listByClassify", method = RequestMethod.GET)
	public ResponseBean listByClassify(

			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "menuType", required = false) String menuType,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "parentId", required = false) Integer parentId) {

		MenuDTO filter = new MenuDTO();
		filter.setTitle(title);
		filter.setMenuType(menuType);
		filter.setStatus(status);
		filter.setParentID(parentId);

		// List<MenuDTO> list = menuService.list(filter);
		List<MenuDTO> list = menuService.menulist(filter);

		ResponseBean responseBean = new ResponseBean();
		responseBean.setData(list);
		return responseBean;
	}

	@ResponseBody
	@RequestMapping(value = "/menuId", method = RequestMethod.GET)
	public ResponseBean getMenu(@RequestParam(value = "menuId", required = false) Integer menuId) {

		MenuDTO filter = new MenuDTO();
		filter.setId(menuId);
		MenuDTO menuDTO = menuService.getMenu(filter);

		ResponseBean responseBean = new ResponseBean();
		responseBean.setData(menuDTO);
		return responseBean;
	}

	@ResponseBody
	@RequestMapping(value = "/listTree", method = RequestMethod.GET)
	public Object listTree(@RequestParam(value = "menuType", required = false) String menuType,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "parentId", required = false) Integer parentId) {

		MenuDTO filter = new MenuDTO();
		filter.setMenuType(menuType);
		filter.setStatus(status);
		filter.setParentID(parentId);

		return menuService.listByFilter(filter);
	}

	@ResponseBody
	@RequestMapping(value = "addMenu", method = RequestMethod.POST)
	public ResponseBean addMenu(@RequestBody MenuDTO menuDTO

//			@RequestParam(value = "title", required = true) String title,
//			@RequestParam(value = "component", required = true) String component,
//			@RequestParam(value = "menuType", required = true) String menuType,
//			@RequestParam(value = "menuLevel", required = true) Integer menuLevel,
//			@RequestParam(value = "path", required = true) String path,
//			@RequestParam(value = "name", required = true) String name,
//			@RequestParam(value = "icon", required = true) String icon,
//			@RequestParam(value = "apiUrl", required = true) String apiUrl,
//			@RequestParam(value = "parentId", required = true) Integer parentId,
//			@RequestParam(value = "orderBy", required = true) Integer orderBy
			) {

//		MenuDTO menuDTO = new MenuDTO();
//		menuDTO.setMenuLevel(menuLevel);
//		menuDTO.setMenuType(menuType);
//		menuDTO.setTitle(title);
//		menuDTO.setComponentText(component);
//		menuDTO.setPath(path);
//		menuDTO.setName(name);
//		menuDTO.setIcon(icon);
//		menuDTO.setApiUrl(apiUrl);
//		menuDTO.setParentId(parentId);
//		menuDTO.setOrderBy(orderBy);

		menuDTO = menuService.add(menuDTO);

		ResponseBean responseBean = new ResponseBean();

		responseBean.setData(menuDTO);
		return responseBean;
	}

	@ResponseBody
	@RequestMapping(value = "updateMenu", method = RequestMethod.POST)
	public ResponseBean updateMenu(@RequestBody MenuDTO menuDTO) {
		menuService.updateDto(menuDTO);
		return new ResponseBean();
	}

	/**
	 * 删除
	 */
	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseBean deleteMenu(@RequestParam(value = "id", required = false) Integer id) {
		MenuDTO menuDTO = new MenuDTO();
		menuDTO.setId(id);
		menuService.delete(menuDTO);
		return new ResponseBean();
	}

}
