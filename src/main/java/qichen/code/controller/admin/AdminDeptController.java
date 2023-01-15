package qichen.code.controller.admin;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.Dept;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.DeptDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.Filter;
import qichen.code.model.ResponseBean;
import qichen.code.service.IDeptService;
import qichen.code.service.IOperationLogService;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/v1/admin/dept")
public class AdminDeptController {

    @Autowired
    private IDeptService deptService;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IOperationLogService operationLogService;

    @ResponseBody
    @PostMapping("/add")
    public ResponseBean add(HttpServletRequest request, @RequestBody DeptDTO deptDTO){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            Dept dept = deptService.add(deptDTO);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","添加【部门】表","t_dept",dept.getId(), JSON.toJSONString(dept));
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
    public ResponseBean query(HttpServletRequest request,
                              @RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "createTimeBegin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeBegin,
                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd,
                              @RequestParam(value = "orders", defaultValue = "createTime") String orders,
                              @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy,
                              @RequestParam(value = "keyword", required = false) String keyword) {

        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }

        DeptDTO deptDTO = new DeptDTO();
        deptDTO.setStatus(status);

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
            List<DeptDTO> list =deptService.listByFilter(deptDTO, filter);
            BigInteger count =deptService.listCount(deptDTO, filter);
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
    @PostMapping("/update")
    public ResponseBean update(HttpServletRequest request,@RequestBody DeptDTO deptDTO){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            Dept dept = deptService.adminUpdate(deptDTO);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","修改【部门】表","t_dept",dept.getId(), JSON.toJSONString(dept));
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
            Dept dept = deptService.delete(id);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","修改【部门】表","t_dept",dept.getId(), JSON.toJSONString(dept));
            return new ResponseBean();
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
            DeptDTO deptDTO = deptService.getDetail(id);
            return new ResponseBean(deptDTO);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }
}
