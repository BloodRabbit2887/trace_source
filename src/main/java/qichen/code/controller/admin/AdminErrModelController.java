package qichen.code.controller.admin;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.Custom;
import qichen.code.entity.ErrModel;
import qichen.code.entity.ErrType;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.ErrModelDTO;
import qichen.code.entity.dto.ErrTypeDTO;
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

@Slf4j
@Controller
@RequestMapping("/api/v1/admin/err-model")
public class AdminErrModelController {

    @Autowired
    private IErrModelService errModelService;
    @Autowired
    private IErrTypeService errTypeService;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IOperationLogService operationLogService;

    @ResponseBody
    @GetMapping("/query")
    public ResponseBean query(@RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "typeId", required = false) Integer typeId,
                              @RequestParam(value = "verifyStatus",required = false) Integer verifyStatus,
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
    @PostMapping("/verify")
    public ResponseBean verify(HttpServletRequest request,
                               @RequestParam(value = "id") Integer id,
                               @RequestParam(value = "status") Integer status,
                               @RequestParam(value = "remark",required = false) String remark){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            ErrModelDTO modelDTO = new ErrModelDTO();
            modelDTO.setId(id);
            modelDTO.setVerifyStatus(status);
            modelDTO.setServerRemark(remark);
            modelDTO.setVerifyId(admin.getId());
            modelDTO.setVerifyType(1);
            ErrModel errModel = errModelService.verify(modelDTO);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","管理员审核【错误典型库】表","t_err_model",id, JSON.toJSONString(errModel));
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
            ErrModel errModel = errModelService.delete(id);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","管理员删除【错误典型库】表","t_err_model",id, JSON.toJSONString(errModel));
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


    @ResponseBody
    @PostMapping("/type/add")
    public ResponseBean typeAdd(HttpServletRequest request,@RequestBody ErrTypeDTO typeDTO){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            ErrType type = errTypeService.add(typeDTO);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","管理员添加【错误类型】表","t_err_type",type.getId(), JSON.toJSONString(type));
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
    @PostMapping("/type/update")
    public ResponseBean typeUpdate(HttpServletRequest request,@RequestBody ErrTypeDTO typeDTO){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            ErrType type = errTypeService.adminUpdate(typeDTO);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","管理员修改【错误类型】表","t_err_type",type.getId(), JSON.toJSONString(type));
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
    @PostMapping("/type/delete")
    public ResponseBean typeDelete(HttpServletRequest request,@RequestParam(value = "id") Integer id){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            ErrType type = errTypeService.adminDelete(id);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","管理员删除【错误类型】表","t_err_type",type.getId(), JSON.toJSONString(type));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }
}
