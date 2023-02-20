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
import qichen.code.entity.dto.AssembleDownPackageDTO;
import qichen.code.entity.dto.AssembleModelPushPackageDTO;
import qichen.code.entity.dto.ModelPushOptionDTO;
import qichen.code.entity.dto.SubmitModelPushOptionDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.AssembleModelPushPackageMapper;
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
    @Autowired
    private IUserService userService;
    @Autowired
    private IAdminService adminService;
    @Autowired
    private IUserTableProjectService userTableProjectService;

    @Transactional
    @Override
    public AssembleModelPushPackage add(AssembleModelPushPackageDTO dto) {

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

        if (dto.getDraft()==null || dto.getDraft()==0){
            Map<String,String> params = new HashMap<>();
            params.put("number","模号");
            params.put("submitId","创建人");
            params.put("submitOptions","入库点检项");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));
            userTableProjectService.updateStatus(dto.getNumber(),dto.getSubmitId(),1,AssembleTableTypeModel.TYPE_MODEL_PUSH);
        }

        AssembleModelPushPackage pushPackage = BeanUtils.copyAs(dto, AssembleModelPushPackage.class);
        saveOrUpdate(pushPackage);

        List<ModelPushOption> list = modelPushOptionService.list();
        if (!CollectionUtils.isEmpty(list) && list.size()>0){

            List<SubmitModelPushOption> submitModelPushOptions = new ArrayList<>();
            if (dto.getId()!=null){
                SubmitModelPushOptionDTO submitModelPushOptionDTO = new SubmitModelPushOptionDTO();
                submitModelPushOptionDTO.setPackageId(dto.getId());
                submitModelPushOptions = submitModelPushOptionService.listFilter(submitModelPushOptionDTO,new Filter());
            }


            List<SubmitModelPushOptionDTO> submitOptions = dto.getSubmitOptions();
            List<SubmitModelPushOption> finalSubmitModelPushOptions = submitModelPushOptions;
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
                if (!CollectionUtils.isEmpty(finalSubmitModelPushOptions) && finalSubmitModelPushOptions.size()>0){
                    for (SubmitModelPushOption pushOption : finalSubmitModelPushOptions) {
                        if (pushOption.getOptionId().equals(modelPushOption.getId())){
                            submitOption.setId(pushOption.getId());
                        }
                    }
                }
                return submitOption;
            }).collect(Collectors.toList());
            submitModelPushOptionService.saveOrUpdateBatch(options);
        }

        return pushPackage;
    }

    private void removeDraft(AssembleModelPushPackageDTO dto) {
        UpdateWrapper<AssembleModelPushPackage> wrapper = new UpdateWrapper<>();
        wrapper.eq("submitId",dto.getSubmitId());
        wrapper.eq("draft",1);
        wrapper.eq("`number`",dto.getNumber());
        remove(wrapper);
    }

    private void checkDraft(AssembleModelPushPackageDTO dto) {
        QueryWrapper<AssembleModelPushPackage> wrapper = new QueryWrapper<>();
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
    public AssembleModelPushPackage verify(Integer id, Integer userId, Integer status, String remark) {
        AssembleModelPushPackage pushPackage = getById(id);
        if (pushPackage==null || pushPackage.getDraft()==1){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        WorkOrder workOrder = workOrderService.getByNumber(pushPackage.getNumber());
        if (workOrder==null){
            throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
        }
        if (!workOrder.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单环节异常");
        }
        pushPackage.setVerifyStatus(status);
        pushPackage.setVerifyId(userId);
        pushPackage.setVerifyRemark(remark);
        pushPackage.setVerifyTime(LocalDateTime.now());
        pushPackage.setUpdateTime(LocalDateTime.now());
        updateById(pushPackage);

        if (status==1){
            removeByNumber(pushPackage.getNumber(),pushPackage.getId());
/*            workOrder.setTableType(AssembleTableTypeModel.TYPE_PLANK);*/
            workOrder.setDeptId(DeptTypeModel.DEPT_VERIFY);
            workOrder.setStatus(1);
            workOrder.setTableTypeStatus(1);
            workOrderService.updateById(workOrder);

            userTableProjectService.linkChange(DeptTypeModel.DEPT_WORK_ASSEMBLE,AssembleTableTypeModel.TYPE_MODEL_PUSH,pushPackage.getNumber());
        }

        return pushPackage;
    }

    @Override
    public List<AssembleModelPushPackage> listFilter(AssembleModelPushPackageDTO dto, Filter filter) {
        QueryWrapper<AssembleModelPushPackage> wrapper = new QueryWrapper<>();
        addFilter(wrapper,dto,filter);
        return list(wrapper);
    }

    @Override
    public AssembleModelPushPackageDTO getModel(Integer userId, String number) {


        AssembleModelPushPackageDTO dto = new AssembleModelPushPackageDTO();
        dto.setNumber(number);

        checkSubmit(number);

        WorkOrder workOrder = workOrderService.getUnFinish(number);
        if (workOrder==null){
            return null;
        }

        AssembleModelPushPackage pushPackage = getDraft(userId);
        if (pushPackage!=null){
            dto = BeanUtils.copyAs(pushPackage,AssembleModelPushPackageDTO.class);
        }

        ModelPushOptionDTO modelPushOptionDTO = new ModelPushOptionDTO();
        modelPushOptionDTO.setStatus(0);
        List<ModelPushOption> options = modelPushOptionService.listFilter(modelPushOptionDTO,new Filter());
        if (!CollectionUtils.isEmpty(options) && options.size()>0){
            options = options.stream().sorted(Comparator.comparing(ModelPushOption::getOrders)).collect(Collectors.toList());
            Integer id = dto.getId();
            List<SubmitModelPushOption> already = new ArrayList<>();
            if (id!=null){
                SubmitModelPushOptionDTO optionDTO = new SubmitModelPushOptionDTO();
                optionDTO.setPackageId(id);
                already = submitModelPushOptionService.listFilter(optionDTO, null);
            }
            List<SubmitModelPushOption> finalAlready = already;
            List<SubmitModelPushOptionDTO> optionDTOS = options.stream().map(option -> {
                SubmitModelPushOptionDTO submitDTO = new SubmitModelPushOptionDTO();
                submitDTO.setOptionId(option.getId());
                submitDTO.setTitle(option.getDetail());
                submitDTO.setMust(option.getMust());
                if (id != null) {
                    submitDTO.setPackageId(id);
                }
                if (!CollectionUtils.isEmpty(finalAlready) && finalAlready.size() > 0) {
                    for (SubmitModelPushOption pushOption : finalAlready) {
                        if (pushOption.getOptionId().equals(option.getId())) {
                            submitDTO.setId(pushOption.getId());
                            submitDTO.setAnswer(pushOption.getAnswer());
                            submitDTO.setRemark(pushOption.getRemark());
                        }
                    }
                }

                return submitDTO;
            }).collect(Collectors.toList());
            dto.setSubmitOptions(optionDTOS);
        }
        return dto;
    }

    private void checkSubmit(String number) {
        QueryWrapper<AssembleModelPushPackage> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        List<AssembleModelPushPackage> list = list(wrapper);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
            for (AssembleModelPushPackage alloyPackage : list) {
                if (alloyPackage.getDraft()==0 && alloyPackage.getVerifyStatus()!=2){
                    throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单已提交");
                }
            }
        }
    }

    @Override
    public List<AssembleModelPushPackageDTO> listByFilter(AssembleModelPushPackageDTO dto, Filter filter) {
        List<AssembleModelPushPackage> list = listFilter(dto, filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    private List<AssembleModelPushPackageDTO> listDTO(List<AssembleModelPushPackage> list) {
        List<AssembleModelPushPackageDTO> dtos = BeanUtils.copyAs(list, AssembleModelPushPackageDTO.class);
        //TODO
        List<User> users = (List<User>) userService.listByIds(list.stream().map(AssembleModelPushPackage::getSubmitId).distinct().collect(Collectors.toList()));
        List<User> admins = new ArrayList<>();
        List<AssembleModelPushPackage> verifys = list.stream().filter(checkPackage -> checkPackage.getVerifyId() != null).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(verifys) && verifys.size()>0){
            admins = (List<User>) userService.listByIds(verifys.stream().map(AssembleModelPushPackage::getVerifyId).distinct().collect(Collectors.toList()));
        }

        for (AssembleModelPushPackageDTO dto : dtos) {
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
    public BigInteger listCount(AssembleModelPushPackageDTO dto, Filter filter) {
        QueryWrapper<AssembleModelPushPackage> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFilter(wrapper,dto,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public AssembleModelPushPackage getByNumber(String number) {
        QueryWrapper<AssembleModelPushPackage> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("`draft`",0);
        wrapper.ne("`verifyStatus`",2);
        return getOne(wrapper);
    }

    private AssembleModelPushPackage getDraft(Integer userId) {
        QueryWrapper<AssembleModelPushPackage> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",userId);
        wrapper.eq("`draft`",1);
        return getOne(wrapper);
    }

    private void addFilter(QueryWrapper<AssembleModelPushPackage> wrapper, AssembleModelPushPackageDTO dto, Filter filter) {
        if (dto!=null){
            if (dto.getDraft()!=null){
                wrapper.eq("`draft`",dto.getDraft());
            }
            if (dto.getVerifyStatus()!=null){
                wrapper.eq("verifyStatus",dto.getVerifyStatus());
            }
            if (dto.getStatus()!=null){
                wrapper.eq("`Status`",dto.getStatus());
            }
            if (!StringUtils.isEmpty(dto.getNumber()) && dto.getNumber().length()>0){
                wrapper.eq("`number`",dto.getNumber());
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
