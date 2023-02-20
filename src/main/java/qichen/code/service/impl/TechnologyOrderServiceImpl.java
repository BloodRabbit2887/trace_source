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
import qichen.code.entity.dto.DeviseOrderDTO;
import qichen.code.entity.dto.SubmitTableOptionDTO;
import qichen.code.entity.dto.TableOptionDTO;
import qichen.code.entity.dto.TechonologyOrderDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.TechnologyOrderMapper;
import qichen.code.model.DeptTypeModel;
import qichen.code.model.Filter;
import qichen.code.model.TableTypeModel;
import qichen.code.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 工艺部工单表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Service
public class TechnologyOrderServiceImpl extends ServiceImpl<TechnologyOrderMapper, TechnologyOrder> implements ITechnologyOrderService {

    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private ITableOptionsService tableOptionsService;
    @Autowired
    private ISubmitTableOptionsService submitTableOptionsService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IAdminService adminService;
    @Autowired
    private IUserTableProjectService userTableProjectService;

    @Transactional
    @Override
    public TechnologyOrder createWorkOrder(TechonologyOrderDTO dto) {

/*        checkDraft(dto);*/
        if (dto.getId()==null){
            removeDraft(dto);
        }

        checkAlready(dto.getNumber());//同时只存在一张工单

        WorkOrder workOrder = workOrderService.getUnFinish(dto.getNumber());
        if (workOrder==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前工艺部无待完成工单");
        }
/*        if (!workOrder.getDeptId().equals(DeptTypeModel.DEPT_TECHNOLOGY)){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前工艺部无待完成工单");
        }*/

        //空值检验
        if (dto.getDraft()==null || dto.getDraft()==0){
            Map<String,String> params = new HashMap<>();
/*            params.put("wedmLsTime","慢丝工时");
            params.put("wedmHsTime","快丝工时");
            params.put("flexTime","曲磨工时");
            params.put("grinderTime","型磨工时");
            params.put("cylindricalTime","外圆磨工时");
            params.put("modelTime","标磨工时");
            params.put("jtxTime","精镗铣工时");
            params.put("smallTime","小型加工中心工时");
            params.put("dragonTime","龙门加工中心工时");*/
            params.put("submitId","创建人");
            params.put("number","模号");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));

            userTableProjectService.updateStatus(dto.getNumber(),dto.getSubmitId(),1, TableTypeModel.TABLE_ONE);
        }

        TechnologyOrder technologyOrder = BeanUtils.copyAs(dto, TechnologyOrder.class);
        technologyOrder.setOrderID(workOrder.getId());
        saveOrUpdate(technologyOrder);


        dto.setId(technologyOrder.getId());

        TableOptionDTO optionDTO = new TableOptionDTO();
        optionDTO.setTableType(TableOptionDTO.TYPE_TABLE_TECHNOLOGY);
        optionDTO.setStatus(0);
        List<TableOptionDTO> optionDTOS = tableOptionsService.listByFilter(optionDTO, null);
        if (!CollectionUtils.isEmpty(optionDTOS) && optionDTOS.size()>0){
            freshSubmitOptions(dto,optionDTOS);
            List<SubmitTableOptionDTO> options = dto.getSubmitOptions();
            if (!CollectionUtils.isEmpty(options) && options.size()>0){
                for (SubmitTableOptionDTO option : options) {
                    option.setOrderId(technologyOrder.getId());
                    if (!StringUtils.isEmpty(option.getSubmitOptionName()) && option.getSubmitOptionName().length()>0){
                        option.setAnswer(option.getSubmitOptionName());
                    }
                }
                List<SubmitTableOptions> submitTableOptions = BeanUtils.copyAs(options, SubmitTableOptions.class);
                submitTableOptionsService.saveOrUpdateBatch(submitTableOptions);
            }
        }

        //TODO  正式删除
/*        workOrder.setDeptId(DeptTypeModel.DEPT_TECHNOLOGY);
        workOrderService.updateById(workOrder);*/


        return technologyOrder;
    }

    private void removeDraft(TechonologyOrderDTO dto) {
        UpdateWrapper<TechnologyOrder> wrapper = new UpdateWrapper<>();
        wrapper.eq("submitId",dto.getSubmitId());
        wrapper.eq("draft",1);
        wrapper.eq("`number`",dto.getNumber());
        remove(wrapper);
    }


    private void freshSubmitOptions(TechonologyOrderDTO dto, List<TableOptionDTO> optionDTOS) {

        List<SubmitTableOptionDTO> submitOptions = dto.getSubmitOptions();

        SubmitTableOptionDTO submitTableOptionDTO = new SubmitTableOptionDTO();
        submitTableOptionDTO.setTableType(TableOptionDTO.TYPE_TABLE_TECHNOLOGY);
        submitTableOptionDTO.setOrderId(dto.getId());
        List<SubmitTableOptions> submitTableOptions = submitTableOptionsService.listFilter(submitTableOptionDTO,null);


        List<SubmitTableOptions> list = optionDTOS.stream().map(optionDTO -> {
            SubmitTableOptions options = new SubmitTableOptions();
            options.setOrderId(dto.getId());
            options.setTableType(TableOptionDTO.TYPE_TABLE_TECHNOLOGY);
            options.setTableOptionId(optionDTO.getId());

            if (!CollectionUtils.isEmpty(submitTableOptions) && submitTableOptions.size()>0){
                for (SubmitTableOptions submitTableOption : submitTableOptions) {
                    if (submitTableOption.getTableOptionId().equals(options.getTableOptionId())){
                        options.setAnswer(submitTableOption.getAnswer());
                        options.setSubmitAnswerId(submitTableOption.getSubmitAnswerId());
                        options.setId(submitTableOption.getId());
                    }
                }
            }

            if (!CollectionUtils.isEmpty(submitOptions) && submitOptions.size()>0){
                for (SubmitTableOptionDTO option : submitOptions) {
                    if (option.getTableOptionId().equals(options.getTableOptionId())){
                        options.setAnswer(option.getAnswer());
                        options.setSubmitAnswerId(option.getSubmitAnswerId());
                    }
                }
            }

            return options;
        }).collect(Collectors.toList());


        if (!CollectionUtils.isEmpty(list) && list.size()>0){
            /*            submitTableOptionsService.saveOrUpdateBatch(list);*/
            dto.setSubmitOptions(submitTableOptionsService.listDTO(list));
        }
    }


    private void checkDraft(TechonologyOrderDTO dto) {
        QueryWrapper<TechnologyOrder> wrapper = new QueryWrapper<>();
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
    public TechnologyOrder getDraft(Integer userId) {
        QueryWrapper<TechnologyOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",userId);
        wrapper.eq("`draft`",1);
        return getOne(wrapper);
    }

    @Override
    public TechnologyOrder getByNumber(String number) {
        QueryWrapper<TechnologyOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("`draft`",0);
        wrapper.ne("`verifyStatus`",2);
        return getOne(wrapper);
    }

    @Override
    public void removeByNumber(String number, Integer otherId) {
        UpdateWrapper<TechnologyOrder> wrapper = new UpdateWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.ne("ID",otherId);
        remove(wrapper);
    }

    @Override
    public TechonologyOrderDTO getWorkOrderModel(Integer userId, String number) {
        TechonologyOrderDTO dto = new TechonologyOrderDTO();
        WorkOrder workOrder = workOrderService.getUnFinish(number);
        if (workOrder==null){
            return null;
        }
        TechnologyOrder order = getDraft(userId);
        if (order!=null){
            dto = BeanUtils.copyAs(order,TechonologyOrderDTO.class);
        }
        dto.setNumber(number);
        TableOptionDTO optionDTO = new TableOptionDTO();
        optionDTO.setTableType(TableOptionDTO.TYPE_TABLE_TECHNOLOGY);
        optionDTO.setStatus(0);
        List<TableOptionDTO> optionDTOS = tableOptionsService.listByFilter(optionDTO, null);

        if (dto.getId()!=null){
            SubmitTableOptionDTO tableOptionDTO = new SubmitTableOptionDTO();
            tableOptionDTO.setTableType(TableOptionDTO.TYPE_TABLE_TECHNOLOGY);
            tableOptionDTO.setOrderId(dto.getId());
            List<SubmitTableOptions> submitTableOptions = submitTableOptionsService.listFilter(tableOptionDTO,null);
            dto.setSubmitOptions(BeanUtils.copyAs(submitTableOptions,SubmitTableOptionDTO.class));
            freshSubmitOptions(dto,optionDTOS);
        }else {
            List<SubmitTableOptions> list = optionDTOS.stream().map(tableOptionDTO -> {
                SubmitTableOptions options = new SubmitTableOptions();
                options.setTableType(TableOptionDTO.TYPE_TABLE_TECHNOLOGY);
                options.setTableOptionId(tableOptionDTO.getId());
                return options;
            }).collect(Collectors.toList());
            dto.setSubmitOptions(submitTableOptionsService.listDTO(list));
        }

        return dto;
    }

    @Override
    public TechonologyOrderDTO getByWorkOrderId(Integer workerOrderId, boolean draft) {
        QueryWrapper<TechnologyOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`orderID`",workerOrderId);
        wrapper.eq("`draft`",draft?1:0);
        if (!draft){
            wrapper.eq("`verifyStatus`",1);
        }
        TechnologyOrder technologyOrder = getOne(wrapper);
        if (technologyOrder==null){
            return null;
        }
        return getDTO(technologyOrder);
    }

    @Override
    public List<TechonologyOrderDTO> listByFilter(TechonologyOrderDTO dto, Filter filter) {
        List<TechnologyOrder> list = listFilter(dto,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }


    private List<TechonologyOrderDTO> listDTO(List<TechnologyOrder> list) {
        List<TechonologyOrderDTO> dtos = BeanUtils.copyAs(list, TechonologyOrderDTO.class);

        List<User> users = (List<User>) userService.listByIds(list.stream().map(TechnologyOrder::getSubmitId).distinct().collect(Collectors.toList()));
        List<Admin> admins = new ArrayList<>();
        List<TechnologyOrder> verifys = list.stream().filter(checkPackage -> checkPackage.getVerifyId() != null).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(verifys) && verifys.size()>0){
            admins = (List<Admin>) adminService.listByIds(verifys.stream().map(TechnologyOrder::getVerifyId).distinct().collect(Collectors.toList()));
        }
        for (TechonologyOrderDTO dto : dtos) {
            if (!CollectionUtils.isEmpty(users) && users.size()>0){
                for (User user : users) {
                    if (user.getId().equals(dto.getSubmitId())){
                        dto.setSubmitName(user.getName());
                    }
                }
            }
            if (!CollectionUtils.isEmpty(admins) && admins.size()>0){
                for (Admin admin : admins) {
                    if (admin.getId().equals(dto.getVerifyId())){
                        dto.setVerifyName(admin.getAdminName());
                    }
                }
            }
        }

        return dtos;
    }

    @Override
    public BigInteger listCount(TechonologyOrderDTO dto, Filter filter) {
        QueryWrapper<TechnologyOrder> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPage(null);
        }
        addFilter(wrapper,dto,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    private void addFilter(QueryWrapper<TechnologyOrder> wrapper, TechonologyOrderDTO dto, Filter filter) {
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
            if (!StringUtils.isEmpty(filter.getKeyword()) && filter.getKeyword().length()>0){
                wrapper.like("`title`",filter.getKeyword());
            }
            if (filter.getPage()!=null && filter.getPageSize()!=null && filter.getPage()!=0 && filter.getPageSize()!=0){
                int fast = filter.getPage()<=1?0:(filter.getPage()-1)*filter.getPageSize();
                wrapper.last(" limit "+fast+", "+filter.getPageSize());
            }
        }
    }

    @Override
    public List<TechnologyOrder> listFilter(TechonologyOrderDTO dto, Filter filter) {
        QueryWrapper<TechnologyOrder> wrapper = new QueryWrapper<>();
        addFilter(wrapper,dto,filter);
        return list(wrapper);
    }

    @Override
    public TechonologyOrderDTO getDetail(Integer id) {
        TechnologyOrder order = getById(id);
        if (order==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(order);
    }


    private TechonologyOrderDTO getDTO(TechnologyOrder technologyOrder) {

        TechonologyOrderDTO dto = BeanUtils.copyAs(technologyOrder, TechonologyOrderDTO.class);

        User submitUser = userService.getById(technologyOrder.getSubmitId());
        if (submitUser!=null){
            dto.setSubmitName(submitUser.getName());
        }
        if (dto.getVerifyId()!=null){
            User verifyUser = userService.getById(dto.getVerifyId());
            if (verifyUser!=null){
                dto.setVerifyName(verifyUser.getName());
            }
        }


        SubmitTableOptionDTO tableOptionDTO = new SubmitTableOptionDTO();
        tableOptionDTO.setTableType(TableOptionDTO.TYPE_TABLE_TECHNOLOGY);
        tableOptionDTO.setOrderId(dto.getId());
        List<SubmitTableOptions> submitTableOptions = submitTableOptionsService.listFilter(tableOptionDTO,null);

        TableOptionDTO optionDTO = new TableOptionDTO();
        optionDTO.setTableType(TableOptionDTO.TYPE_TABLE_TECHNOLOGY);
        optionDTO.setStatus(0);
        List<TableOptionDTO> optionDTOS = tableOptionsService.listByFilter(optionDTO, null);

        dto.setSubmitOptions(BeanUtils.copyAs(submitTableOptions,SubmitTableOptionDTO.class));
        freshSubmitOptions(dto,optionDTOS);

        return dto;
    }

    private void checkAlready(String number) {
        QueryWrapper<TechnologyOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("`draft`",0);
        wrapper.eq("`verifyStatus`",1);
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前模号存在已审核工单");
        }
    }


}
