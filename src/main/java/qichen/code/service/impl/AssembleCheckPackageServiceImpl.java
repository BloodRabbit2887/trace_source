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
import qichen.code.entity.dto.*;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.AssembleCheckPackageMapper;
import qichen.code.model.AssembleTableTypeModel;
import qichen.code.model.DeptTypeModel;
import qichen.code.model.Filter;
import qichen.code.service.IAssembleCheckPackageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IAssembleComponentService;
import qichen.code.service.ISubmitComponentOptionService;
import qichen.code.service.IWorkOrderService;
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
 * 合金组装组工作检查表(装配车间) 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
@Service
public class AssembleCheckPackageServiceImpl extends ServiceImpl<AssembleCheckPackageMapper, AssembleCheckPackage> implements IAssembleCheckPackageService {

    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private IAssembleComponentService assembleComponentService;
    @Autowired
    private ISubmitComponentOptionService submitComponentOptionService;


    @Transactional
    @Override
    public AssembleCheckPackage add(AssembleCheckPackageDTO dto) {

        checkDraft(dto);

        checkAlready(dto.getNumber());

        //TODO
        WorkOrder workOrder = workOrderService.getUnFinish(dto.getNumber());
        if (workOrder==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前装配车间无待完成工单");
        }
/*        if (!workOrder.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前装配车间无待完成工单");
        }*/

        if (dto.getDraft()==null || dto.getDraft()==0){
            Map<String,String> params = new HashMap<>();
            params.put("number","模号");
            params.put("submitId","创建人");
            params.put("components","部件检查项");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));
        }

        AssembleCheckPackage checkPackage = BeanUtils.copyAs(dto, AssembleCheckPackage.class);
        saveOrUpdate(checkPackage);

        List<AssembleComponentDTO> components = dto.getComponents();
        if (!CollectionUtils.isEmpty(components) && components.size()>0){

            List<SubmitComponentOption> submitComponentOptions = new ArrayList<>();
            if (dto.getId()!=null){
                SubmitComponentOptionDTO submitComponentOptionDTO = new SubmitComponentOptionDTO();
                submitComponentOptionDTO.setCheckTableId(dto.getId());
                submitComponentOptions = submitComponentOptionService.listFilter(submitComponentOptionDTO,new Filter());
            }


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

                List<SubmitComponentOption> finalSubmitComponentOptions = submitComponentOptions;

                List<SubmitComponentOption> options = optionDTOS.stream().map(optionDTO -> {
                    SubmitComponentOption option = new SubmitComponentOption();
                    if (!CollectionUtils.isEmpty(submits) && submits.size()>0){
                        for (SubmitComponentOptionDTO submit : submits) {
                            if (submit.getOptionId().equals(optionDTO.getId())){
                                option = BeanUtils.copyAs(submit,SubmitComponentOption.class);
                            }
                        }
                    }

                    if (!CollectionUtils.isEmpty(finalSubmitComponentOptions) && finalSubmitComponentOptions.size()>0){
                        for (SubmitComponentOption componentOption : finalSubmitComponentOptions) {
                            if (componentOption.getOptionId().equals(optionDTO.getId())){
                                option.setId(componentOption.getId());
                            }
                        }
                    }

                    option.setOptionId(optionDTO.getId());
                    option.setCheckTableId(checkPackage.getId());
                    return option;
                }).collect(Collectors.toList());

                submitComponentOptionService.saveOrUpdateBatch(options);
            }
        }
        return checkPackage;
    }

    private void checkDraft(AssembleCheckPackageDTO dto) {
        QueryWrapper<AssembleCheckPackage> wrapper = new QueryWrapper<>();
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

    @Override
    public AssembleCheckPackage verify(Integer id, Integer userId, Integer status, String remark) {
        AssembleCheckPackage checkPackage = getById(id);
        if (checkPackage==null || checkPackage.getDraft()==1){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        checkPackage.setVerifyStatus(status);
        checkPackage.setVerifyId(userId);
        checkPackage.setVerifyRemark(remark);
        checkPackage.setVerifyTime(LocalDateTime.now());
        checkPackage.setUpdateTime(LocalDateTime.now());
        updateById(checkPackage);

        if (status==1){
            removeByNumber(checkPackage.getNumber(),checkPackage.getId());
        }

        return checkPackage;
    }

    @Override
    public AssembleCheckPackageDTO getAlloyModel(Integer userId, String nummber) {
        AssembleCheckPackageDTO dto = new AssembleCheckPackageDTO();
        dto.setNumber(nummber);

        WorkOrder workOrder = workOrderService.getUnFinish(nummber);
        if (workOrder==null){
            return null;
        }

        AssembleCheckPackage checkPackage = getDraft(userId);
        if (checkPackage!=null){
            dto = BeanUtils.copyAs(checkPackage,AssembleCheckPackageDTO.class);
        }

        AssembleComponentDTO assembleComponentDTO = new AssembleComponentDTO();
        assembleComponentDTO.setCheckType(AssembleTableTypeModel.TYPE_PACKAGE);
        assembleComponentDTO.setStatus(0);
        List<AssembleComponentDTO> assembleComponentDTOS = assembleComponentService.listByFilter(assembleComponentDTO,new Filter());
        if (!CollectionUtils.isEmpty(assembleComponentDTOS) && assembleComponentDTOS.size()>0){

            List<SubmitComponentOption> submitComponentOptions = new ArrayList<>();
            if (dto.getId()!=null){
                SubmitComponentOptionDTO submitComponentOptionDTO = new SubmitComponentOptionDTO();
                submitComponentOptionDTO.setCheckTableId(dto.getId());
                submitComponentOptions = submitComponentOptionService.listFilter(submitComponentOptionDTO,new Filter());
            }

            for (AssembleComponentDTO componentDTO : assembleComponentDTOS) {
                List<ComponentOptionDTO> componentOptions = componentDTO.getComponentOptions();
                if (!CollectionUtils.isEmpty(componentOptions) && componentOptions.size()>0){
                    List<SubmitComponentOption> finalSubmitComponentOptions = submitComponentOptions;

                    List<SubmitComponentOptionDTO> dtoList = componentOptions.stream().map(optionDTO -> {
                        SubmitComponentOptionDTO submitDTO = new SubmitComponentOptionDTO();
                        submitDTO.setOptionId(optionDTO.getId());
                        submitDTO.setType(optionDTO.getType());

                        if (!CollectionUtils.isEmpty(finalSubmitComponentOptions) && finalSubmitComponentOptions.size() > 0) {
                            for (SubmitComponentOption option : finalSubmitComponentOptions) {
                                if (option.getOptionId().equals(submitDTO.getOptionId())) {
                                    submitDTO = BeanUtils.copyAs(option, SubmitComponentOptionDTO.class);
                                }
                            }
                        }
                        submitDTO.setNumber(optionDTO.getNumber());
                        submitDTO.setCheckDetail(optionDTO.getDetail());
                        submitDTO.setNeeds(optionDTO.getNeeds());
                        return submitDTO;
                    }).collect(Collectors.toList());

                    componentDTO.setSubmitOptions(dtoList);
                    componentDTO.setComponentOptions(null);
                }
            }

            dto.setComponents(assembleComponentDTOS);
        }

        return dto;
    }

    private AssembleCheckPackage getDraft(Integer userId) {
        QueryWrapper<AssembleCheckPackage> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",userId);
        wrapper.eq("`draft`",1);
        return getOne(wrapper);
    }

    private void removeByNumber(String number, Integer id) {
        UpdateWrapper<AssembleCheckPackage> wrapper = new UpdateWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.ne("ID",id);
        remove(wrapper);
    }

    private void checkAlready(String number) {
       QueryWrapper<AssembleCheckPackage> wrapper = new QueryWrapper<>();
       wrapper.eq("`number`",number);
       wrapper.eq("verifyStatus",1);
       wrapper.eq("`draft`",0);
       Integer count = baseMapper.selectCount(wrapper);
       if (count>0){
           throw new BusinessException(ResException.MAKE_ERR.getCode(),"【合金组装组扭转部位工作检查表】当前工单已存在");
       }
    }
}
