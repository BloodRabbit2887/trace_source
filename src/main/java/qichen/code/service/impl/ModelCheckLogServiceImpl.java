package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.DeviseOrder;
import qichen.code.entity.ModelCheckLog;
import qichen.code.entity.SubmitTableOptions;
import qichen.code.entity.WorkOrder;
import qichen.code.entity.dto.DeviseOrderDTO;
import qichen.code.entity.dto.ModelCheckLogDTO;
import qichen.code.entity.dto.SubmitTableOptionDTO;
import qichen.code.entity.dto.TableOptionDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.ModelCheckLogMapper;
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
 * 零件检测尺寸特性表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Service
public class ModelCheckLogServiceImpl extends ServiceImpl<ModelCheckLogMapper, ModelCheckLog> implements IModelCheckLogService {

    @Autowired
    private IQualityOrderService qualityOrderService;
    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private ITableOptionsService tableOptionsService;
    @Autowired
    private ISubmitTableOptionsService submitTableOptionsService;


    @Override
    public ModelCheckLog submit(ModelCheckLogDTO dto) {

        checkDraft(dto);

        checkAlready(dto.getNumber());//同时只存在一张工单

        WorkOrder workOrder = workOrderService.getUnFinish(dto.getNumber());
        if (workOrder==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前设计部无待完成工单");
        }
/*        if (!workOrder.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前设计部无待完成工单");
        }*/

        //空值检验
        if (dto.getDraft()==null || dto.getDraft()==0){
            Map<String,String> params = new HashMap<>();
            params.put("number","模号");
            /*params.put("shapeSku","外形规格");
            params.put("upWeight","上模净重");
            params.put("downWeight","下模净重");
            params.put("stantdHeight","合模高度");
            params.put("lineHeight","送料线高度");
            params.put("matWidth","适用料宽");
            params.put("pace","步距");
            params.put("magnetic","模具磁力强度");
            params.put("speed","试模冲裁速度");
            params.put("punchVersion","试模冲床型号");
            params.put("spareParesStaus","备件种类及数量");
            params.put("externalVersion","外接电机型号");
            params.put("cylinderId","推出气缸型号");
            params.put("sampleLogStatus","冲样检测报告");
            params.put("pointImgsStatus","扣点高度关系图");
            params.put("downPaceCheckLogs","下模步距检测报告");
            params.put("upPaceCheckLogs","上模导正钉步距检测报告");
            params.put("cutting","刃口切纸");
            params.put("testMats","试冲料条");
            params.put("readme","模具说明书");
            params.put("lineImg","电控机构接线图");*/
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));
        }

        ModelCheckLog modelCheckLog = BeanUtils.copyAs(dto, ModelCheckLog.class);
        saveOrUpdate(modelCheckLog);

        dto.setId(modelCheckLog.getId());

        TableOptionDTO optionDTO = new TableOptionDTO();
        List<Integer> tableTypes = new ArrayList<>();
        tableTypes.add(TableOptionDTO.TYPE_MODEL_CHECK_MODEL);
        tableTypes.add(TableOptionDTO.TYPE_MODEL_CHECK_OTHER);
        tableTypes.add(TableOptionDTO.TYPE_MODEL_CHECK_MEANS);
        optionDTO.setTableTypes(tableTypes);
        optionDTO.setStatus(0);
        List<TableOptionDTO> optionDTOS = tableOptionsService.listByFilter(optionDTO, null);

        SubmitTableOptionDTO tableOptionDTO = new SubmitTableOptionDTO();
        tableOptionDTO.setTableTypes(tableTypes);
        tableOptionDTO.setOrderId(dto.getId());
        List<SubmitTableOptions> submitTableOptions = submitTableOptionsService.listFilter(tableOptionDTO,null);

        freshSubmitOptions(dto,optionDTOS,submitTableOptions);

        List<ModelCheckLogDTO.Item> items = dto.getItems();
        for (ModelCheckLogDTO.Item item : items) {
            submitTableOptionsService.saveOrUpdateBatch(BeanUtils.copyAs(item.getSubmitOptions(),SubmitTableOptions.class));
        }

        //TODO  正式删除
        workOrder.setDeptId(DeptTypeModel.DEPT_QUALITY);
        workOrderService.updateById(workOrder);

        return modelCheckLog;
    }

    private void checkAlready(String number) {
        QueryWrapper<ModelCheckLog> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("verifyStatus",1);
        wrapper.eq("`draft`",0);
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前模号存在已审核工单");
        }
    }

    private void checkDraft(ModelCheckLogDTO dto) {
        QueryWrapper<ModelCheckLog> wrapper = new QueryWrapper<>();
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
    public ModelCheckLogDTO getWorkOrderModel(Integer userId, String number) {
        ModelCheckLogDTO dto = new ModelCheckLogDTO();
        WorkOrder workOrder = workOrderService.getUnFinish(number);
        if (workOrder==null){
            return null;
        }
        ModelCheckLog order = getDraft(userId);
        if (order!=null){
            dto = BeanUtils.copyAs(order, ModelCheckLogDTO.class);
        }
        dto.setNumber(number);

        TableOptionDTO optionDTO = new TableOptionDTO();
        List<Integer> tableTypes = new ArrayList<>();
        tableTypes.add(TableOptionDTO.TYPE_MODEL_CHECK_MODEL);
        tableTypes.add(TableOptionDTO.TYPE_MODEL_CHECK_OTHER);
        tableTypes.add(TableOptionDTO.TYPE_MODEL_CHECK_MEANS);
        optionDTO.setTableTypes(tableTypes);
        optionDTO.setStatus(0);
        List<TableOptionDTO> optionDTOS = tableOptionsService.listByFilter(optionDTO, null);

        List<SubmitTableOptions> submitTableOptions = new ArrayList<>();
        if (dto.getId()!=null){
            SubmitTableOptionDTO tableOptionDTO = new SubmitTableOptionDTO();
            tableOptionDTO.setTableTypes(tableTypes);
            tableOptionDTO.setOrderId(dto.getId());
            submitTableOptions = submitTableOptionsService.listFilter(tableOptionDTO,null);
        }
        freshSubmitOptions(dto,optionDTOS,submitTableOptions);

        return dto;
    }

    @Override
    public ModelCheckLogDTO getVerify(String number) {
        QueryWrapper<ModelCheckLog> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("verifyStatus",1);
        wrapper.eq("draft",0);
        ModelCheckLog modelCheckLog = getOne(wrapper);
        if (modelCheckLog==null){
            return null;
        }
        return getDTO(modelCheckLog);
    }

    private ModelCheckLogDTO getDTO(ModelCheckLog modelCheckLog) {

        ModelCheckLogDTO dto = BeanUtils.copyAs(modelCheckLog, ModelCheckLogDTO.class);

        TableOptionDTO optionDTO = new TableOptionDTO();
        List<Integer> tableTypes = new ArrayList<>();
        tableTypes.add(TableOptionDTO.TYPE_MODEL_CHECK_MODEL);
        tableTypes.add(TableOptionDTO.TYPE_MODEL_CHECK_OTHER);
        tableTypes.add(TableOptionDTO.TYPE_MODEL_CHECK_MEANS);
        optionDTO.setTableTypes(tableTypes);
        optionDTO.setStatus(0);
        List<TableOptionDTO> optionDTOS = tableOptionsService.listByFilter(optionDTO, null);

        List<SubmitTableOptions> submitTableOptions = new ArrayList<>();
        if (dto.getId()!=null){
            SubmitTableOptionDTO tableOptionDTO = new SubmitTableOptionDTO();
            tableOptionDTO.setTableTypes(tableTypes);
            tableOptionDTO.setOrderId(dto.getId());
            submitTableOptions = submitTableOptionsService.listFilter(tableOptionDTO,null);
        }
        freshSubmitOptions(dto,optionDTOS,submitTableOptions);

        return dto;
    }

    private void freshSubmitOptions(ModelCheckLogDTO dto, List<TableOptionDTO> optionDTOS, List<SubmitTableOptions> submitTableOptions) {

        List<ModelCheckLogDTO.Item> items = new ArrayList<>();

        ModelCheckLogDTO.Item modelItem = new ModelCheckLogDTO.Item();
        modelItem.setTitle(TableOptionDTO.ITEM_MODEL_CHECK_MODEL);
        List<SubmitTableOptionDTO> subModels = new ArrayList<>();
        ModelCheckLogDTO.Item otherItem = new ModelCheckLogDTO.Item();
        otherItem.setTitle(TableOptionDTO.ITEM_MODEL_CHECK_OTHER);
        List<SubmitTableOptionDTO> subOthers = new ArrayList<>();
        ModelCheckLogDTO.Item meansItem = new ModelCheckLogDTO.Item();
        meansItem.setTitle(TableOptionDTO.ITEM_MODEL_CHECK_MEANS);
        List<SubmitTableOptionDTO> subMeans = new ArrayList<>();

        List<ModelCheckLogDTO.Item> dtoItems = dto.getItems();

        List<SubmitTableOptions> optionsList = optionDTOS.stream().map(optionDTO -> {
            SubmitTableOptions options = new SubmitTableOptions();
            options.setTableOptionId(optionDTO.getId());
            options.setTableType(optionDTO.getTableType());

            if (!CollectionUtils.isEmpty(submitTableOptions) && submitTableOptions.size() > 0) {
                for (SubmitTableOptions submitTableOption : submitTableOptions) {
                    if (submitTableOption.getTableOptionId().equals(options.getTableOptionId())) {
                        options = submitTableOption;
                    }
                }
            }
            if (!CollectionUtils.isEmpty(dtoItems) && dtoItems.size() > 0) {
                List<ModelCheckLogDTO.Item> list = dtoItems.stream().filter(item -> item.getTitle().equals(TableOptionDTO.MODEL_CHECK_MAP.get(optionDTO.getTableType()))).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(list) && list.size() > 0) {
                    List<SubmitTableOptionDTO> submitOptions = list.get(0).getSubmitOptions();
                    if (!CollectionUtils.isEmpty(submitOptions) && submitOptions.size() > 0) {
                        for (SubmitTableOptionDTO submitOption : submitOptions) {
                            if (submitOption.getTableOptionId().equals(optionDTO.getId())) {
                                options.setAnswer(submitOption.getAnswer());
                                options.setSubmitAnswerId(submitOption.getSubmitAnswerId());
                            }
                        }
                    }
                }
            }

            if (dto.getId()!=null){
                options.setOrderId(dto.getId());
            }
            return options;
        }).distinct().collect(Collectors.toList());

        List<SubmitTableOptionDTO> dtos = submitTableOptionsService.listDTO(optionsList);

        for (SubmitTableOptionDTO optionDTO : dtos) {
            if (optionDTO.getTableType().equals(TableOptionDTO.TYPE_MODEL_CHECK_MODEL)){
                subModels.add(optionDTO);
            }
            if (optionDTO.getTableType().equals(TableOptionDTO.TYPE_MODEL_CHECK_OTHER)){
                subOthers.add(optionDTO);
            }
            if (optionDTO.getTableType().equals(TableOptionDTO.TYPE_MODEL_CHECK_MEANS)){
                subMeans.add(optionDTO);
            }
        }

        modelItem.setSubmitOptions(subModels);
        items.add(modelItem);
        otherItem.setSubmitOptions(subOthers);
        items.add(otherItem);
        meansItem.setSubmitOptions(subMeans);
        items.add(meansItem);

        dto.setItems(items);
    }

    private ModelCheckLog getDraft(Integer userId) {
        QueryWrapper<ModelCheckLog> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",userId);
        wrapper.eq("`draft`",1);
        return getOne(wrapper);
    }


}
