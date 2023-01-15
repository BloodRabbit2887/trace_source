package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import qichen.code.entity.AfterSaleOrder;
import qichen.code.entity.DeviseOrder;
import qichen.code.entity.ModelInstall;
import qichen.code.entity.WorkOrder;
import qichen.code.entity.dto.AfterSaleOrderDTO;
import qichen.code.entity.dto.ModelInstallDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.ModelInstallMapper;
import qichen.code.model.DeptTypeModel;
import qichen.code.service.IModelInstallService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IWorkOrderService;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 模具安装调试服务报告单 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-13
 */
@Service
public class ModelInstallServiceImpl extends ServiceImpl<ModelInstallMapper, ModelInstall> implements IModelInstallService {

    @Autowired
    private IWorkOrderService workOrderService;

    @Override
    public ModelInstall createWorkOrder(ModelInstallDTO dto) {

        checkAlready(dto.getNumber());

        checkDraft(dto);

        WorkOrder workOrder = workOrderService.getUnFinish(dto.getNumber());
        if (workOrder==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前售后部无待完成工单");
        }

        if (dto.getDraft()==null || dto.getDraft()==0){
            Map<String,String> params = new HashMap<>();
            params.put("number","模号");
            params.put("submitId","创建人");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));
        }

        ModelInstall modelInstall = BeanUtils.copyAs(dto, ModelInstall.class);
        saveOrUpdate(modelInstall);


        //TODO  正式删除
        workOrder.setDeptId(DeptTypeModel.DEPT_AFTER_SALE);
        workOrderService.updateById(workOrder);

        return modelInstall;
    }

    @Override
    public ModelInstallDTO getWorkOrderModel(Integer userId, String number) {
        ModelInstallDTO dto = new ModelInstallDTO();
        ModelInstall order = getDraft(userId);
        if (order!=null){
            dto = BeanUtils.copyAs(order, ModelInstallDTO.class);
        }
        dto.setNumber(number);
        return dto;
    }

    @Override
    public ModelInstallDTO getByNumber(String number, Integer draft) {
        QueryWrapper<ModelInstall> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("verifyStatus",1);
        wrapper.eq("`draft`",0);
        ModelInstall modelInstall = getOne(wrapper);
        if (modelInstall!=null){
            return getDTO(modelInstall);
        }
        return null;
    }

    private ModelInstallDTO getDTO(ModelInstall modelInstall) {
        ModelInstallDTO dto = BeanUtils.copyAs(modelInstall, ModelInstallDTO.class);
        //TODO
        return dto;
    }

    private ModelInstall getDraft(Integer userId) {
        QueryWrapper<ModelInstall> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",userId);
        wrapper.eq("`draft`",1);
        return getOne(wrapper);
    }

    private void checkDraft(ModelInstallDTO dto) {
        QueryWrapper<ModelInstall> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",dto.getSubmitId());
        wrapper.eq("draft",1);
        if (dto.getId()!=null){
            wrapper.ne("`ID`",dto.getId());
        }
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前存在草稿未完成");
        }
    }

    private void checkAlready(String number) {
        QueryWrapper<ModelInstall> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("verifyStatus",1);
        wrapper.eq("`draft`",0);
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前模号存在已审核工单");
        }
    }

    public static void main(String[] args) {
        JSONObject object = new JSONObject();
        for (Field field : ModelInstall.class.getDeclaredFields()) {
            object.put(field.getName(),"0");
        }
        System.out.println(JSON.toJSONString(object));
    }
}
