package qichen.code.controller.admin;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.Custom;
import qichen.code.entity.TableOptions;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.TableOptionDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.Filter;
import qichen.code.model.ResponseBean;
import qichen.code.service.IOperationLogService;
import qichen.code.service.ITableOptionsService;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/v1/admin/tableOptions")
public class AdminTableOptionController {

    @Autowired
    private ITableOptionsService tableOptionsService;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IOperationLogService operationLogService;

    @ResponseBody
    @PostMapping("/addBatch")
    public ResponseBean addBatch(HttpServletRequest request,
                            @RequestBody List<TableOptionDTO> tableOptions){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            tableOptions = JSON.parseArray(JSON.toJSONString(tableOptions),TableOptionDTO.class);
            List<TableOptions> options = tableOptionsService.addBatch(tableOptions);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","批量添加【表单选项】表","t_table_options",null, JSON.toJSONString(options));
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
    @PostMapping("/delete")
    public ResponseBean delete(HttpServletRequest request, @RequestParam(value = "id") Integer id){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            TableOptions options = tableOptionsService.delete(id);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","删除【表单选项】表","t_table_options",id, JSON.toJSONString(options));
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
    public ResponseBean update(HttpServletRequest request,@RequestBody TableOptionDTO dto){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            TableOptions options = tableOptionsService.adminUpdate(dto);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","修改【表单选项】表","t_table_options",options.getId(), JSON.toJSONString(options));
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
    @GetMapping("/query")
    public ResponseBean query(@RequestParam(value = "type",required = false) Integer type,
                              @RequestParam(value = "tableType",required = false) Integer tableType,
                              @RequestParam(value = "level",required = false) Integer level,
                              @RequestParam(value = "must",required = false) Integer must,
                              @RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "createTimeBegin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeBegin,
                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd,
                              @RequestParam(value = "orders", defaultValue = "CreateTime") String orders,
                              @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy,
                              @RequestParam(value = "keyword", required = false) String keyword) {

        TableOptionDTO dto = new TableOptionDTO();
        dto.setType(type);
        dto.setTableType(tableType);
        dto.setLevel(level);
        dto.setMust(must);
        dto.setStatus(status);

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
            List<TableOptionDTO> list =tableOptionsService.listByFilter(dto, filter);
            BigInteger count =tableOptionsService.listCount(dto, filter);
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
            TableOptionDTO dto = tableOptionsService.getDetail(id);
            return new ResponseBean(dto);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }
}
