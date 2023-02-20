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
import qichen.code.mapper.MouldBasePackageMapper;
import qichen.code.model.AssembleProjectModel;
import qichen.code.model.AssembleTableTypeModel;
import qichen.code.model.DeptTypeModel;
import qichen.code.model.Filter;
import qichen.code.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 模架组装组工作检查表(装配车间) 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-09
 */
@Service
public class MouldBasePackageServiceImpl extends ServiceImpl<MouldBasePackageMapper, MouldBasePackage> implements IMouldBasePackageService {

    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private IAssembleComponentService assembleComponentService;
    @Autowired
    private ISubmitComponentOptionService submitComponentOptionService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IAdminService adminService;
    @Autowired
    private IUserTableProjectService userTableProjectService;

    @Transactional
    @Override
    public MouldBasePackage add(MouldBasePackageDTO dto) {

/*        checkDraft(dto);*/
        if (dto.getId()==null){
            removeDraft(dto);
        }

        checkAlready(dto.getNumber());

        //TODO
        WorkOrder workOrder = workOrderService.getUnFinish(dto.getNumber());
        if (workOrder==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前装配车间无待完成工单");
        }
/*        if (!workOrder.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前装配车间无待完成工单");
        }*/

        checkAlready(dto.getNumber());
        if (dto.getDraft()==null || dto.getDraft()==0){
            Map<String,String> params = new HashMap<>();
            params.put("title","标题");
            params.put("number","模号");
            params.put("submitId","创建人");
            params.put("components","部件检查项");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));

            userTableProjectService.updateStatus(dto.getNumber(),dto.getSubmitId(),1,AssembleTableTypeModel.TYPE_MOULE_BASE);
        }

        MouldBasePackage checkPackage = BeanUtils.copyAs(dto, MouldBasePackage.class);
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

    private void removeDraft(MouldBasePackageDTO dto) {
        UpdateWrapper<MouldBasePackage> wrapper = new UpdateWrapper<>();
        wrapper.eq("submitId",dto.getSubmitId());
        wrapper.eq("draft",1);
        wrapper.eq("`number`",dto.getNumber());
        remove(wrapper);
    }

    private void checkDraft(MouldBasePackageDTO dto) {
        QueryWrapper<MouldBasePackage> wrapper = new QueryWrapper<>();
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
    public MouldBasePackage verify(MouldBasePackageDTO dto, Integer userId) {
        MouldBasePackage checkPackage = getById(dto.getId());
        if (checkPackage==null || checkPackage.getDraft()==1){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        WorkOrder workOrder = workOrderService.getByNumber(checkPackage.getNumber());
        if (workOrder==null){
            throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
        }
        if (!workOrder.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单环节异常");
        }

        MouldBasePackage basePackage = BeanUtils.copyAs(dto, MouldBasePackage.class);
        updateById(basePackage);

        if (basePackage.getStatus()==1){
            removeByNumber(checkPackage.getNumber(),checkPackage.getId());
            workOrder.setTableType(AssembleTableTypeModel.TYPE_DOWN);
            workOrder.setTableTypeStatus(0);
            workOrderService.updateById(workOrder);

            userTableProjectService.linkChange(DeptTypeModel.DEPT_WORK_ASSEMBLE,AssembleTableTypeModel.TYPE_MOULE_BASE,dto.getNumber());
        }

        return basePackage;
    }

    @Override
    public MouldBasePackageDTO getAlloyModel(Integer userId, String nummber) {
        MouldBasePackageDTO dto = new MouldBasePackageDTO();
        dto.setNumber(nummber);

        checkSubmit(nummber);

        WorkOrder workOrder = workOrderService.getUnFinish(nummber);
        if (workOrder==null){
            return null;
        }

        MouldBasePackage basePackage = getDraft(userId);
        if (basePackage!=null){
            dto = BeanUtils.copyAs(basePackage,MouldBasePackageDTO.class);
        }

        AssembleComponentDTO assembleComponentDTO = new AssembleComponentDTO();
        assembleComponentDTO.setCheckType(AssembleTableTypeModel.TYPE_MOULE_BASE);
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
                        submitDTO.setMust(optionDTO.getMust());

                        if (!CollectionUtils.isEmpty(finalSubmitComponentOptions) && finalSubmitComponentOptions.size() > 0) {
                            for (SubmitComponentOption option : finalSubmitComponentOptions) {
                                if (option.getOptionId().equals(submitDTO.getOptionId())) {
                                    submitDTO = BeanUtils.copyAs(option, SubmitComponentOptionDTO.class);
                                }
                            }
                        }
                        submitDTO.setNumber(optionDTO.getNumber());
                        submitDTO.setMust(optionDTO.getMust());
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

    private void checkSubmit(String number) {
        QueryWrapper<MouldBasePackage> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        List<MouldBasePackage> list = list(wrapper);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
            for (MouldBasePackage alloyPackage : list) {
                if (alloyPackage.getDraft()==0 && alloyPackage.getVerifyStatus()!=2){
                    throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单已提交");
                }
            }
        }
    }

    @Override
    public List<MouldBasePackageDTO> listByFilter(MouldBasePackageDTO dto, Filter filter) {
        List<MouldBasePackage> list = listFilter(dto,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    private List<MouldBasePackageDTO> listDTO(List<MouldBasePackage> list) {
        List<MouldBasePackageDTO> dtos = BeanUtils.copyAs(list, MouldBasePackageDTO.class);

        //TODO
        List<User> users = (List<User>) userService.listByIds(list.stream().map(MouldBasePackage::getSubmitId).distinct().collect(Collectors.toList()));
        List<User> admins = new ArrayList<>();
        List<MouldBasePackage> verifys = list.stream().filter(basePackage -> basePackage.getVerifyId() != null).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(verifys) && verifys.size()>0){
            admins = (List<User>) userService.listByIds(verifys.stream().map(MouldBasePackage::getVerifyId).distinct().collect(Collectors.toList()));
        }

        for (MouldBasePackageDTO dto : dtos) {
            if (!CollectionUtils.isEmpty(users) && users.size()>0){
                for (User user : users) {
                    if (user.getId().equals(dto.getSubmitId())){
                        dto.setSubmitName(user.getName());
                    }
                }
            }
            if (!CollectionUtils.isEmpty(admins) && admins.size()>0){
                for (User admin : admins) {
                    if (dto.getVerifyId()!=null && dto.getVerifyId().equals(admin.getId())){
                        dto.setVerifyName(admin.getName());
                    }
                }
            }
        }

        return dtos;
    }

    @Override
    public List<MouldBasePackage> listFilter(MouldBasePackageDTO dto, Filter filter) {
        QueryWrapper<MouldBasePackage> wrapper = new QueryWrapper<>();
        addFilter(wrapper,dto,filter);
        return list(wrapper);
    }

    @Override
    public MouldBasePackageDTO getAlloyDetailByNumber(String number, Integer userId) {
        QueryWrapper<MouldBasePackage> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("submitId",userId);
        MouldBasePackage alloyPackage = getOne(wrapper);
        if (alloyPackage==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(alloyPackage);
    }

    @Override
    public MouldBasePackageDTO getVerify(String number) {
        QueryWrapper<MouldBasePackage> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("verifyStatus",0);
        wrapper.eq("`draft`",0);
        wrapper.orderBy(true,false,"createTime");
        List<MouldBasePackage> list = list(wrapper);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
            return getDTO(list.get(0));
        }
        throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前模号无待审核工单");
    }

    @Override
    public MouldBasePackage getByNumber(String number) {
        QueryWrapper<MouldBasePackage> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("`draft`",0);
        wrapper.ne("`verifyStatus`",2);
        return getOne(wrapper);
    }

    @Override
    public Integer getIdByAssembleOther(Integer userId, String number) {
        QueryWrapper<MouldBasePackage> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",userId);
        wrapper.eq("`number`",number);
        wrapper.eq("verifyStatus",0);
        wrapper.eq("`draft`",1);
        MouldBasePackage entity = getOne(wrapper);
        if (entity!=null){
            return entity.getId();
        }
        return null;
    }

    private void addFilter(QueryWrapper<MouldBasePackage> wrapper, MouldBasePackageDTO dto, Filter filter) {
        if (dto!=null){
            if (!StringUtils.isEmpty(dto.getNumber()) && dto.getNumber().length()>0){
                wrapper.eq("`number`",dto.getNumber());
            }
            if (dto.getDraft()!=null){
                wrapper.eq("`draft`",dto.getDraft());
            }
            if (dto.getVerifyStatus()!=null){
                wrapper.eq("`verifyStatus`",dto.getVerifyStatus());
            }
            if (dto.getStatus()!=null){
                wrapper.eq("`Status`",dto.getStatus());
            }
            if (dto.getSubmit()!=null){
                wrapper.ne("verifyStatus",2);
            }
        }
        if (filter!=null){
            if (filter.getCreateTimeBegin()!=null){
                wrapper.ge("createTime",filter.getCreateTimeBegin());
            }
            if (filter.getCreateTimeEnd()!=null){
                wrapper.le("createTime",filter.getCreateTimeEnd());
            }
            if (!StringUtils.isEmpty(filter.getKeyword()) && filter.getKeyword().length()>0){
                wrapper.and(queryWrapper->queryWrapper.like("`title`",filter.getKeyword()).or().like("`number`",filter.getKeyword()));
            }
            if (!StringUtils.isEmpty(filter.getOrders()) && filter.getOrders().length()>0){
                if (filter.getOrderBy()!=null){
                    wrapper.orderBy(true,filter.getOrderBy(),filter.getOrders());
                }
            }
            if (filter.getPage()!=null && filter.getPageSize()!=null && filter.getPage()!=0 && filter.getPageSize()!=0){
                int fast = filter.getPage()<=1?0:(filter.getPage()-1)*filter.getPageSize();
                wrapper.last(" limit "+fast+", "+filter.getPageSize());
            }
        }
    }

    @Override
    public BigInteger listCount(MouldBasePackageDTO dto, Filter filter) {
        QueryWrapper<MouldBasePackage> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFilter(wrapper,dto,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public MouldBasePackageDTO getDetail(Integer id) {
        MouldBasePackage basePackage = getById(id);
        if (basePackage==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(basePackage);
    }

    private MouldBasePackageDTO getDTO(MouldBasePackage basePackage) {
        MouldBasePackageDTO dto = BeanUtils.copyAs(basePackage, MouldBasePackageDTO.class);

        User user = userService.getById(dto.getSubmitId());
        if (user!=null){
            dto.setSubmitName(user.getName());
        }
        if (dto.getVerifyId()!=null){
            User admin = userService.getById(dto.getVerifyId());
            if (admin!=null){
                dto.setVerifyName(admin.getName());
            }
        }

        AssembleComponentDTO assembleComponentDTO = new AssembleComponentDTO();
        assembleComponentDTO.setCheckType(AssembleTableTypeModel.TYPE_MOULE_BASE);
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
                        submitDTO.setMust(optionDTO.getMust());

                        if (!CollectionUtils.isEmpty(finalSubmitComponentOptions) && finalSubmitComponentOptions.size() > 0) {
                            for (SubmitComponentOption option : finalSubmitComponentOptions) {
                                if (option.getOptionId().equals(submitDTO.getOptionId())) {
                                    submitDTO = BeanUtils.copyAs(option, SubmitComponentOptionDTO.class);
                                }
                            }
                        }
                        submitDTO.setNumber(optionDTO.getNumber());
                        submitDTO.setMust(optionDTO.getMust());
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

    private MouldBasePackage getDraft(Integer userId) {
        QueryWrapper<MouldBasePackage> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",userId);
        wrapper.eq("`draft`",1);
        return getOne(wrapper);
    }

    private void removeByNumber(String number, Integer id) {
        UpdateWrapper<MouldBasePackage> wrapper = new UpdateWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.ne("ID",id);
        remove(wrapper);
    }

    private void checkAlready(String number) {
        QueryWrapper<MouldBasePackage> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("verifyStatus",1);
        wrapper.eq("`draft`",0);
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"【模架组装组工作检查表】当前工单已存在");
        }
    }
}
