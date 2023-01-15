package qichen.code.controller.admin;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.AssembleComponent;
import qichen.code.entity.Dept;
import qichen.code.entity.ModelPushOption;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.AssembleComponentDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.ResponseBean;
import qichen.code.service.*;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/v1/admin/assemble")
public class AdminAssembleController {

    @Autowired
    private IAssembleCheckAlloyPackageService assembleCheckAlloyPackageService;
    @Autowired
    private IAssembleComponentService assembleComponentService;
    @Autowired
    private IComponentOptionService componentOptionService;
    @Autowired
    private IOperationLogService operationLogService;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IModelPushOptionService modelPushOptionService;


    @ResponseBody
    @PostMapping("/component/add")
    public ResponseBean addComponent(HttpServletRequest request,
                                     @RequestBody AssembleComponentDTO assembleComponentDTO){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            AssembleComponent assembleComponent = assembleComponentService.add(assembleComponentDTO);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","添加【装配车间检测部件】表","t_assemble_component",assembleComponent.getId(), JSON.toJSONString(assembleComponent));
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
    @PostMapping("/component/update")
    public ResponseBean updateComponent(HttpServletRequest request,
                                     @RequestBody AssembleComponentDTO assembleComponentDTO){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            AssembleComponent assembleComponent = assembleComponentService.adminUpdate(assembleComponentDTO);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","修改【装配车间检测部件】表","t_assemble_component",assembleComponent.getId(), JSON.toJSONString(assembleComponent));
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @PostMapping("/component/delete")
    public ResponseBean deleteComponent(HttpServletRequest request, @RequestParam(value = "id") Integer id){
        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            AssembleComponent assembleComponent = assembleComponentService.delete(id);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","删除【装配车间检测部件】表","t_assemble_component",assembleComponent.getId(), JSON.toJSONString(assembleComponent));
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }



    @ResponseBody
    @PostMapping("/commitModelPushLogBatch")
    public ResponseBean commitModelPushLogBatch(HttpServletRequest request, @RequestParam(value = "list")List<String> list){

        AdminDTO admin = userContextUtils.newGetCurrentAdmin(request);
        if (admin == null) {
            return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
        }
        if (admin.getAdminType()!=1){
            return new ResponseBean(ResException.ADMIN_PER_MISS);
        }
        try {
            List<ModelPushOption> options = modelPushOptionService.commitModelPushLogBatch(list);
            operationLogService.saveOperationLog(admin.getAdminType(),admin.getId(),"310","批量添加【模具入库点检事项表】表","t_model_push_option",null, JSON.toJSONString(options));
            return new ResponseBean();
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

}
