package qichen.code.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.*;
import qichen.code.entity.dto.*;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.AssembleTableTypeModel;
import qichen.code.model.DeptTypeModel;
import qichen.code.model.ResponseBean;
import qichen.code.service.*;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/code/assemble")
public class AssembleController {

    @Autowired
    private IAssembleCheckAlloyPackageService assembleCheckAlloyPackageService;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IOperationLogService operationLogService;
    @Autowired
    private IAssembleCheckPackageService assembleCheckPackageService;
    @Autowired
    private IAssemblePlankPackageService assemblePlankPackageService;
    @Autowired
    private IAssembleDownPackageService assembleDownPackageService;
    @Autowired
    private IAssembleModelPushPackageService assembleModelPushPackageService;
    @Autowired
    private IMouldBasePackageService mouldBasePackageService;
    @Autowired
    private IAssembleOrderService assembleOrderService;
    @Autowired
    private IWorkOrderService workOrderService;


    @ResponseBody
    @GetMapping("/alloy/getModel")
    public ResponseBean getAlloyModel(HttpServletRequest request,@RequestParam(value = "number") String nummber){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (!user.getAssembleTableType().equals(AssembleTableTypeModel.TYPE_ALLOY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            AssembleCheckAlloyPackageDTO dto = assembleCheckAlloyPackageService.getAlloyModel(user.getId(),nummber);
            return new ResponseBean(dto);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


    //TODO 合金扭转检查提交
    @ResponseBody
    @PostMapping("/alloy/add")
    public ResponseBean addAlloy(HttpServletRequest request,@RequestBody AssembleCheckAlloyPackageDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (!user.getAssembleTableType().equals(AssembleTableTypeModel.TYPE_ALLOY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            dto.setSubmitId(user.getId());

            //TODO 正式删除
            dto.setVerifyStatus(1);
            dto.setVerifyId(2);

            AssembleCheckAlloyPackage alloyPackage = assembleCheckAlloyPackageService.add(dto);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "提交【合金组装组扭转部位工作检查】表(装配车间)", "t_assemble_check_alloy_package", alloyPackage.getId(), JSON.toJSONString(alloyPackage));
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
    @GetMapping("/alloy/verify")
    public ResponseBean verifyAlloy(HttpServletRequest request,@RequestParam(value = "id") Integer id,@RequestParam(value = "status") Integer status,@RequestParam(value = "remark") String remark){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getType()==null || user.getType()==0){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            AssembleCheckAlloyPackage alloyPackage = assembleCheckAlloyPackageService.verify(id,user.getId(),status,remark);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "审批【合金组装组扭转部位工作检查】表(装配车间)", "t_assemble_check_alloy_package", alloyPackage.getId(), JSON.toJSONString(alloyPackage));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


    //TODO 合金装组检查提交
    @ResponseBody
    @PostMapping("/package/add")
    public ResponseBean addPackage(HttpServletRequest request,@RequestBody AssembleCheckPackageDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (!user.getAssembleTableType().equals(AssembleTableTypeModel.TYPE_PACKAGE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            dto.setSubmitId(user.getId());
            AssembleCheckPackage checkPackage = assembleCheckPackageService.add(dto);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "提交【合金组装组工作检查表】表(装配车间)", "t_assemble_check_package", checkPackage.getId(), JSON.toJSONString(checkPackage));
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
    @GetMapping("/package/verify")
    public ResponseBean verifyPackage(HttpServletRequest request,@RequestParam(value = "id") Integer id,@RequestParam(value = "status") Integer status,@RequestParam(value = "remark") String remark){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_VERIFY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getType()==null || user.getType()==0){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            AssembleCheckPackage checkPackage = assembleCheckPackageService.verify(id,user.getId(),status,remark);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "审批【合金组装组工作检查表】表(装配车间)", "t_assemble_check_package", checkPackage.getId(), JSON.toJSONString(checkPackage));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


    //TODO 合金装组检查提交
    @ResponseBody
    @PostMapping("/plank/add")
    public ResponseBean addPlank(HttpServletRequest request,@RequestBody AssemblePlankPackageDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (!user.getAssembleTableType().equals(AssembleTableTypeModel.TYPE_PLANK)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            dto.setSubmitId(user.getId());
            AssemblePlankPackage checkPackage = assemblePlankPackageService.add(dto);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "提交【模架组导槽板工作检查表】表(装配车间)", "t_assemble_plank_package", checkPackage.getId(), JSON.toJSONString(checkPackage));
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
    @GetMapping("/plank/verify")
    public ResponseBean verifyPlank(HttpServletRequest request,@RequestParam(value = "id") Integer id,@RequestParam(value = "status") Integer status,@RequestParam(value = "remark") String remark){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getType()==null || user.getType()==0){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            AssemblePlankPackage plankPackage = assemblePlankPackageService.verify(id,user.getId(),status,remark);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "审批【模架组导槽板工作检查】表(装配车间)", " 表: t_assemble_plank_package", plankPackage.getId(), JSON.toJSONString(plankPackage));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    //TODO 合金装组检查提交
    @ResponseBody
    @PostMapping("/down/add")
    public ResponseBean addDown(HttpServletRequest request,@RequestBody AssembleDownPackageDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (!user.getAssembleTableType().equals(AssembleTableTypeModel.TYPE_DOWN)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            dto.setSubmitId(user.getId());
            AssembleDownPackage checkPackage = assembleDownPackageService.add(dto);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "提交【模架组下模座垫板工作检查】表(装配车间)", "t_assemble_down_package", checkPackage.getId(), JSON.toJSONString(checkPackage));
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
    @GetMapping("/down/verify")
    public ResponseBean verifyDown(HttpServletRequest request,@RequestParam(value = "id") Integer id,@RequestParam(value = "status") Integer status,@RequestParam(value = "remark") String remark){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getType()==null || user.getType()==0){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            AssembleDownPackage downPackage = assembleDownPackageService.verify(id,user.getId(),status,remark);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "审批【模架组下模座垫板工作检查】表(装配车间)", " 表: t_assemble_down_package", downPackage.getId(), JSON.toJSONString(downPackage));
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
    @PostMapping("/modelPush/add")
    public ResponseBean modelPushAdd(HttpServletRequest request,@RequestBody AssembleModelPushPackageDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (!user.getAssembleTableType().equals(AssembleTableTypeModel.TYPE_MODEL_PUSH)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            dto.setSubmitId(user.getId());
            AssembleModelPushPackage checkPackage = assembleModelPushPackageService.add(dto);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "提交【模具入库点检】表(装配车间)", "t_assemble_model_push_package", checkPackage.getId(), JSON.toJSONString(checkPackage));
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
    @GetMapping("/modelPush/verify")
    public ResponseBean verifyModelPush(HttpServletRequest request,@RequestParam(value = "id") Integer id,@RequestParam(value = "status") Integer status,@RequestParam(value = "remark") String remark){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getType()==null || user.getType()==0){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            AssembleModelPushPackage pushPackage = assembleModelPushPackageService.verify(id,user.getId(),status,remark);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "审批【模具入库点检】表(装配车间)", "t_assemble_model_push_package", pushPackage.getId(), JSON.toJSONString(pushPackage));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }



    //TODO 合金装组检查提交
    @ResponseBody
    @PostMapping("/base/add")
    public ResponseBean addBase(HttpServletRequest request,@RequestBody MouldBasePackageDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (!user.getAssembleTableType().equals(AssembleTableTypeModel.TYPE_MOULE_BASE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            dto.setSubmitId(user.getId());
            MouldBasePackage checkPackage = mouldBasePackageService.add(dto);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "提交【模架组装组工作检查】表(装配车间)", "t_mould_base_package", checkPackage.getId(), JSON.toJSONString(checkPackage));
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
    @GetMapping("/Base/verify")
    public ResponseBean verifyBase(HttpServletRequest request,@RequestBody MouldBasePackageDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_VERIFY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getType()==null || user.getType()==0){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            MouldBasePackage checkPackage = mouldBasePackageService.verify(dto,user.getId());
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "审批【模架组装组工作检查】表(装配车间)", "t_mould_base_package", checkPackage.getId(), JSON.toJSONString(checkPackage));
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
    @PostMapping("/createWorkOrder")
    public ResponseBean createWorkOrder(HttpServletRequest request,@RequestBody AssembleOrderDTO dto){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_VERIFY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getType()==null || user.getType()==0){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            dto.setSubmitId(user.getId());
            AssembleOrder assembleOrder = assembleOrderService.createWorkOrder(dto);
            operationLogService.saveOperationLog(user.getType(), user.getId(), "410", "添加【装配车间工单】表", "t_assemble_order", assembleOrder.getId(), JSON.toJSONString(assembleOrder));
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
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getVerifyPermission()==0){
            return new ResponseBean(new BusinessException(ResException.USER_PER_MISS.getCode(),"无审核权限,请联系部门主管"));
        }
        try {
            WorkOrder workOrder = workOrderService.verifyWorkOrder(user.getId(),number,status,verifyRemark);
            operationLogService.saveOperationLog(user.getType(),user.getId(),"410","工单审核【装配车间】","t_work_order",workOrder.getId(), JSON.toJSONString(workOrder));
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
        if (!user.getDeptId().equals(DeptTypeModel.DEPT_VERIFY)){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        if (user.getType()!=1){
            return new ResponseBean(new BusinessException(ResException.USER_PER_MISS.getCode(),"职工无环节推进权限,请联系部门主管"));
        }
        try {
            WorkOrder workOrder = workOrderService.linkChange(user.getId(),number,status,remark);
            operationLogService.saveOperationLog(user.getType(),user.getId(),"410","工单环节推进【装配车间】","t_work_order",workOrder.getId(), JSON.toJSONString(workOrder));
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
