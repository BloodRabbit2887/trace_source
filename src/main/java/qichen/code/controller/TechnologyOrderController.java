package qichen.code.controller;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import qichen.code.entity.TechnologyOrder;
import qichen.code.entity.WorkOrder;
import qichen.code.entity.dto.DeviseOrderDTO;
import qichen.code.entity.dto.TechonologyOrderDTO;
import qichen.code.entity.dto.UserDTO;
import qichen.code.entity.dto.WorkOrderDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.DeptTypeModel;
import qichen.code.model.ResponseBean;
import qichen.code.service.IOperationLogService;
import qichen.code.service.ITechnologyOrderService;
import qichen.code.service.IUserService;
import qichen.code.service.IWorkOrderService;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 工艺部工单表 前端控制器
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Slf4j
@RestController
@RequestMapping("/code/technology-order")
public class TechnologyOrderController {

    @Autowired
    private IUserService userService;
    @Autowired
    private ITechnologyOrderService technologyOrderService;
    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IOperationLogService operationLogService;


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
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_TECHNOLOGY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            TechonologyOrderDTO dto = technologyOrderService.getWorkOrderModel(user.getId(),number);
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
                                        @RequestBody TechonologyOrderDTO techonologyOrderDTO){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_TECHNOLOGY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            techonologyOrderDTO.setSubmitId(user.getId());

            //TODO 正式删除
            techonologyOrderDTO.setVerifyStatus(1);
            techonologyOrderDTO.setVerifyId(2);

            TechnologyOrder technologyOrder = technologyOrderService.createWorkOrder(techonologyOrderDTO);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "创建工单【工艺部】", "t_technology_order", technologyOrder.getId(), JSON.toJSONString(technologyOrder));
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
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_TECHNOLOGY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getVerifyPermission()==0){
            return new ResponseBean(new BusinessException(ResException.USER_PER_MISS.getCode(),"无审核权限,请联系部门主管"));
        }
        try {
            WorkOrder workOrder = workOrderService.verifyWorkOrder(user.getId(),number,status,verifyRemark);
            operationLogService.saveOperationLog(user.getType(),user.getId(),"410","工单审核【工艺部】","t_work_order",workOrder.getId(), JSON.toJSONString(workOrder));
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
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_TECHNOLOGY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getType()!=1){
            return new ResponseBean(new BusinessException(ResException.USER_PER_MISS.getCode(),"职工无环节推进权限,请联系部门主管"));
        }
        try {
            WorkOrder workOrder = workOrderService.linkChange(user.getId(),number,status,remark);
            operationLogService.saveOperationLog(user.getType(),user.getId(),"410","工单环节推进【工艺部】","t_work_order",workOrder.getId(), JSON.toJSONString(workOrder));
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

