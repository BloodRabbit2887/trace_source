package qichen.code.controller.admin;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.Custom;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.CustomDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.Filter;
import qichen.code.model.ResponseBean;
import qichen.code.service.ICustomService;
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
@RequestMapping("/api/v1/admin/custom")
public class AdminCustomController {

    @Autowired
    private ICustomService customService;
    @Autowired
    private IOperationLogService operationLogService;
    @Autowired
    private UserContextUtils userContextUtils;

    @ResponseBody
    @GetMapping("/query")
    public ResponseBean query(@RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "createTimeBegin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeBegin,
                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd,
                              @RequestParam(value = "orders", defaultValue = "createTime") String orders,
                              @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy,
                              @RequestParam(value = "keyword", required = false) String keyword){

        CustomDTO customDTO = new CustomDTO();
        Filter filter = new Filter();
        filter.setPage(page);
        filter.setPageSize(pageSize);
        filter.setKeyword(keyword);
        filter.setOrders(orders);
        filter.setOrderBy(orderBy);
        filter.setCreateTimeBegin(createTimeBegin);
        filter.setCreateTimeEnd(createTimeEnd);

        Map<String, Object> res = new HashMap<>();

        try {
            List<CustomDTO> list = customService.listByFilter(customDTO,filter);
            BigInteger count = customService.listCount(customDTO,filter);
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
    @PostMapping("/add")
    public ResponseBean add(HttpServletRequest request, @RequestBody CustomDTO customDTO){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            Custom custom = customService.add(customDTO);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","添加【客户管理】表","t_custom",custom.getId(), JSON.toJSONString(custom));
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @PostMapping("/update")
    public ResponseBean update(HttpServletRequest request, @RequestBody CustomDTO customDTO){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            Custom custom = customService.adminUpdate(customDTO);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","修改【客户管理】表","t_custom",custom.getId(), JSON.toJSONString(custom));
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/detail")
    public ResponseBean detail(@RequestParam(value = "id") Integer id){
        try {
            CustomDTO custom = customService.getDetail(id);
            return new ResponseBean(custom);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            exception.printStackTrace();
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
            Custom custom = customService.delete(id);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","删除【客户管理】表","t_custom",custom.getId(), JSON.toJSONString(custom));
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }
}
