package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.*;
import qichen.code.entity.dto.AssembleComponentDTO;
import qichen.code.entity.dto.AssembleDownPackageDTO;
import qichen.code.entity.dto.ComponentOptionDTO;
import qichen.code.entity.dto.SubmitComponentOptionDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.AssembleDownPackageMapper;
import qichen.code.model.DeptTypeModel;
import qichen.code.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 模架组下模座垫板工作检查表 (装配车间) 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
@Service
public class AssembleDownPackageServiceImpl extends ServiceImpl<AssembleDownPackageMapper, AssembleDownPackage> implements IAssembleDownPackageService {

    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private IAssembleComponentService assembleComponentService;
    @Autowired
    private IComponentOptionService componentOptionService;
    @Autowired
    private ISubmitComponentOptionService submitComponentOptionService;


    @Override
    public AssembleDownPackage add(AssembleDownPackageDTO dto) {
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
            params.put("components","部件检查项");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));
        }

        AssembleDownPackage downPackage = BeanUtils.copyAs(dto, AssembleDownPackage.class);
        saveOrUpdate(downPackage);

        List<AssembleComponentDTO> components = dto.getComponents();
        if (!CollectionUtils.isEmpty(components) && components.size()>0){
            List<AssembleComponentDTO> componentDTOS = assembleComponentService.listDTO(BeanUtils.copyAs(components, AssembleComponent.class));
            List<SubmitComponentOptionDTO> submits = new ArrayList<>();
            List<ComponentOptionDTO> optionDTOS = new ArrayList<>();
            for (AssembleComponentDTO component : components) {
                List<SubmitComponentOptionDTO> submitOptions = component.getSubmitOptions();
                if (!CollectionUtils.isEmpty(submitOptions) && submitOptions.size()>0){
                    submits.addAll(submitOptions);
                }
            }
            for (AssembleComponentDTO componentDTO : componentDTOS) {
                List<ComponentOptionDTO> componentOptions = componentDTO.getComponentOptions();
                if (!CollectionUtils.isEmpty(componentOptions) && componentOptions.size()>0){
                    optionDTOS.addAll(componentOptions);
                }
            }
            if (!CollectionUtils.isEmpty(optionDTOS) && optionDTOS.size()>0){

                List<SubmitComponentOption> options = optionDTOS.stream().map(optionDTO -> {
                    SubmitComponentOption option = new SubmitComponentOption();
                    if (!CollectionUtils.isEmpty(submits) && submits.size()>0){
                        for (SubmitComponentOptionDTO submit : submits) {
                            if (submit.getOptionId().equals(optionDTO.getId())){
                                option = BeanUtils.copyAs(submit,SubmitComponentOption.class);
                            }
                        }
                    }
                    option.setOptionId(optionDTO.getId());
                    option.setCheckTableId(downPackage.getId());
                    return option;
                }).collect(Collectors.toList());

                submitComponentOptionService.saveOrUpdateBatch(options);
            }
        }

        return downPackage;
    }

    private void checkAlready(String number) {
        QueryWrapper<AssembleDownPackage> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("verifyStatus",1);
        wrapper.eq("`draft`",0);
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"【合金组装组扭转部位工作检查表】当前工单已存在");
        }
    }

    @Override
    public AssembleDownPackage verify(Integer id, Integer userId, Integer status, String remark) {

        AssembleDownPackage downPackage = getById(id);
        if (downPackage==null || downPackage.getDraft()==1){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        downPackage.setVerifyStatus(status);
        downPackage.setVerifyId(userId);
        downPackage.setVerifyRemark(remark);
        downPackage.setVerifyTime(LocalDateTime.now());
        downPackage.setUpdateTime(LocalDateTime.now());
        updateById(downPackage);

        if (status==1){
            removeByNumber(downPackage.getNumber(),downPackage.getId());
        }

        return downPackage;
    }

    private void removeByNumber(String number, Integer id) {
        UpdateWrapper<AssembleDownPackage> wrapper = new UpdateWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.ne("ID",id);
        remove(wrapper);
    }
}

