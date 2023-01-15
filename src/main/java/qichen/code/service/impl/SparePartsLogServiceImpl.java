package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.*;
import qichen.code.entity.dto.*;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.SparePartsLogMapper;
import qichen.code.model.DeptTypeModel;
import qichen.code.model.Filter;
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
 * 零件检测报告表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Service
public class SparePartsLogServiceImpl extends ServiceImpl<SparePartsLogMapper, SparePartsLog> implements ISparePartsLogService {

    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private IQualityOrderService qualityOrderService;
    @Autowired
    private ISparePartsSizeService sparePartsSizeService;
    @Autowired
    private ITableOptionsService tableOptionsService;
    @Autowired
    private ISubmitTableOptionsService submitTableOptionsService;


    @Transactional
    @Override
    public SparePartsLog submit(SparePartsLogDTO dto) {

        checkDraft(dto);

        checkAlready(dto.getNumber());//同时只存在一张工单

        WorkOrder workOrder = workOrderService.getUnFinish(dto.getNumber());
        if (workOrder==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前质管部无待完成工单");
        }

        if (dto.getDraft()==null || dto.getDraft()==0){
            Map<String,String> params = new HashMap<>();
            params.put("number","模号");
            params.put("partNumber","部号");
            params.put("name","零件名称");
            params.put("count","数量");
            params.put("submitId","创建人");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));

        }

        SparePartsLog sparePartsLog = BeanUtils.copyAs(dto, SparePartsLog.class);
        saveOrUpdate(sparePartsLog);

        List<SparePartsSizeDTO> sparePartsSizes = dto.getSparePartsSizes();
        if (!CollectionUtils.isEmpty(sparePartsSizes) && sparePartsSizes.size()>0){
            for (SparePartsSizeDTO sparePartsSize : sparePartsSizes) {
                sparePartsSize.setLogId(sparePartsLog.getId());
                sparePartsSize.setSubmitId(sparePartsLog.getSubmitId());
            }
            sparePartsSizeService.saveOrUpdateBatch(BeanUtils.copyAs(sparePartsSizes, SparePartsSize.class));
        }


        TableOptionDTO optionDTO = new TableOptionDTO();
        optionDTO.setTableType(TableOptionDTO.TYPE_TABLE_SPARE_LOG);
        optionDTO.setStatus(0);
        List<TableOptionDTO> optionDTOS = tableOptionsService.listByFilter(optionDTO, null);
        if (!CollectionUtils.isEmpty(optionDTOS) && optionDTOS.size()>0){
            freshSubmitOptions(dto,optionDTOS);
            List<SubmitTableOptionDTO> options = dto.getSubmitOptions();
            if (!CollectionUtils.isEmpty(options) && options.size()>0){
                for (SubmitTableOptionDTO option : options) {
                    option.setOrderId(sparePartsLog.getId());
                    if (!StringUtils.isEmpty(option.getSubmitOptionName()) && option.getSubmitOptionName().length()>0){
                        option.setAnswer(option.getSubmitOptionName());
                    }
                }
                List<SubmitTableOptions> submitTableOptions = BeanUtils.copyAs(options, SubmitTableOptions.class);
                submitTableOptionsService.saveOrUpdateBatch(submitTableOptions);
            }
        }

        //TODO  正式删除
        workOrder.setDeptId(DeptTypeModel.DEPT_QUALITY);
        workOrderService.updateById(workOrder);

        return sparePartsLog;
    }

    @Override
    public SparePartsLogDTO getWorkOrderModel(Integer userId, String number) {
        SparePartsLogDTO dto = new SparePartsLogDTO();
        WorkOrder workOrder = workOrderService.getUnFinish(number);
        if (workOrder==null){
            return null;
        }

        SparePartsLog order = getDraft(userId);
        if (order!=null){
            dto = BeanUtils.copyAs(order,SparePartsLogDTO.class);
        }
        dto.setNumber(number);

        TableOptionDTO optionDTO = new TableOptionDTO();
        optionDTO.setTableType(TableOptionDTO.TYPE_TABLE_SPARE_LOG);
        optionDTO.setStatus(0);
        List<TableOptionDTO> optionDTOS = tableOptionsService.listByFilter(optionDTO, null);

        if (dto.getId()!=null){
            SubmitTableOptionDTO tableOptionDTO = new SubmitTableOptionDTO();
            tableOptionDTO.setTableType(TableOptionDTO.TYPE_TABLE_SPARE_LOG);
            tableOptionDTO.setOrderId(dto.getId());
            List<SubmitTableOptions> submitTableOptions = submitTableOptionsService.listFilter(tableOptionDTO,null);
            dto.setSubmitOptions(BeanUtils.copyAs(submitTableOptions,SubmitTableOptionDTO.class));
            freshSubmitOptions(dto,optionDTOS);
        }else {
            List<SubmitTableOptions> list = optionDTOS.stream().map(tableOptionDTO -> {
                SubmitTableOptions options = new SubmitTableOptions();
                options.setTableType(TableOptionDTO.TYPE_TABLE_SPARE_LOG);
                options.setTableOptionId(tableOptionDTO.getId());
                return options;
            }).collect(Collectors.toList());
            dto.setSubmitOptions(submitTableOptionsService.listDTO(list));
        }

        if (dto.getId()!=null){
            freshSparePartsSizes(dto);
        }

        return dto;
    }

    private void freshSparePartsSizes(SparePartsLogDTO dto) {
        SparePartsSizeDTO sparePartsSizeDTO = new SparePartsSizeDTO();
        sparePartsSizeDTO.setLogId(dto.getId());
        List<SparePartsSizeDTO> dtos = sparePartsSizeService.listByFilter(sparePartsSizeDTO,new Filter());
        if (!CollectionUtils.isEmpty(dtos) && dtos.size()>0){
            dto.setSparePartsSizes(dtos);
        }
    }

    @Override
    public SparePartsLog getDraft(Integer userId) {
        QueryWrapper<SparePartsLog> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",userId);
        wrapper.eq("`draft`",1);
        return getOne(wrapper);
    }

    @Override
    public SparePartsLogDTO getVerify(String number) {
        QueryWrapper<SparePartsLog> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("verifyStatus",1);
        wrapper.eq("draft",0);
        SparePartsLog sparePartsLog = getOne(wrapper);
        if (sparePartsLog==null){
            return null;
        }
        return getDTO(sparePartsLog);
    }

    private SparePartsLogDTO getDTO(SparePartsLog sparePartsLog) {
        SparePartsLogDTO dto = BeanUtils.copyAs(sparePartsLog, SparePartsLogDTO.class);

        TableOptionDTO optionDTO = new TableOptionDTO();
        optionDTO.setTableType(TableOptionDTO.TYPE_TABLE_SPARE_LOG);
        optionDTO.setStatus(0);
        List<TableOptionDTO> optionDTOS = tableOptionsService.listByFilter(optionDTO, null);

        SubmitTableOptionDTO tableOptionDTO = new SubmitTableOptionDTO();
        tableOptionDTO.setTableType(TableOptionDTO.TYPE_TABLE_SPARE_LOG);
        tableOptionDTO.setOrderId(dto.getId());
        List<SubmitTableOptions> submitTableOptions = submitTableOptionsService.listFilter(tableOptionDTO,null);
        dto.setSubmitOptions(BeanUtils.copyAs(submitTableOptions,SubmitTableOptionDTO.class));
        freshSubmitOptions(dto,optionDTOS);

        freshSparePartsSizes(dto);

        return dto;
    }

    private void freshSubmitOptions(SparePartsLogDTO dto, List<TableOptionDTO> optionDTOS) {

        List<SubmitTableOptionDTO> submitOptions = dto.getSubmitOptions();

        SubmitTableOptionDTO submitTableOptionDTO = new SubmitTableOptionDTO();
        submitTableOptionDTO.setTableType(TableOptionDTO.TYPE_TABLE_SPARE_LOG);
        submitTableOptionDTO.setOrderId(dto.getId());
        List<SubmitTableOptions> submitTableOptions = submitTableOptionsService.listFilter(submitTableOptionDTO,null);


        List<SubmitTableOptions> list = optionDTOS.stream().map(optionDTO -> {
            SubmitTableOptions options = new SubmitTableOptions();
            options.setOrderId(dto.getId());
            options.setTableType(TableOptionDTO.TYPE_TABLE_SPARE_LOG);
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

    private void checkAlready(String number) {
        QueryWrapper<SparePartsLog> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("verifyStatus",1);
        wrapper.eq("`draft`",0);
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前模号存在已审核工单");
        }
    }

    private void checkDraft(SparePartsLogDTO dto) {
        QueryWrapper<SparePartsLog> wrapper = new QueryWrapper<>();
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

    private SparePartsLog getAlready(Integer qualityOrderId) {
        QueryWrapper<SparePartsLog> wrapper = new QueryWrapper<>();
        wrapper.eq("`qualityOrderID`",qualityOrderId);
        wrapper.eq("`verifyStatus`",1);
        wrapper.eq("`draft`",0);
        return getOne(wrapper);
    }
}
