package qichen.code.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.OperationLogDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.Filter;
import qichen.code.model.ResponseBean;
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
@RequestMapping("/api/v1/admin/operationLog")
public class AdminOperationLogController {

    @Autowired
    private IOperationLogService operationLogService;
    @Autowired
    private UserContextUtils userContextUtils;

    @ResponseBody
    @GetMapping("/query")
    public ResponseBean query(HttpServletRequest request,
                              @RequestParam(value = "aboutTable",required = false) Integer aboutTable,
                              @RequestParam(value = "operation",required = false) String operation,
                              @RequestParam(value = "aboutId",required = false) Integer aboutId,
                              @RequestParam(value = "type",defaultValue = "1") Integer type,
                              @RequestParam(value = "createTimeBegin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeBegin,
                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd,
                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "orders", defaultValue = "CreateTime") String orders,
                              @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy,
                              @RequestParam(value = "keyword", required = false) String keyword){

        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }

        OperationLogDTO dto = new OperationLogDTO();
        dto.setObjectID(aboutId);
        dto.setObjectType(type);
        dto.setOperation(operation);
        dto.setAssociationTable(OperationLogDTO.ABOUT_TABLE.get(aboutTable));

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
            List<OperationLogDTO> list = operationLogService.listByFilter(dto,filter);
            BigInteger count = operationLogService.listCount(dto,filter);
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
            OperationLogDTO dto = operationLogService.getDetail(id);
            return new ResponseBean(dto);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

}
