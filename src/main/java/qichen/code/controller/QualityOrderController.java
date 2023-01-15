package qichen.code.controller;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import qichen.code.entity.*;
import qichen.code.entity.dto.*;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.DeptTypeModel;
import qichen.code.model.ResponseBean;
import qichen.code.service.*;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 质量管理部工单表 前端控制器
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Slf4j
@RestController
@RequestMapping("/code/quality-order")
public class QualityOrderController {

    @Autowired
    private IQualityOrderService qualityOrderService;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IOperationLogService operationLogService;
    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private IModelCheckLogService modelCheckLogService;
    @Autowired
    private ISparePartsLogService sparePartsLogService;

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
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            QualityOrderDTO dto = qualityOrderService.getWorkOrderModel(user.getId(),number);
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
    public ResponseBean createWorkOrder(HttpServletRequest request, @RequestBody QualityOrderDTO qualityOrderDTO){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            qualityOrderDTO.setSubmitId(user.getId());

            //TODO 正式删除
            qualityOrderDTO.setVerifyStatus(1);
            qualityOrderDTO.setVerifyId(2);

            QualityOrder qualityOrder = qualityOrderService.createWorkOrder(qualityOrderDTO);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "创建工单【质管部】", "t_quality_order", qualityOrder.getId(), JSON.toJSONString(qualityOrder));
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
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getVerifyPermission()==0){
            return new ResponseBean(new BusinessException(ResException.USER_PER_MISS.getCode(),"无审核权限,请联系部门主管"));
        }
        try {
            WorkOrder workOrder = workOrderService.verifyWorkOrder(user.getId(),number,status,verifyRemark);
            operationLogService.saveOperationLog(user.getType(),user.getId(),"410","工单审核【质管部】","t_work_order",workOrder.getId(), JSON.toJSONString(workOrder));
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
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getType()!=1){
            return new ResponseBean(new BusinessException(ResException.USER_PER_MISS.getCode(),"职工无环节推进权限,请联系部门主管"));
        }
        try {
            WorkOrder workOrder = workOrderService.linkChange(user.getId(),number,status,remark);
            operationLogService.saveOperationLog(user.getType(),user.getId(),"410","工单环节推进【质管部】","t_work_order",workOrder.getId(), JSON.toJSONString(workOrder));
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
    @GetMapping("/getModelCheckModel")
    public ResponseBean getModelCheckModel(HttpServletRequest request,@RequestParam(value = "number") String number){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            ModelCheckLogDTO dto = modelCheckLogService.getWorkOrderModel(user.getId(),number);
            return new ResponseBean(dto);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


    //TODO 提交 模具检测报告单
    @ResponseBody
    @PostMapping("/submitModelCheckLog")
    public ResponseBean submitModelCheckLog(HttpServletRequest request, @RequestBody ModelCheckLogDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            dto.setSubmitId(user.getId());

            //TODO 正式删除
            dto.setVerifyStatus(1);
            dto.setVerifyId(2);

            ModelCheckLog modelCheckLog = modelCheckLogService.submit(dto);
            operationLogService.saveOperationLog(user.getType(),user.getId(),"410","提交【模具检测报告】表","t_model_check_log",modelCheckLog.getId(), JSON.toJSONString(modelCheckLog));
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
    @GetMapping("/getSparePartsLogModel")
    public ResponseBean getSparePartsLogModel(HttpServletRequest request,@RequestParam(value = "number") String number){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            SparePartsLogDTO dto = sparePartsLogService.getWorkOrderModel(user.getId(),number);
            return new ResponseBean(dto);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }
    //TODO 提交 零件检测报告
    @ResponseBody
    @PostMapping("/submitSparePartsLog")
    public ResponseBean submitSparePartsLog(HttpServletRequest request, @RequestBody SparePartsLogDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            dto.setSubmitId(user.getId());

            //TODO 正式删除
            dto.setVerifyStatus(1);
            dto.setVerifyId(2);

            SparePartsLog sparePartsLog = sparePartsLogService.submit(dto);
            operationLogService.saveOperationLog(user.getType(),user.getId(),"410","提交【零件检测报告】表","t_spare_parts_log",sparePartsLog.getId(), JSON.toJSONString(sparePartsLog));
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

