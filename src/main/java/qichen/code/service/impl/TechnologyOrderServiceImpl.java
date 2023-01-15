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
import qichen.code.entity.TechnologyOrder;
import qichen.code.entity.WorkOrder;
import qichen.code.entity.dto.DeviseOrderDTO;
import qichen.code.entity.dto.SubmitTableOptionDTO;
import qichen.code.entity.dto.TableOptionDTO;
import qichen.code.entity.dto.TechonologyOrderDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.TechnologyOrderMapper;
import qichen.code.model.DeptTypeModel;
import qichen.code.service.ISubmitTableOptionsService;
import qichen.code.service.ITableOptionsService;
import qichen.code.service.ITechnologyOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IWorkOrderService;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Transactional
    @Override
    public TechnologyOrder createWorkOrder(TechonologyOrderDTO dto) {

        checkDraft(dto);

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
        workOrder.setDeptId(DeptTypeModel.DEPT_TECHNOLOGY);
        workOrderService.updateById(workOrder);


        return technologyOrder;
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

    private TechonologyOrderDTO getDTO(TechnologyOrder technologyOrder) {
        TechonologyOrderDTO dto = BeanUtils.copyAs(technologyOrder, TechonologyOrderDTO.class);
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
