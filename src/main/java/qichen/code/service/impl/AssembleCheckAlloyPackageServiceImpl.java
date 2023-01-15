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
import qichen.code.entity.dto.AssembleCheckAlloyPackageDTO;
import qichen.code.entity.dto.AssembleComponentDTO;
import qichen.code.entity.dto.ComponentOptionDTO;
import qichen.code.entity.dto.SubmitComponentOptionDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.AssembleCheckAlloyPackageMapper;
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
 * 合金组装组扭转部位工作检查表(装配车间) 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-05
 */
@Service
public class AssembleCheckAlloyPackageServiceImpl extends ServiceImpl<AssembleCheckAlloyPackageMapper, AssembleCheckAlloyPackage> implements IAssembleCheckAlloyPackageService {

    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private IAssembleComponentService assembleComponentService;
    @Autowired
    private IComponentOptionService componentOptionService;
    @Autowired
    private ISubmitComponentOptionService submitComponentOptionService;

    @Transactional
    @Override
    public AssembleCheckAlloyPackage add(AssembleCheckAlloyPackageDTO dto) {
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

        AssembleCheckAlloyPackage alloyPackage = BeanUtils.copyAs(dto, AssembleCheckAlloyPackage.class);
        saveOrUpdate(alloyPackage);

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
                    option.setCheckTableId(alloyPackage.getId());
                    return option;
                }).collect(Collectors.toList());

                submitComponentOptionService.saveOrUpdateBatch(options);
            }
        }

        return alloyPackage;
    }

    @Override
    public AssembleCheckAlloyPackage verify(Integer id, Integer userId, Integer status, String remark) {

        AssembleCheckAlloyPackage alloyPackage = getById(id);
        if (alloyPackage==null || alloyPackage.getDraft()==1){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        alloyPackage.setVerifyStatus(status);
        alloyPackage.setVerifyId(userId);
        alloyPackage.setVerifyRemark(remark);
        alloyPackage.setVerifyTime(LocalDateTime.now());
        alloyPackage.setUpdateTime(LocalDateTime.now());
        updateById(alloyPackage);

        if (status==1){
            removeByNumber(alloyPackage.getNumber(),alloyPackage.getId());
        }

        return alloyPackage;
    }

    @Override
    public AssembleCheckAlloyPackageDTO getAlloyModel(Integer userId, String nummber) {
        AssembleCheckAlloyPackageDTO dto = new AssembleCheckAlloyPackageDTO();
        dto.setNumber(nummber);

        WorkOrder workOrder = workOrderService.getUnFinish(nummber);
        if (workOrder==null){
            return null;
        }

        AssembleCheckAlloyPackage alloyPackage = getDraft(userId);
        if (alloyPackage!=null){
            dto = BeanUtils.copyAs(alloyPackage,AssembleCheckAlloyPackageDTO.class);
        }

        return dto;
    }

    private AssembleCheckAlloyPackage getDraft(Integer userId) {
        QueryWrapper<AssembleCheckAlloyPackage> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",userId);
        wrapper.eq("`draft`",1);
        return getOne(wrapper);
    }

    private void removeByNumber(String number, Integer id) {
        UpdateWrapper<AssembleCheckAlloyPackage> wrapper = new UpdateWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.ne("ID",id);
        remove(wrapper);
    }

    private void checkAlready(String number) {
        QueryWrapper<AssembleCheckAlloyPackage> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("verifyStatus",1);
        wrapper.eq("`draft`",0);
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"【合金组装组扭转部位工作检查表】当前工单已存在");
        }
    }
}
