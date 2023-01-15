package qichen.code.controller.admin;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.ElectricType;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.ElectricTypeDTO;
import qichen.code.entity.dto.UserLoginDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.ResponseBean;
import qichen.code.service.IElectricTypeService;
import qichen.code.service.IOperationLogService;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/api/v1/admin/electricType")
public class AdminElectricTypeController {

    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IElectricTypeService electricTypeService;
    @Autowired
    private IOperationLogService operationLogService;

    @ResponseBody
    @PostMapping("/add")
    public ResponseBean add(HttpServletRequest request, @RequestBody ElectricTypeDTO dto){
        AdminDTO adminDTO = userContextUtils.newGetCurrentAdmin(request);
        if (adminDTO==null){
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        Byte adminType = adminDTO.getAdminType().byteValue();
        if (!AdminDTO.TYPE_PLATFORM.equals(adminType)) {
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            ElectricType electricType = electricTypeService.add(dto);
            operationLogService.saveOperationLog(adminDTO.getAdminType(),adminDTO.getId(),"310","添加【电机类型】表","t_electric_type",electricType.getId(), JSON.toJSONString(electricType));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @PostMapping("/update")
    public ResponseBean update(HttpServletRequest request, @RequestBody ElectricTypeDTO dto){
        AdminDTO adminDTO = userContextUtils.newGetCurrentAdmin(request);
        if (adminDTO==null){
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        Byte adminType = adminDTO.getAdminType().byteValue();
        if (!AdminDTO.TYPE_PLATFORM.equals(adminType)) {
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            ElectricType electricType = electricTypeService.adminUpdate(dto);
            operationLogService.saveOperationLog(adminDTO.getAdminType(),adminDTO.getId(),"310","修改【电机类型】表","t_electric_type",electricType.getId(), JSON.toJSONString(electricType));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


    @ResponseBody
    @PostMapping("/delete")
    public ResponseBean delete(HttpServletRequest request, @RequestParam(value = "id") Integer id){
        AdminDTO adminDTO = userContextUtils.newGetCurrentAdmin(request);
        if (adminDTO==null){
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        Byte adminType = adminDTO.getAdminType().byteValue();
        if (!AdminDTO.TYPE_PLATFORM.equals(adminType)) {
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            ElectricType electricType = electricTypeService.adminDelete(id);
            operationLogService.saveOperationLog(adminDTO.getAdminType(),adminDTO.getId(),"310","删除【电机类型】表","t_electric_type",electricType.getId(), JSON.toJSONString(electricType));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

}
