package qichen.code.controller;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import qichen.code.entity.ErrModel;
import qichen.code.entity.WorkOrder;
import qichen.code.entity.dto.ErrModelDTO;
import qichen.code.entity.dto.ErrTypeDTO;
import qichen.code.entity.dto.UserDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.Filter;
import qichen.code.model.ResponseBean;
import qichen.code.service.IErrModelService;
import qichen.code.service.IErrTypeService;
import qichen.code.service.IOperationLogService;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 错误典型库表 前端控制器
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-29
 */
@Slf4j
@RestController
@RequestMapping("/code/err-model")
public class ErrModelController {

    @Autowired
    private IErrModelService errModelService;
    @Autowired
    private IErrTypeService errTypeService;
    @Autowired
    private IOperationLogService operationLogService;
    @Autowired
    private UserContextUtils userContextUtils;

    @ResponseBody
    @PostMapping("/commit")
    public ResponseBean commit(HttpServletRequest request, @RequestBody ErrModelDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        try {
            dto.setSubmitId(user.getId());
            dto.setSubmitType(2);
            if (dto.getId()==null){
                dto.setVerifyStatus(0);
            }
            ErrModel errModel = errModelService.commit(dto);
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/query")
    public ResponseBean query(@RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "typeId", required = false) Integer typeId,
                              @RequestParam(value = "verifyStatus",defaultValue = "1") Integer verifyStatus,
                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "createTimeBegin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeBegin,
                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd,
                              @RequestParam(value = "orders", defaultValue = "createTime") String orders,
                              @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy,
                              @RequestParam(value = "keyword", required = false) String keyword) {


        ErrModelDTO dto = new ErrModelDTO();
        dto.setStatus(status);
        dto.setTypeId(typeId);
        dto.setVerifyStatus(verifyStatus);

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
            List<ErrModelDTO> list =errModelService.listByFilter(dto, filter);
            BigInteger count =errModelService.listCount(dto, filter);
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

            ErrModelDTO dto = errModelService.getDetail(id);
            return new ResponseBean(dto);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }



    @ResponseBody
    @GetMapping("/type/query")
    public ResponseBean queryType(@RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "createTimeBegin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeBegin,
                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd,
                              @RequestParam(value = "orders", defaultValue = "createTime") String orders,
                              @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy,
                              @RequestParam(value = "keyword", required = false) String keyword) {


        ErrTypeDTO dto = new ErrTypeDTO();
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
            List<ErrTypeDTO> list =errTypeService.listByFilter(dto, filter);
            BigInteger count =errTypeService.listCount(dto, filter);
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
    @GetMapping("/type/detail")
    public ResponseBean typeDetail(@RequestParam(value = "id") Integer id){
        try {
            ErrTypeDTO dto = errTypeService.getDetail(id);
            return new ResponseBean(dto);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


}

