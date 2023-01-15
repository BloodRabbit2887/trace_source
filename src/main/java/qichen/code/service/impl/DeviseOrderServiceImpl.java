package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.DeviseOrder;
import qichen.code.entity.SubmitTableOptions;
import qichen.code.entity.User;
import qichen.code.entity.WorkOrder;
import qichen.code.entity.dto.DeviseOrderDTO;
import qichen.code.entity.dto.SubmitTableOptionDTO;
import qichen.code.entity.dto.TableOptionDTO;
import qichen.code.entity.dto.WorkOrderDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.DeviseOrderMapper;
import qichen.code.model.DeptTypeModel;
import qichen.code.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 设计部工单表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Service
public class DeviseOrderServiceImpl extends ServiceImpl<DeviseOrderMapper, DeviseOrder> implements IDeviseOrderService {

    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private ITableOptionsService tableOptionsService;
    @Autowired
    private ISubmitTableOptionsService submitTableOptionsService;
    @Autowired
    private IUserService userService;

    @Transactional
    @Override
    public DeviseOrder createWorkOrder(DeviseOrderDTO dto) {

        checkDraft(dto);

        checkAlready(dto.getNumber());//同时只存在一张工单

        WorkOrder workOrder = workOrderService.getUnFinish(dto.getNumber());
        if (workOrder==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前设计部无待完成工单");
        }
/*        if (!workOrder.getDeptId().equals(DeptTypeModel.DEPT_DESIGN)){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前设计部无待完成工单");
        }*/
        //空值检验
        if (dto.getDraft()==null || dto.getDraft()==0){

            Map<String,String> params = new HashMap<>();
            params.put("number","模号");
/*            params.put("size","产品大小");
            params.put("statorId","定子叠铆类型");
            params.put("rotorId","转子叠铆类型");
            params.put("driveTypeId","驱动类型");
            params.put("tricknessId","料厚类型");
            params.put("matNumberId","材料牌号");
            params.put("broderId","搭边情况");
            params.put("outMetId","出料形式");*/
            params.put("pdf1","安装图和排样图涵盖气管及批量接线图");
            params.put("img1","安装图和排样图涵盖气管及批量接线图");
            params.put("pdf2","产品图列表");
            params.put("img2","产品图列表");
            params.put("submitId","创建人");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));
        }

        check(dto);

        DeviseOrder order = BeanUtils.copyAs(dto, DeviseOrder.class);
/*        order.setVerifyStatus(0);*/
        order.setStatus(0);
        order.setOrderID(workOrder.getId());
        saveOrUpdate(order);

        dto.setId(order.getId());

        TableOptionDTO optionDTO = new TableOptionDTO();
        optionDTO.setTableType(TableOptionDTO.TYPE_TABLE_DEVISE);
        optionDTO.setStatus(0);
        List<TableOptionDTO> optionDTOS = tableOptionsService.listByFilter(optionDTO, null);
        if (!CollectionUtils.isEmpty(optionDTOS) && optionDTOS.size()>0){
            freshSubmitOptions(dto,optionDTOS);
            List<SubmitTableOptionDTO> options = dto.getSubmitOptions();
            if (!CollectionUtils.isEmpty(options) && options.size()>0){
                for (SubmitTableOptionDTO option : options) {
                    option.setOrderId(order.getId());
                    if (!StringUtils.isEmpty(option.getSubmitOptionName()) && option.getSubmitOptionName().length()>0){
                        option.setAnswer(option.getSubmitOptionName());
                    }
                }
                List<SubmitTableOptions> submitTableOptions = BeanUtils.copyAs(options, SubmitTableOptions.class);
                submitTableOptionsService.saveOrUpdateBatch(submitTableOptions);
            }
        }

        //TODO  正式删除
        workOrder.setDeptId(DeptTypeModel.DEPT_DESIGN);
        workOrderService.updateById(workOrder);

        return order;
    }

    private void checkDraft(DeviseOrderDTO dto) {
        QueryWrapper<DeviseOrder> wrapper = new QueryWrapper<>();
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

    private void freshSubmitOptions(DeviseOrderDTO dto, List<TableOptionDTO> optionDTOS) {

        List<SubmitTableOptionDTO> submitOptions = dto.getSubmitOptions();

        SubmitTableOptionDTO submitTableOptionDTO = new SubmitTableOptionDTO();
        submitTableOptionDTO.setTableType(TableOptionDTO.TYPE_TABLE_DEVISE);
        submitTableOptionDTO.setOrderId(dto.getId());
        List<SubmitTableOptions> submitTableOptions = submitTableOptionsService.listFilter(submitTableOptionDTO,null);


        List<SubmitTableOptions> list = optionDTOS.stream().map(optionDTO -> {
            SubmitTableOptions options = new SubmitTableOptions();
            options.setOrderId(dto.getId());
            options.setTableType(TableOptionDTO.TYPE_TABLE_DEVISE);
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

    @Override
    public DeviseOrder getByNumber(String number) {
        QueryWrapper<DeviseOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("`draft`",0);
        wrapper.ne("`verifyStatus`",2);
        return getOne(wrapper);
    }

    @Override
    public void removeByNumber(String number, Integer otherId) {
        UpdateWrapper<DeviseOrder> wrapper = new UpdateWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.ne("`ID`",otherId);
        remove(wrapper);
    }

    @Override
    public DeviseOrder getDraft(Integer userId) {
        QueryWrapper<DeviseOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",userId);
        wrapper.eq("`draft`",1);
        return getOne(wrapper);
    }

    @Override
    public DeviseOrderDTO getWorkOrderModel(Integer userId,String number) {
        DeviseOrderDTO dto = new DeviseOrderDTO();
        WorkOrder workOrder = workOrderService.getUnFinish(number);
        if (workOrder==null){
            return null;
        }
        DeviseOrder order = getDraft(userId);
        if (order!=null){
            dto = BeanUtils.copyAs(order,DeviseOrderDTO.class);
        }
        dto.setNumber(number);
        TableOptionDTO optionDTO = new TableOptionDTO();
        optionDTO.setTableType(TableOptionDTO.TYPE_TABLE_DEVISE);
        optionDTO.setStatus(0);
        List<TableOptionDTO> optionDTOS = tableOptionsService.listByFilter(optionDTO, null);

        if (dto.getId()!=null){
            SubmitTableOptionDTO tableOptionDTO = new SubmitTableOptionDTO();
            tableOptionDTO.setTableType(TableOptionDTO.TYPE_TABLE_DEVISE);
            tableOptionDTO.setOrderId(dto.getId());
            List<SubmitTableOptions> submitTableOptions = submitTableOptionsService.listFilter(tableOptionDTO,null);
            dto.setSubmitOptions(BeanUtils.copyAs(submitTableOptions,SubmitTableOptionDTO.class));
            freshSubmitOptions(dto,optionDTOS);
        }else {
            List<SubmitTableOptions> list = optionDTOS.stream().map(tableOptionDTO -> {
                SubmitTableOptions options = new SubmitTableOptions();
                options.setTableType(TableOptionDTO.TYPE_TABLE_DEVISE);
                options.setTableOptionId(tableOptionDTO.getId());
                return options;
            }).collect(Collectors.toList());
            dto.setSubmitOptions(submitTableOptionsService.listDTO(list));
        }

        return dto;
    }

    @Override
    public DeviseOrderDTO getByWorkOrderId(Integer workerOrderId, boolean draft) {
        QueryWrapper<DeviseOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`orderID`",workerOrderId);
        wrapper.eq("`draft`",draft?1:0);
        if (!draft){
            wrapper.eq("`verifyStatus`",1);
        }
        DeviseOrder deviseOrder = getOne(wrapper);
        if (deviseOrder==null){
            return null;
        }
        return getDTO(deviseOrder);
    }

    private DeviseOrderDTO getDTO(DeviseOrder deviseOrder) {
        DeviseOrderDTO dto = BeanUtils.copyAs(deviseOrder, DeviseOrderDTO.class);

        TableOptionDTO optionDTO = new TableOptionDTO();
        optionDTO.setTableType(TableOptionDTO.TYPE_TABLE_DEVISE);
        optionDTO.setStatus(0);


        List<TableOptionDTO> optionDTOS = tableOptionsService.listByFilter(optionDTO, null);

        if (dto.getId()!=null){


            User user = userService.getById(deviseOrder.getSubmitId());
            if (user!=null){
                dto.setSubmitName(user.getName());
            }

            SubmitTableOptionDTO tableOptionDTO = new SubmitTableOptionDTO();
            tableOptionDTO.setTableType(TableOptionDTO.TYPE_TABLE_DEVISE);
            tableOptionDTO.setOrderId(dto.getId());
            List<SubmitTableOptions> submitTableOptions = submitTableOptionsService.listFilter(tableOptionDTO,null);
            dto.setSubmitOptions(BeanUtils.copyAs(submitTableOptions,SubmitTableOptionDTO.class));
            freshSubmitOptions(dto,optionDTOS);
        }

        return dto;
    }

    private void check(DeviseOrderDTO dto) {
        //TODO
    }

    private void checkAlready(String number) {
        QueryWrapper<DeviseOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("verifyStatus",1);
        wrapper.eq("`draft`",0);
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前模号存在已审核工单");
        }
    }


}
