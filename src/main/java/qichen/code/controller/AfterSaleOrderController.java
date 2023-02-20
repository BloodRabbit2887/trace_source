package qichen.code.controller;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import qichen.code.entity.AfterSaleOrder;
import qichen.code.entity.DeviseOrder;
import qichen.code.entity.ModelInstall;
import qichen.code.entity.dto.AfterSaleOrderDTO;
import qichen.code.entity.dto.DeviseOrderDTO;
import qichen.code.entity.dto.ModelInstallDTO;
import qichen.code.entity.dto.UserDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.DeptTypeModel;
import qichen.code.model.ResponseBean;
import qichen.code.service.IAfterSaleOrderService;
import qichen.code.service.IModelInstallService;
import qichen.code.service.IOperationLogService;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 维修工单表 前端控制器
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-12
 */
@Slf4j
@RestController
@RequestMapping("/code/after-sale-order")
public class AfterSaleOrderController {

    @Autowired
    private IAfterSaleOrderService afterSaleOrderService;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IOperationLogService operationLogService;
    @Autowired
    private IModelInstallService modelInstallService;

    @ResponseBody
    @GetMapping("/getWorkOrderModel")
    public ResponseBean getWorkOrderModel(HttpServletRequest request, @RequestParam(value = "number") String number){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_AFTER_SALE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            AfterSaleOrderDTO dto = afterSaleOrderService.getWorkOrderModel(user.getId(),number);
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
    @PostMapping("/createWorkOrder")
    public ResponseBean createWorkOrder(HttpServletRequest request,
                                        @RequestBody AfterSaleOrderDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_AFTER_SALE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            dto.setSubmitId(user.getId());
            dto.setSubmitType(2);

            //TODO 正式删除
/*            dto.setVerifyStatus(1);
            dto.setVerifyId(2);*/

            AfterSaleOrder afterSaleOrder = afterSaleOrderService.createWorkOrder(dto);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "创建【维修工单】", "t_after_sale_order", afterSaleOrder.getId(), JSON.toJSONString(afterSaleOrder));
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
    @GetMapping("/modelInstall/getWorkOrderModel")
    public ResponseBean getModelInstallModel(HttpServletRequest request, @RequestParam(value = "number") String number){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_AFTER_SALE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            ModelInstallDTO dto = modelInstallService.getWorkOrderModel(user.getId(),number);
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
    @PostMapping("/modelInstall/createWorkOrder")
    public ResponseBean createModelInstallOrder(HttpServletRequest request, @RequestBody ModelInstallDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_AFTER_SALE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            dto.setSubmitId(user.getId());

            //TODO 正式删除
/*            dto.setVerifyStatus(1);
            dto.setVerifyId(2);*/

            ModelInstall modelInstall = modelInstallService.createWorkOrder(dto);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "创建【模具安装调试服务报告单】", "t_model_install", modelInstall.getId(), JSON.toJSONString(modelInstall));
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

