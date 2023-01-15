package qichen.code.controller.admin;


import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.MenuDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.ResponseBean;
import qichen.code.model.SearchModel;
import qichen.code.service.*;
import qichen.code.utils.ContextUtils;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * <p>
 * 管理员表 前端控制器
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-09-07
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private IAdminService adminService;
    @Autowired
    private IOperationLogService operationLogService;
    @Autowired
    private IMenuService menuService;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IAdminTokenService adminTokenService;
    @Autowired
    private IUserLoginService userLoginService;

    @ResponseBody
    @RequestMapping(value = "/verify/login")
    public ResponseBean login(HttpSession session,
                              @RequestParam(value = "loginName") String loginName,
                              @RequestParam(value = "password") String password) {

        try {
            AdminDTO admin = adminService.login(loginName, password);
            session.setAttribute("admin", admin);
            Integer operManType = admin.getAdminType();
            operationLogService.saveOperationLog(operManType, admin.getId(), "100", "管理用户【" + admin.getAdminNO() + "】登录", null, admin.getId(), null);
            ResponseBean responseBean = new ResponseBean();
            responseBean.setData(admin);
            return responseBean;
        } catch (BusinessException e) {
            log.error(e.getMessage());
            return new ResponseBean(e);
        }
    }


    @RequestMapping(method = RequestMethod.GET)
    public String index(HttpServletRequest request, Model model) {
        Integer adminId = userContextUtils.newGetCurrentAdminId(request);

        MenuDTO filter = new MenuDTO();
        filter.setAdminId(adminId);
        List<MenuDTO> menuList = menuService.listByFilter(filter);
        model.addAttribute("menuList", menuList);
        return "html/admin";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseBean logout(HttpServletRequest request,HttpSession session) {
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        ContextUtils.clearAccessMap(admin.getId());
        session.invalidate();
        Integer operManType = admin.getAdminType();
        adminTokenService.removeByAdminId(admin.getId());
        operationLogService.saveOperationLog(operManType, admin.getId(), "101", "管理用户【" + admin.getAdminNO() + "】退出", null, admin.getId(), null);
        return new ResponseBean();
    }

    @ResponseBody
    @GetMapping("/userLoginSearch")
    public ResponseBean userLoginSearch(@RequestParam(value = "type") Integer type){
        try {
            List<SearchModel> list = userLoginService.searchByType(type);
            return new ResponseBean(list);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

}

