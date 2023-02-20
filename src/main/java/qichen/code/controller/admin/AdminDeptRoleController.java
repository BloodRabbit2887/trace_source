package qichen.code.controller.admin;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.Dept;
import qichen.code.entity.DeptRole;
import qichen.code.entity.User;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.BasicsDTO;
import qichen.code.entity.dto.DeptRoleDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.Filter;
import qichen.code.model.ResponseBean;
import qichen.code.service.IDeptRoleService;

import qichen.code.service.IOperationLogService;
import qichen.code.service.IUserService;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/api/v1/admin/deptRole")
public class AdminDeptRoleController {

    @Autowired
    private IDeptRoleService deptRoleService;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IOperationLogService operationLogService;
    @Autowired
    private IUserService userService;



    @ResponseBody
    @PostMapping("/add")
    public ResponseBean add(HttpServletRequest request,
                            @RequestBody DeptRoleDTO dto){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            DeptRole role = deptRoleService.add(dto);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","添加【部门角色】表","t_dept_role",role.getId(), JSON.toJSONString(role));
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/query")
    public ResponseBean query(@RequestParam(value = "level",required = false) Integer level,
                              @RequestParam(value = "createAllOrderPermission",required = false) Integer createAllOrderPermission,
                              @RequestParam(value = "distributionPermission",required = false) Integer distributionPermission,
                              @RequestParam(value = "verifyPermission",required = false) Integer verifyPermission,
                              @RequestParam(value = "linkChangePermission",required = false) Integer linkChangePermission,
                              @RequestParam(value = "updatePermission",required = false) Integer updatePermission,
                              @RequestParam(value = "deptId",required = false) Integer deptId,
                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "createTimeBegin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeBegin,
                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd,
                              @RequestParam(value = "orders", defaultValue = "createTime") String orders,
                              @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy,
                              @RequestParam(value = "keyword", required = false) String keyword){
        DeptRoleDTO dto = new DeptRoleDTO();
        dto.setLevel(level);
        dto.setCreateAllOrderPermission(createAllOrderPermission);
        dto.setDistributionPermission(distributionPermission);
        dto.setVerifyPermission(verifyPermission);
        dto.setLinkChangePermission(linkChangePermission);
        dto.setUpdatePermission(updatePermission);
        dto.setDeptId(deptId);

        Filter filter = new Filter();
        filter.setKeyword(keyword);
        filter.setCreateTimeBegin(createTimeBegin);
        filter.setCreateTimeEnd(createTimeEnd);
        filter.setOrders(orders);
        filter.setOrderBy(orderBy);
        filter.setPage(page);
        filter.setPageSize(pageSize);
        Map<String, Object> res = new HashMap<>();

        try {
            List<DeptRoleDTO> list =deptRoleService.listByFilter(dto, filter);
            BigInteger count =deptRoleService.listCount(dto, filter);
            res.put("list", list);
            res.put("count", count);
            return new ResponseBean(res);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/detail")
    public ResponseBean detail(@RequestParam(value = "id") Integer id){
        try {
            DeptRoleDTO dto = deptRoleService.getDetail(id);
            return new ResponseBean(dto);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


    @ResponseBody
    @PostMapping("/update")
    public ResponseBean update(HttpServletRequest request,
                               @RequestBody DeptRoleDTO dto){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            DeptRole role = deptRoleService.adminUpdate(dto);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","修改【部门角色】表","t_dept_role",role.getId(), JSON.toJSONString(role));
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


    @ResponseBody
    @PostMapping("/delete")
    public ResponseBean delete(HttpServletRequest request,@RequestParam(value = "id") Integer id){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            DeptRole role = deptRoleService.delete(id);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","删除【部门角色】表","t_dept_role",role.getId(), JSON.toJSONString(role));
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/fresh")
    public ResponseBean fresh(){
        List<DeptRole> list = deptRoleService.list();
        List<User> users = userService.list();
        for (User user : users) {
            for (DeptRole role : list) {
                if (user.getDeptId().equals(role.getDeptId())){
                    user.setDeptRoleId(role.getId());
                }
            }
        }
        userService.updateBatchById(users);
        return new ResponseBean();
    }


}
