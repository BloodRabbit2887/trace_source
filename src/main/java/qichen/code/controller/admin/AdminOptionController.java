package qichen.code.controller.admin;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.Option;
import qichen.code.entity.OptionType;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.OptionDTO;
import qichen.code.entity.dto.OptionTypeDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.Filter;
import qichen.code.model.ResponseBean;
import qichen.code.service.IOperationLogService;
import qichen.code.service.IOptionService;
import qichen.code.service.IOptionTypeService;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/v1/admin/option")
public class AdminOptionController {

    @Autowired
    private IOperationLogService operationLogService;
    @Autowired
    private IOptionService optionService;
    @Autowired
    private IOptionTypeService optionTypeService;
    @Autowired
    private UserContextUtils userContextUtils;

    @ResponseBody
    @PostMapping("/type/add")
    public ResponseBean addType(HttpServletRequest request, @RequestBody OptionTypeDTO dto){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            OptionType optionType = optionTypeService.add(dto);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","添加【下拉选项类型】表","t_option_type",optionType.getId(), JSON.toJSONString(optionType));
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @PostMapping("/type/update")
    public ResponseBean updateType(HttpServletRequest request, @RequestBody OptionTypeDTO dto){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            OptionType optionType = optionTypeService.adminUpdate(dto);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","修改【下拉选项类型】表","t_option_type",optionType.getId(), JSON.toJSONString(optionType));
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


    @ResponseBody
    @PostMapping("/type/delete")
    public ResponseBean deleteType(HttpServletRequest request, @RequestParam(value = "id") Integer id){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            OptionType optionType = optionTypeService.adminDelete(id);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","删除【下拉选项类型】表","t_option_type",optionType.getId(), JSON.toJSONString(optionType));
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
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
                                  @RequestParam(value = "keyword", required = false) String keyword){
        try {
            OptionTypeDTO dto = new OptionTypeDTO();
            dto.setStatus(status);
            Filter filter = new Filter();
            filter.setPage(page);
            filter.setPageSize(pageSize);
            filter.setCreateTimeBegin(createTimeBegin);
            filter.setCreateTimeEnd(createTimeEnd);
            filter.setOrders(orders);
            filter.setOrderBy(orderBy);
            filter.setKeyword(keyword);

            Map<String,Object> res = new HashMap<>();
            List<OptionTypeDTO> list = optionTypeService.listByFilter(dto,filter);
            BigInteger count = optionTypeService.listCount(dto,filter);
            res.put("list",list);
            res.put("count",count);
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
            OptionTypeDTO dto = optionTypeService.getDetail(id);
            return new ResponseBean(dto);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }



    @ResponseBody
    @PostMapping("/add")
    public ResponseBean add(HttpServletRequest request, @RequestBody OptionDTO dto){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            Option option = optionService.add(dto);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","添加【下拉选项】表","t_option",option.getId(), JSON.toJSONString(option));
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


    @ResponseBody
    @PostMapping("/update")
    public ResponseBean update(HttpServletRequest request, @RequestBody OptionDTO dto){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            Option option = optionService.adminUpdate(dto);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","修改【下拉选项】表","t_option",option.getId(), JSON.toJSONString(option));
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
    public ResponseBean update(HttpServletRequest request,@RequestParam(value = "id") Integer id){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            Option option = optionService.adminDelete(id);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","删除【下拉选项】表","t_option",option.getId(), JSON.toJSONString(option));
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
                              @RequestParam(value = "typeId",required = false) Integer typeId,
                              @RequestParam(value = "page", required = false) Integer page,
                              @RequestParam(value = "pageSize", required = false) Integer pageSize,
                              @RequestParam(value = "createTimeBegin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeBegin,
                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd,
                              @RequestParam(value = "orders", defaultValue = "createTime") String orders,
                              @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy,
                              @RequestParam(value = "keyword", required = false) String keyword) {

        OptionDTO optionDTO = new OptionDTO();
        optionDTO.setTypeId(typeId);
        optionDTO.setStatus(status);

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
            List<OptionDTO> list =optionService.listByFilter(optionDTO, filter);
            BigInteger count =optionService.listCount(optionDTO, filter);
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
            OptionDTO optionDTO = optionService.getDetail(id);
            return new ResponseBean(optionDTO);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }




}
