package qichen.code.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import qichen.code.entity.dto.UserDTO;
import qichen.code.entity.dto.UserTableProjectDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.DistributionStatusDTO;
import qichen.code.model.Filter;
import qichen.code.model.ResponseBean;
import qichen.code.service.IUserTableProjectService;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户工单分配任务表 前端控制器
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-02-15
 */
@Slf4j
@RestController
@RequestMapping("/code/user-table-project")
public class UserTableProjectController {

    @Autowired
    private IUserTableProjectService userTableProjectService;
    @Autowired
    private UserContextUtils userContextUtils;


    @ResponseBody
    @PostMapping("/submit")
    public ResponseBean add(HttpServletRequest request,
                            @RequestBody UserTableProjectDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (user.getDistributionPermission()==0){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            dto.setSubmitId(user.getId());
            userTableProjectService.add(dto);
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/tableTypes")
    public ResponseBean tableTypes(HttpServletRequest request,
                                   @RequestParam(value = "number") String number){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (user.getDistributionPermission()==0){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            List<DistributionStatusDTO> dtos = userTableProjectService.tableTypes(user.getId(),number);
            return new ResponseBean(dtos);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }

    }

    @ResponseBody
    @PostMapping("/delete")
    public ResponseBean delete(HttpServletRequest request,
                               @RequestParam(value = "id") Integer id){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (user.getDistributionPermission()==0){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            userTableProjectService.delete(id);
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
    public ResponseBean query(@RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "submitId",required = false) Integer submitId,
                              @RequestParam(value = "userId",required = false) Integer userId,
                              @RequestParam(value = "deptId",required = false) Integer deptId,
                              @RequestParam(value = "number",required = false) String number,
                              @RequestParam(value = "tableType",required = false) Integer tableType,
                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "createTimeBegin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeBegin,
                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd,
                              @RequestParam(value = "orders", defaultValue = "CreateTime") String orders,
                              @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy,
                              @RequestParam(value = "keyword", required = false) String keyword) {

        UserTableProjectDTO dto = new UserTableProjectDTO();
        dto.setStatus(status);
        dto.setSubmitId(submitId);
        dto.setUserId(userId);
        dto.setDeptId(deptId);
        dto.setNumber(number);
        dto.setTableType(tableType);

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
            List<UserTableProjectDTO> list =userTableProjectService.listByFilter(dto, filter);
            BigInteger count =userTableProjectService.listCount(dto, filter);
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
            UserTableProjectDTO dto = userTableProjectService.getDetail(id);
            return new ResponseBean(dto);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


}

