package qichen.code.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.OptionDTO;
import qichen.code.entity.dto.OptionTypeDTO;
import qichen.code.entity.dto.UserDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.Filter;
import qichen.code.model.ResponseBean;
import qichen.code.service.IOperationLogService;
import qichen.code.service.IOptionService;
import qichen.code.service.IOptionTypeService;
import qichen.code.service.IUserService;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-16
 */
@Slf4j
@RestController
@RequestMapping("/code/user")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IOperationLogService operationLogService;
    @Autowired
    private IOptionService optionService;
    @Autowired
    private IOptionTypeService optionTypeService;

    //登录
    @ResponseBody
    @PostMapping("/login")
    public ResponseBean login(HttpServletRequest request,
                              @RequestParam(value = "account") String account,
                              @RequestParam(value = "password") String password,
                              @RequestParam(value = "mac",required = false) String mac){
        try {
            Map<String,Object> res = userService.login(account,password,request,mac);
            return new ResponseBean(res);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @PostMapping("/logout")
    public ResponseBean logout(HttpServletRequest request){
        try {
            userService.logout(request);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
        return new ResponseBean();
    }

    //基本信息
    @ResponseBody
    @GetMapping("/detail")
    public ResponseBean detail(HttpServletRequest request){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        try {
            UserDTO userDTO = userService.getDTO(user);
            return new ResponseBean(userDTO);
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

    //审核权限变
    @ResponseBody
    @GetMapping("/changePermission")
    public ResponseBean changePermission(HttpServletRequest request,
                                         @RequestParam(value = "userId") Integer userId){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (user.getVerifyPermission()!=1){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getId().equals(userId)){
            return new ResponseBean(new BusinessException(ResException.USER_PER_MISS.getCode(),"无法变更当前账号权限"));
        }
        try {
            userService.changePermission(userId,user);
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/optionType/query")
    public ResponseBean queryOptionType(@RequestParam(value = "status", required = false) Integer status,
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
    @GetMapping("/optionType/detail")
    public ResponseBean optionTypeDetail(@RequestParam(value = "id") Integer id){
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
    @GetMapping("/option/query")
    public ResponseBean queryOption(@RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "tableOptionId",required = false) Integer tableOptionId,
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
        optionDTO.setTableOptionId(tableOptionId);

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
    @GetMapping("/option/detail")
    public ResponseBean optionDetail(@RequestParam(value = "id") Integer id){
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

