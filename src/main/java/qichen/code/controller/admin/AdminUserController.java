package qichen.code.controller.admin;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.User;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.UserDTO;
import qichen.code.entity.dto.UserLoginDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.Filter;
import qichen.code.model.ResponseBean;
import qichen.code.service.IOperationLogService;
import qichen.code.service.IUserLoginService;
import qichen.code.service.IUserService;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/api/v1/admin/user")
@Controller
public class AdminUserController {

    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IUserService userService;
    @Autowired
    private IOperationLogService operationLogService;
    @Autowired
    private IUserLoginService userLoginService;

    //创建新用户
    @ResponseBody
    @PostMapping("/add")
    public ResponseBean add(HttpServletRequest request, @RequestBody UserDTO userDTO){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            User user = userService.add(userDTO);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310"," 添加【用户】表","t_user",user.getId(), JSON.toJSONString(user));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    //列表查询
    @ResponseBody
    @GetMapping("/query")
    public ResponseBean query(@RequestParam(value = "type",required = false) Integer type,
                              @RequestParam(value = "verifyPermission",required = false) Integer verifyPermission,
                              @RequestParam(value = "account",required = false) String account,
                              @RequestParam(value = "name",required = false) String name,
                              @RequestParam(value = "deptId",required = false) Integer deptId,
                              @RequestParam(value = "sex",required = false) Integer sex,
                              @RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "createTimeBegin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeBegin,
                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd,
                              @RequestParam(value = "orders", defaultValue = "createTime") String orders,
                              @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy,
                              @RequestParam(value = "keyword", required = false) String keyword) {

        UserDTO userDTO = new UserDTO();
        userDTO.setDeptId(deptId);
        userDTO.setSex(sex);
        userDTO.setStatus(status);
        userDTO.setName(name);
        userDTO.setAccount(account);
        userDTO.setType(type);
        userDTO.setVerifyPermission(verifyPermission);

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
            List<UserDTO> list =userService.listByFilter(userDTO, filter);
            BigInteger count =userService.listCount(userDTO, filter);
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

    //用户详情
    @ResponseBody
    @GetMapping("/detail")
    public ResponseBean detail(@RequestParam(value = "id") Integer userId){
        try {
            UserDTO userDTO = userService.getDetail(userId);
            return new ResponseBean(userDTO);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    //修改
    @ResponseBody
    @PostMapping("/update")
    public ResponseBean update(HttpServletRequest request,
                               @RequestBody UserDTO userDTO){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            User user = userService.adminUpdate(userDTO);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","修改【用户】表","t_user",user.getId(), JSON.toJSONString(user));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    //账号锁定/解锁
    @ResponseBody
    @PostMapping("/lock")
    public ResponseBean lock(HttpServletRequest request,@RequestParam(value = "id") Integer id){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            User user = userService.lock(id);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","用户"+(user.getStatus()==1?"锁定":"解锁"),"t_user",id, JSON.toJSONString(user));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    //删除
    @ResponseBody
    @PostMapping("/delete")
    public ResponseBean delete(HttpServletRequest request,@RequestParam(value = "id") Integer id){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            User user = userService.delete(id);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","删除【用户】表","t_user",id, JSON.toJSONString(user));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/loginSearch")
    public ResponseBean loginSearch(@RequestParam(value = "deptId", required = false) Integer deptId,
                                    @RequestParam(value = "userId",required = false) Integer userId,
                                    @RequestParam(value = "loginTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date loginTime,
                                    @RequestParam(value = "offLineTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date offLineTime,
                                    @RequestParam(value = "orders", defaultValue = "loginTime") String orders,
                                    @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy,
                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setDeptId(deptId);
        userLoginDTO.setUserId(userId);
        if (loginTime!=null){
            userLoginDTO.setLoginTime(LocalDateTime.ofInstant(loginTime.toInstant(), ZoneId.systemDefault()));
        }
        if (offLineTime!=null){
            userLoginDTO.setOffLineTime(LocalDateTime.ofInstant(offLineTime.toInstant(),ZoneId.systemDefault()));
        }
        Filter filter = new Filter();
        filter.setPage(page);
        filter.setPageSize(pageSize);
        filter.setOrders(orders);
        filter.setOrderBy(orderBy);

        Map<String,Object> res = new HashMap<>();

        try {
            List<UserLoginDTO> list = userLoginService.listByFilter(userLoginDTO,filter);
            BigInteger count = userLoginService.listCount(userLoginDTO,filter);
            res.put("list",list);
            res.put("count",count);
            return new ResponseBean(res);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/loginDetail")
    public ResponseBean loginDetail(@RequestParam(value = "id") Integer id){
        try {
            UserLoginDTO userLoginDTO = userLoginService.getDetail(id);
            return new ResponseBean(userLoginDTO);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


}
