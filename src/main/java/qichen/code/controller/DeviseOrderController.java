package qichen.code.controller;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import qichen.code.entity.DeviseOrder;
import qichen.code.entity.WorkOrder;
import qichen.code.entity.dto.DeviseOrderDTO;
import qichen.code.entity.dto.UserDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.DeptTypeModel;
import qichen.code.model.ResponseBean;
import qichen.code.service.IDeviseOrderService;
import qichen.code.service.IOperationLogService;
import qichen.code.service.IWorkOrderService;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 设计部工单表 前端控制器
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Slf4j
@RestController
@RequestMapping("/code/devise-order")
public class DeviseOrderController {

    @Autowired
    private IDeviseOrderService deviseOrderService;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IOperationLogService operationLogService;
    @Autowired
    private IWorkOrderService workOrderService;

    @ResponseBody
    @GetMapping("/getWorkOrderModel")
    public ResponseBean getWorkOrderModel(HttpServletRequest request,@RequestParam(value = "number") String number){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_DESIGN)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            DeviseOrderDTO dto = deviseOrderService.getWorkOrderModel(user.getId(),number);
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
                                        @RequestBody DeviseOrderDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_DESIGN)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            dto.setSubmitId(user.getId());

            //TODO 正式删除
            dto.setVerifyStatus(1);
            dto.setVerifyId(2);

            DeviseOrder deviseOrder = deviseOrderService.createWorkOrder(dto);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "创建工单【设计部】", "t_devise_order", deviseOrder.getId(), JSON.toJSONString(deviseOrder));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    //工单审核
    @ResponseBody
    @GetMapping("/verifyWorkOrder")
    public ResponseBean verifyWorkOrder(HttpServletRequest request,
                                        @RequestParam(value = "number") String number,
                                        @RequestParam(value = "status") Integer status,
                                        @RequestParam(value = "verifyRemark",required = false) String verifyRemark){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_DESIGN)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getVerifyPermission()==0){
            return new ResponseBean(new BusinessException(ResException.USER_PER_MISS.getCode(),"无审核权限,请联系部门主管"));
        }
        try {
            WorkOrder workOrder = workOrderService.verifyWorkOrder(user.getId(),number,status,verifyRemark);
            operationLogService.saveOperationLog(user.getType(),user.getId(),"410","工单审核【设计部】","t_work_order",workOrder.getId(), JSON.toJSONString(workOrder));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


    //环节推送
    @ResponseBody
    @GetMapping("/linkChange")
    public ResponseBean linkChange(HttpServletRequest request,
                                   @RequestParam(value = "number") String number,
                                   @RequestParam(value = "status") Integer status,
                                   @RequestParam(value = "remark",required = false) String remark){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_DESIGN)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getType()!=1){
            return new ResponseBean(new BusinessException(ResException.USER_PER_MISS.getCode(),"职工无环节推进权限,请联系部门主管"));
        }
        try {
            WorkOrder workOrder = workOrderService.linkChange(user.getId(),number,status,remark);
            operationLogService.saveOperationLog(user.getType(),user.getId(),"410","工单环节推进【设计部】","t_work_order",workOrder.getId(), JSON.toJSONString(workOrder));
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

