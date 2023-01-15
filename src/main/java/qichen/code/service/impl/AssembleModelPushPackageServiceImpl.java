package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.*;
import qichen.code.entity.dto.AssembleModelPushPackageDTO;
import qichen.code.entity.dto.SubmitModelPushOptionDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.AssembleModelPushPackageMapper;
import qichen.code.model.DeptTypeModel;
import qichen.code.service.IAssembleModelPushPackageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IModelPushOptionService;
import qichen.code.service.ISubmitModelPushOptionService;
import qichen.code.service.IWorkOrderService;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 合金组装组扭转部位工作检查表(装配车间) 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
@Service
public class AssembleModelPushPackageServiceImpl extends ServiceImpl<AssembleModelPushPackageMapper, AssembleModelPushPackage> implements IAssembleModelPushPackageService {

    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private IModelPushOptionService modelPushOptionService;
    @Autowired
    private ISubmitModelPushOptionService submitModelPushOptionService;

    @Transactional
    @Override
    public AssembleModelPushPackage add(AssembleModelPushPackageDTO dto) {
        //TODO
        WorkOrder workOrder = workOrderService.getUnFinish(dto.getNumber());
        if (workOrder==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前装配车间无待完成工单");
        }
        if (!workOrder.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前装配车间无待完成工单");
        }

        checkAlready(dto.getNumber());
        if (dto.getDraft()==null || dto.getDraft()==0){
            Map<String,String> params = new HashMap<>();
            params.put("title","标题");
            params.put("number","模号");
            params.put("submitId","创建人");
            params.put("submitOptions","入库点检项");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));
        }

        AssembleModelPushPackage pushPackage = BeanUtils.copyAs(dto, AssembleModelPushPackage.class);
        saveOrUpdate(pushPackage);

        List<ModelPushOption> list = modelPushOptionService.list();
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
            List<SubmitModelPushOptionDTO> submitOptions = dto.getSubmitOptions();
            List<SubmitModelPushOption> options = list.stream().map(modelPushOption -> {
                SubmitModelPushOption submitOption = new SubmitModelPushOption();
                submitOption.setOptionId(modelPushOption.getId());
                submitOption.setAnswer(2);
                submitOption.setPackageId(pushPackage.getId());
                if (!CollectionUtils.isEmpty(submitOptions) && submitOptions.size() > 0) {
                    for (SubmitModelPushOptionDTO option : submitOptions) {
                        if (option.getOptionId().equals(modelPushOption.getId())) {
                            submitOption = option;
                            submitOption.setPackageId(pushPackage.getId());
                            submitOption.setAnswer(option.getAnswer()==null?3:option.getAnswer());
                        }
                    }
                }
                return submitOption;
            }).collect(Collectors.toList());
            submitModelPushOptionService.saveOrUpdateBatch(options);
        }

        return null;
    }

    @Override
    public AssembleModelPushPackage verify(Integer id, Integer userId, Integer status, String remark) {
        AssembleModelPushPackage pushPackage = getById(id);
        if (pushPackage==null || pushPackage.getDraft()==1){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        pushPackage.setVerifyStatus(status);
        pushPackage.setVerifyId(userId);
        pushPackage.setVerifyRemark(remark);
        pushPackage.setVerifyTime(LocalDateTime.now());
        pushPackage.setUpdateTime(LocalDateTime.now());
        updateById(pushPackage);

        if (status==1){
            removeByNumber(pushPackage.getNumber(),pushPackage.getId());
        }

        return pushPackage;
    }

    private void removeByNumber(String number, Integer id) {
        UpdateWrapper<AssembleModelPushPackage> wrapper = new UpdateWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.ne("ID",id);
        remove(wrapper);
    }

    private void checkAlready(String number) {
        QueryWrapper<AssembleModelPushPackage> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("verifyStatus",1);
        wrapper.eq("`draft`",0);
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"【模具入库点检表】当前工单已存在");
        }
    }
}
