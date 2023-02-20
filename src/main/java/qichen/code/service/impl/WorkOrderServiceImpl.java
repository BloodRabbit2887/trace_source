package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.controller.conf.FilePathConf;
import qichen.code.entity.*;
import qichen.code.entity.dto.*;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.WorkOrderMapper;
import qichen.code.model.*;
import qichen.code.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;
import qichen.code.utils.QRCodeUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 工单表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements IWorkOrderService {

    @Autowired
    private ICustomService customService;
    @Autowired
    private IUserService userService;
    @Autowired
    private FilePathConf filePathConf;
    @Autowired
    private IDeviseOrderService deviseOrderService;
    @Autowired
    private ITechnologyOrderService technologyOrderService;
    @Autowired
    private IQualityOrderService qualityOrderService;
    @Autowired
    private IAssembleOrderService assembleOrderService;
    @Autowired
    private IOptionService optionService;
    @Autowired
    private IAfterSaleOrderService afterSaleOrderService;
    @Autowired
    private IModelInstallService modelInstallService;
    @Autowired
    private IModelCheckLogService modelCheckLogService;
    @Autowired
    private ISparePartsLogService sparePartsLogService;
    @Autowired
    private IAssembleCheckAlloyPackageService assembleCheckAlloyPackageService;
    @Autowired
    private IAssembleCheckPackageService assembleCheckPackageService;
    @Autowired
    private IAssemblePlankPackageService assemblePlankPackageService;
    @Autowired
    private IAssembleDownPackageService assembleDownPackageService;
    @Autowired
    private IAssembleModelPushPackageService assembleModelPushPackageService;
    @Autowired
    private IMouldBasePackageService mouldBasePackageService;
    @Autowired
    private IDeptRoleService deptRoleService;
    @Autowired
    private IUserTableProjectService userTableProjectService;



    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(10,50,30L, TimeUnit.SECONDS,new LinkedBlockingDeque<>(),new CustomizableThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

    @Override
    public List<WorkOrderDTO> listByFilter(WorkOrderDTO workOrderDTO, Filter filter) {
        List<WorkOrder> list = listFilter(workOrderDTO,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    //工单审核
    @Transactional
    @Override
    public WorkOrder verifyWorkOrder(Integer userId, String number, Integer status, String verifyRemark) {
        User user = userService.getById(userId);
        if (user==null){
            throw new BusinessException(ResException.QUERY_MISS.getCode(),"用户信息异常");
        }

        DeptRole deptRole = deptRoleService.getByAdd(user.getDeptId(), user.getDeptRoleId());
        if (deptRole.getVerifyPermission()==0){
            throw new BusinessException(ResException.USER_PER_MISS);
        }

        WorkOrder workOrder = getByNumber(number);
        if (user.getDeptId().equals(DeptTypeModel.DEPT_SALE)){
            QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
            wrapper.eq("`number`",number);
            workOrder = getOne(wrapper);
        }
        if (workOrder==null){
            throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
        }
        if (workOrder.getDraft()==1){
            throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
        }

        if (!workOrder.getDeptId().equals(user.getDeptId())){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前环节无操作权限");
        }
        if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_SALE)){//营销部
            if (status!=1){
                workOrder.setSaleStatus(0);
            }else {
                workOrder.setTableType(TableTypeModel.TABLE_ONE);
                workOrder.setTableTypeStatus(0);
                workOrder.setDeptId(DeptTypeModel.DEPT_DESIGN);
            }
            workOrder.setVerifyId(userId);
            workOrder.setVerifyType(2);
            workOrder.setVerifyStatus(status);
            workOrder.setVerifyRemark(verifyRemark);
            workOrder.setVerifyTime(LocalDateTime.now());
            workOrder.setUpdateTime(LocalDateTime.now());

        }else if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_DESIGN)){//设计部

            DeviseOrder deviseOrder = deviseOrderService.getByNumber(number);
            if (deviseOrder==null){
                throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
            }

            deviseOrder.setVerifyId(userId);
            deviseOrder.setVerifyStatus(status);
            deviseOrder.setVerifyRemark(verifyRemark);
            deviseOrder.setVerifyTime(LocalDateTime.now());
            deviseOrder.setUpdateTime(LocalDateTime.now());
            deviseOrderService.updateById(deviseOrder);
            if (status==1){
                deviseOrderService.removeByNumber(number,deviseOrder.getId());
                workOrder.setTableType(TableTypeModel.TABLE_ONE);
                workOrder.setTableTypeStatus(0);
                workOrder.setDeptId(DeptTypeModel.DEPT_TECHNOLOGY);
            }

            userTableProjectService.updateStatus(number,deviseOrder.getSubmitId(),status==1?3:0,TableTypeModel.TABLE_ONE);

        }else if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_TECHNOLOGY)){//工艺科
            TechnologyOrder technologyOrder = technologyOrderService.getByNumber(number);
            if (technologyOrder==null){
                throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
            }
            technologyOrder.setVerifyId(userId);
            technologyOrder.setVerifyStatus(status);
            technologyOrder.setVerifyRemark(verifyRemark);
            technologyOrder.setVerifyTime(LocalDateTime.now());
            technologyOrder.setUpdateTime(LocalDateTime.now());
            technologyOrderService.updateById(technologyOrder);

            if (status==1){
                technologyOrderService.removeByNumber(number,technologyOrder.getId());
                workOrder.setTableType(TableTypeModel.TABLE_ONE);
                workOrder.setTableTypeStatus(0);
                workOrder.setDeptId(DeptTypeModel.DEPT_QUALITY);
            }

            userTableProjectService.updateStatus(number,technologyOrder.getSubmitId(),status==1?3:0,TableTypeModel.TABLE_ONE);
        }else if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){//质量管理科
            QualityOrder qualityOrder = qualityOrderService.getByNumber(number);
            if (qualityOrder==null){
                throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
            }
            qualityOrder.setVerifyId(userId);
            qualityOrder.setVerifyStatus(status);
            qualityOrder.setVerifyRemark(verifyRemark);
            qualityOrder.setVerifyTime(LocalDateTime.now());
            qualityOrder.setUpdateTime(LocalDateTime.now());
            qualityOrderService.updateById(qualityOrder);

            if (status==1){
                workOrder.setTableType(TableTypeModel.TABLE_THREE);
                workOrder.setTableTypeStatus(0);
                qualityOrderService.removeByNumber(number,qualityOrder.getId());
            }

            userTableProjectService.updateStatus(number,qualityOrder.getSubmitId(),status==1?3:0,TableTypeModel.TABLE_ONE);

        }else if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){//装配车间
            AssembleOrder assembleOrder = assembleOrderService.getByNumber(number);
            if (assembleOrder==null){
                throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
            }
            assembleOrder.setVerifyId(userId);
            assembleOrder.setVerifyStatus(status);
            assembleOrder.setVerifyRemark(verifyRemark);
            assembleOrder.setVerifyTime(LocalDateTime.now());
            assembleOrder.setUpdateTime(LocalDateTime.now());
            assembleOrderService.updateById(assembleOrder);

            if (status==1){
                workOrder.setTableType(AssembleTableTypeModel.TYPE_MOULE_BASE);
                workOrder.setTableTypeStatus(0);
                assembleOrderService.removeByNumber(number,assembleOrder.getId());
            }

        }else if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_VERIFY)){//质量管理部/检验科

        }else if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_AFTER_SALE)){//装配调试售后服务科

        }
        updateById(workOrder);
        return workOrder;
    }

    @Transactional
    @Override
    public WorkOrder linkChange(Integer userId, String number,Integer status,String remark) {
        User user = userService.getById(userId);
        if (user==null){
            throw new BusinessException(ResException.QUERY_MISS.getCode(),"用户信息异常");
        }

        WorkOrder workOrder = getByNumber(number);
        if (workOrder==null){
            throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
        }
        if (workOrder.getDraft()==1){
            throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
        }
        if (!workOrder.getDeptId().equals(user.getDeptId())){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前环节无操作权限");
        }
        if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_SALE)){//营销部
            workOrder.setDeptId(DeptTypeModel.DEPT_DESIGN);
            workOrder.setSaleStatus(status);
            workOrder.setSaleRemark(remark);
        }else if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_DESIGN)){//设计部
            DeviseOrder deviseOrder = deviseOrderService.getByNumber(number);
            if (deviseOrder==null){
                throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
            }
            workOrder.setDeptId(DeptTypeModel.DEPT_TECHNOLOGY);
            deviseOrder.setStatus(status);
            deviseOrderService.updateById(deviseOrder);
        }else if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_TECHNOLOGY)){//工艺科
            TechnologyOrder technologyOrder = technologyOrderService.getByNumber(number);
            if (technologyOrder==null){
                throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
            }
            workOrder.setDeptId(DeptTypeModel.DEPT_QUALITY);
            technologyOrder.setStatus(status);
            technologyOrderService.updateById(technologyOrder);
        }else if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){//质量管理科
            QualityOrder qualityOrder = qualityOrderService.getByNumber(number);
            if (qualityOrder==null){
                throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
            }
            workOrder.setDeptId(DeptTypeModel.DEPT_WORK_ASSEMBLE);
            qualityOrder.setStatus(status);
            qualityOrderService.updateById(qualityOrder);

            AssembleOrder assembleOrder = new AssembleOrder();
            assembleOrder.setOrderID(workOrder.getId());
            assembleOrder.setNumber(number);
            assembleOrder.setStatus(0);
            assembleOrder.setDraft(0);
            assembleOrder.setVerifyStatus(1);
            assembleOrder.setVerifyTime(LocalDateTime.now());
            assembleOrder.setStatus(0);
            assembleOrderService.save(assembleOrder);

        }else if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_VERIFY)){//装配车间
            AssembleOrder assembleOrder = assembleOrderService.getByNumber(number);
            if (assembleOrder==null){
                assembleOrder = new AssembleOrder();
                assembleOrder.setOrderID(workOrder.getId());
                assembleOrder.setNumber(number);
                assembleOrder.setStatus(1);
                assembleOrder.setDraft(0);
                assembleOrder.setSubmitId(userId);
                assembleOrder.setVerifyId(userId);
                assembleOrder.setVerifyStatus(1);
                assembleOrder.setVerifyTime(LocalDateTime.now());
                /*                throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");*/
            }
            workOrder.setDeptId(DeptTypeModel.DEPT_VERIFY);
            workOrder.setStatus(1);
            assembleOrder.setStatus(status);
            assembleOrderService.updateById(assembleOrder);
        }
        workOrder.setUpdateTime(LocalDateTime.now());
        workOrder.setTableType(0);
        workOrder.setTableTypeStatus(0);
        updateById(workOrder);
        return workOrder;
    }

    @Override
    public WorkOrder getByNumber(String number) {
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("`verifyStatus`",1);
        return getOne(wrapper);
    }


    private List<WorkOrderDTO> listDTO(List<WorkOrder> list) {

        List<WorkOrderDTO> dtos = BeanUtils.copyAs(list, WorkOrderDTO.class);

        List<Integer> customIds = list.stream().map(WorkOrder::getCustomId).distinct().collect(Collectors.toList());
        List<Custom> customs = (List<Custom>) customService.listByIds(customIds);

        List<Integer> userIds = list.stream().map(WorkOrder::getSubmitId).distinct().collect(Collectors.toList());
        List<User> users = (List<User>) userService.listByIds(userIds);

        OptionDTO optionDTO = new OptionDTO();
        optionDTO.setTypeId(12);
        List<Option> options = optionService.listFilter(optionDTO, null);

        for (WorkOrderDTO dto : dtos) {

            dto.setDeptName(DeptTypeModel.TYPE_MAP.get(dto.getDeptId()));

            if (!CollectionUtils.isEmpty(options) && options.size()>0){
                for (Option option : options) {
                    if (option.getId().equals(dto.getCount())){
                        dto.setCountStr(option.getTitle());
                    }
                }
            }

            if (!CollectionUtils.isEmpty(customs) && customs.size()>0){
                for (Custom custom : customs) {
                    if (custom.getId().equals(dto.getCustomId())){
                        dto.setCustomName(custom.getName());
                    }
                }
            }
            if (!CollectionUtils.isEmpty(users) && users.size()>0){
                for (User user : users) {
                    if (user.getId().equals(dto.getSubmitId())){
                        dto.setSubmitName(user.getName());
                    }
                }
            }
        }

        //TODO
        return dtos;
    }

    private List<WorkOrder> listFilter(WorkOrderDTO workOrderDTO, Filter filter) {
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        addFitler(wrapper,workOrderDTO,filter);
        return list(wrapper);
    }

    private void addFitler(QueryWrapper<WorkOrder> wrapper, WorkOrderDTO workOrderDTO, Filter filter) {
        if (workOrderDTO!=null){
            if (workOrderDTO.getDraft()!=null){
                wrapper.eq("draft",workOrderDTO.getDraft());
            }
            if (!StringUtils.isEmpty(workOrderDTO.getNumber()) && workOrderDTO.getNumber().length()>0){
                wrapper.eq("number",workOrderDTO.getNumber());
            }
            if (workOrderDTO.getStatus()!=null){
                wrapper.eq("`Status`",workOrderDTO.getStatus());
            }
            if (workOrderDTO.getCount()!=null){
                wrapper.eq("`count`",workOrderDTO.getCount());
            }
            if (workOrderDTO.getModelTypeId()!=null){
                wrapper.eq("`modelTypeId`",workOrderDTO.getModelTypeId());
            }
            if (workOrderDTO.getElectricTypeId()!=null){
                wrapper.eq("electricTypeId",workOrderDTO.getElectricTypeId());
            }
            if (workOrderDTO.getSaleId()!=null){
                wrapper.eq("saleId",workOrderDTO.getSaleId());
            }
            if (!StringUtils.isEmpty(workOrderDTO.getSaleName()) && workOrderDTO.getSaleName().length()>0){
                List<Integer> userIds = new ArrayList<>();
                userIds.add(0);
                UserDTO userDTO = new UserDTO();
                userDTO.setName(workOrderDTO.getSaleName());
                List<User> users = userService.listFilter(userDTO, null);
                if (!CollectionUtils.isEmpty(users) && users.size()>0){
                    userIds.addAll(users.stream().map(User::getId).distinct().collect(Collectors.toList()));
                }
                wrapper.in("saleId",userIds);
            }
            if (!StringUtils.isEmpty(workOrderDTO.getCustomName()) && workOrderDTO.getCustomName().length()>0){
                List<Integer> customIds = new ArrayList<>();
                customIds.add(0);
                CustomDTO customDTO = new CustomDTO();
                customDTO.setName(workOrderDTO.getCustomName());
                List<Custom> customs = customService.listFilter(customDTO, null);
                if (!CollectionUtils.isEmpty(customs) && customs.size()>0){
                    customIds.addAll(customs.stream().map(Custom::getId).distinct().collect(Collectors.toList()));
                }
                wrapper.in("customId",customIds);
            }

            if (workOrderDTO.getDeptId()!=null){
                wrapper.eq("deptId",workOrderDTO.getDeptId());
            }
            if (workOrderDTO.getDeptStatus()!=null){
                wrapper.eq("deptStatus",workOrderDTO.getDeptStatus());
            }
            if (!StringUtils.isEmpty(workOrderDTO.getModelTitle()) && workOrderDTO.getModelTitle().length()>0){
                wrapper.like("modelTitle",workOrderDTO.getModelTitle());
            }
            if (workOrderDTO.getVerifyStatus()!=null){
                wrapper.eq("verifyStatus",workOrderDTO.getVerifyStatus());
            }
            if (!StringUtils.isEmpty(workOrderDTO.getCustomMobile()) && workOrderDTO.getCustomMobile().length()>0){
                wrapper.eq("customMobile",workOrderDTO.getCustomMobile());
            }
            if (workOrderDTO.getTableType()!=null){
                wrapper.eq("tableType",workOrderDTO.getTableType());
                wrapper.eq("tableTypeStatus",workOrderDTO.getTableTypeStatus());
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
                List<Integer> customIds = new ArrayList<>();
                customIds.add(0);
                Filter filter1 = new Filter();
                filter1.setKeyword(filter.getKeyword());
                List<Custom> customs = customService.listFilter(null, filter1);
                if (!CollectionUtils.isEmpty(customs) && customs.size()>0){
                    customIds.addAll(customs.stream().map(Custom::getId).distinct().collect(Collectors.toList()));
                }
                wrapper.and(queryWrapper->queryWrapper.like("modelTitle",filter.getKeyword()).or().like("number",filter.getKeyword()).or().in("customId",customIds));
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
    public BigInteger listCount(WorkOrderDTO workOrderDTO, Filter filter) {
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFitler(wrapper,workOrderDTO,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Transactional
    @Override
    public void createOrderBySale(WorkOrderDTO workOrderDTO) {

/*        checkDraft(workOrderDTO);*/
        if (workOrderDTO.getId()==null){
            removeDraft(workOrderDTO);
        }

        checkAlready(workOrderDTO.getNumber());//同时只存在一张工单

        if (workOrderDTO.getDraft()==null || workOrderDTO.getDraft()==0){

            Map<String,String> columns = new HashMap<>();
            columns.put("number","模号");
            columns.put("electricTypeId","电机类型");
            columns.put("modelTitle","模具名称");
            columns.put("count","列数");
            columns.put("modelTypeId","模具类别");
            columns.put("saleId","业务员");
            columns.put("customId","客户名称");
            columns.put("tecPDF","技术协议");
            columns.put("tecImg","技术协议");
            JsonUtils.checkColumnNull(columns, JSONObject.parseObject(JSON.toJSONString(workOrderDTO)));

/*            checkNumber(workOrderDTO.getNumber());*/
        }

        createQrcode(workOrderDTO);

        WorkOrder workOrder = BeanUtils.copyAs(workOrderDTO, WorkOrder.class);

        saveOrUpdate(workOrder);
    }

    private void removeDraft(WorkOrderDTO dto) {
        UpdateWrapper<WorkOrder> wrapper = new UpdateWrapper<>();
        wrapper.eq("submitId",dto.getSubmitId());
        wrapper.eq("draft",1);
        wrapper.eq("`number`",dto.getNumber());
        remove(wrapper);
    }

    private void checkDraft(WorkOrderDTO workOrderDTO) {
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",workOrderDTO.getSubmitId());
        wrapper.eq("draft",1);
        if (workOrderDTO.getId()!=null){
            wrapper.ne("`ID`",workOrderDTO.getId());
        }
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前存在草稿未完成");
        }
    }

    private void checkNumber(String number) {
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("`Status`",1);
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"模号重复");
        }
    }

    private void createQrcode(WorkOrderDTO workOrderDTO) {
        try {
            String code = QRCodeUtils.encode(workOrderDTO.getNumber(), filePathConf.getQuestion_local());
            workOrderDTO.setQrcode(filePathConf.getQuestion_net()+code);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"二维码生成失败");
        }
    }




    private void checkAlready(String number) {
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`Status`",0);
        wrapper.eq("verifyStatus",1);
        wrapper.eq("`draft`",0);
        wrapper.eq("`number`",number);
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"同模号只可同时存在一张已审核工单");
        }
    }





    @Override
    public Object getDraft(Integer userId, Integer submitType) {

        User user = userService.getById(userId);
        if (user==null){
            throw new BusinessException(ResException.QUERY_MISS.getCode(),"用户信息异常");
        }

        Map<String,Object> res = new HashMap<>();
        res.put("hasDraft",false);

        if (user.getDeptId().equals(DeptTypeModel.DEPT_SALE)){
            res.put("draftType", DeptDTO.TYPE_WORK_ORDER);//营销部
            QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
            wrapper.eq("submitId",userId);
            wrapper.eq("submitType",submitType);
            wrapper.eq("draft",1);
            WorkOrder workOrder = getOne(wrapper);
            if (workOrder!=null){
                res.put("hasDraft",true);
                res.put("draft",workOrder);
                return res;
            }
        }

        if (user.getDeptId().equals(DeptTypeModel.DEPT_DESIGN)){
            res.put("draftType", DeptDTO.TYPE_SALE_ORDER);//营销部
            DeviseOrder deviseOrder = deviseOrderService.getDraft(user.getId());
            if (deviseOrder!=null){
                res.put("hasDraft",true);
                res.put("draft",deviseOrder);
                return res;
            }
        }

        if (user.getDeptId().equals(DeptTypeModel.DEPT_TECHNOLOGY)){
            res.put("draftType", DeptDTO.TYPE_TECHNOLOGY_ORDER);//营销部
            TechnologyOrder technologyOrder = technologyOrderService.getDraft(user.getId());
            if (technologyOrder==null){
                res.put("hasDraft",true);
                res.put("draft",technologyOrder);
                return res;
            }
        }

        //TODO


        return res;
    }

    @Override
    public WorkOrder getUnFinish(String number) {
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`Status`",0);
        wrapper.eq("verifyStatus",1);
        wrapper.eq("`draft`",0);
        wrapper.eq("`number`",number);
        return getOne(wrapper);
    }

    @Override
    public WorkOrderDTO getOrderModel(Integer userId, String number) {
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`submitId`",userId);
        wrapper.eq("`draft`",1);
        WorkOrder order = getOne(wrapper);
        if (order!=null){
            return getDTO(order);
        }
        QueryWrapper<WorkOrder> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("`submitId`",userId);
        wrapper1.eq("`number`",number);
        wrapper1.eq("`verifyStatus`",0);
        WorkOrder workOrder = getOne(wrapper);
        if (workOrder!=null){
            return getDTO(workOrder);
        }
        WorkOrderDTO workOrderDTO = new WorkOrderDTO();
        workOrderDTO.setNumber(number);
        return workOrderDTO;
    }

    @Override
    public WorkOrderDTO getDetail(Integer id, boolean detail) {
        WorkOrder order = getById(id);
        if (order==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        WorkOrderDTO dto = getDTO(order);

        if (detail){

            //TODO 设计部
            CompletableFuture<Void> deviseFuture = CompletableFuture.runAsync(() -> {
                DeviseOrderDTO deviseOrderDTO = deviseOrderService.getByWorkOrderId(dto.getId(),false);
                if (deviseOrderDTO!=null){
                    dto.setDeviseUserName(deviseOrderDTO.getSubmitName());
                    dto.setDeviseCreateTime(deviseOrderDTO.getCreateTime());
                    dto.setDeviseImg1(deviseOrderDTO.getImg1());
                    dto.setDeviseImg2(deviseOrderDTO.getImg2());
                    dto.setDevisePdf1(deviseOrderDTO.getPdf1());
                    dto.setDevisePdf2(deviseOrderDTO.getPdf2());
                    if (!CollectionUtils.isEmpty(deviseOrderDTO.getSubmitOptions()) && deviseOrderDTO.getSubmitOptions().size()>0){
                        dto.setDeviseOptions(deviseOrderDTO.getSubmitOptions());
                    }
                }
            }, THREAD_POOL_EXECUTOR);

            //TODO 工艺部
            CompletableFuture<Void> technologyFuture = CompletableFuture.runAsync(() -> {
                TechonologyOrderDTO techonologyOrderDTO = technologyOrderService.getByWorkOrderId(dto.getId(),false);
                if (techonologyOrderDTO!=null){
                    User user = userService.getById(techonologyOrderDTO.getSubmitId());
                    if (user!=null){
                        dto.setTecUserName(user.getName());
                    }
                    dto.setTeCreateTime(techonologyOrderDTO.getCreateTime());
                    if (!CollectionUtils.isEmpty(techonologyOrderDTO.getSubmitOptions()) && techonologyOrderDTO.getSubmitOptions().size()>0){
                        dto.setTechnologyOptions(techonologyOrderDTO.getSubmitOptions());
                    }
                }
            }, THREAD_POOL_EXECUTOR);

            //TODO 质量管理部 文件*4
            CompletableFuture<Void> qualityFilesFuture = CompletableFuture.runAsync(() -> {
                QualityOrderDTO qualityOrderDTO = qualityOrderService.getByOrderId(dto.getId(),false);
                if (qualityOrderDTO!=null){
                    User user = userService.getById(qualityOrderDTO.getSubmitId());
                    if (user!=null){
                        dto.setQualityUserName(user.getName());
                    }
                    dto.setQualityCreateTime(qualityOrderDTO.getCreateTime());
                    if (!CollectionUtils.isEmpty(qualityOrderDTO.getFiles()) && qualityOrderDTO.getFiles().size()>0){
                        for (QualityOrderFileDTO file : qualityOrderDTO.getFiles()) {
                            if (file.getType()==1){
                                dto.setQualityOrderFile1(file);
                            }
                            if (file.getType()==2){
                                dto.setQualityOrderFile2(file);
                            }
                            if (file.getType()==3){
                                dto.setQualityOrderFile3(file);
                            }
                            if (file.getType()==4){
                                dto.setQualityOrderFile4(file);
                            }
                        }
                    }
                    if (qualityOrderDTO.getModelCheckLog()!=null){
                        dto.setModelCheckLog(qualityOrderDTO.getModelCheckLog());
                    }
                    if (qualityOrderDTO.getSparePartsLog()!=null){
                        dto.setSparePartsLog(qualityOrderDTO.getSparePartsLog());
                    }
                }else {
                    ModelCheckLogDTO modelCheckLogDTO = modelCheckLogService.getVerify(dto.getNumber());
                    if (modelCheckLogDTO!=null){
                        User user = userService.getById(modelCheckLogDTO.getSubmitId());
                        if (user!=null){
                            dto.setQualityUserName(user.getName());
                        }
                        dto.setQualityCreateTime(modelCheckLogDTO.getCreateTime());
                        dto.setModelCheckLog(modelCheckLogDTO);
                    }
                    SparePartsLogDTO sparePartsLogDTO = sparePartsLogService.getVerify(dto.getNumber());
                    if (sparePartsLogDTO!=null){
                        User user = userService.getById(sparePartsLogDTO.getSubmitId());
                        if (user!=null){
                            dto.setQualityUserName(user.getName());
                        }
                        dto.setQualityCreateTime(sparePartsLogDTO.getCreateTime());
                        dto.setSparePartsLog(sparePartsLogDTO);
                    }

                }
            }, THREAD_POOL_EXECUTOR);

            //TODO 维修工单
            CompletableFuture<Void> afterSaleFuture = CompletableFuture.runAsync(() -> {
                AfterSaleOrderDTO afterSaleOrderDTO = new AfterSaleOrderDTO();
                afterSaleOrderDTO.setNumber(dto.getNumber());
                afterSaleOrderDTO.setVerifyStatus(1);
                afterSaleOrderDTO.setDraft(0);
                List<AfterSaleOrderDTO> afterSaleOrderDTOS = afterSaleOrderService.listByFilter(afterSaleOrderDTO,new Filter());
                if (!CollectionUtils.isEmpty(afterSaleOrderDTOS) && afterSaleOrderDTOS.size()>0){
                    dto.setAfterSaleOrders(afterSaleOrderDTOS);
                    AfterSaleOrderDTO afterSaleOrderDTO1 = afterSaleOrderDTOS.get(0);
                    User user = userService.getById(afterSaleOrderDTO1.getSubmitId());
                    if (user!=null){
                        dto.setAfterUserName(user.getName());
                    }
                    dto.setAfterCreateTime(afterSaleOrderDTO1.getCreateTime());
                }

            }, THREAD_POOL_EXECUTOR);

            //TODO 维修工单
            CompletableFuture<Void> modelInstallFuture = CompletableFuture.runAsync(() -> {
                ModelInstallDTO modelInstallDTO = modelInstallService.getByNumber(dto.getNumber(),0);
                if (modelInstallDTO!=null){
                    dto.setModelInstall(modelInstallDTO);
                    User user = userService.getById(modelInstallDTO.getSubmitId());
                    if (user!=null){
                        dto.setAfterUserName(user.getName());
                    }
                    dto.setAfterCreateTime(modelInstallDTO.getCreateTime());
                }
            }, THREAD_POOL_EXECUTOR);

            CompletableFuture.allOf(deviseFuture,technologyFuture,qualityFilesFuture,afterSaleFuture,modelInstallFuture).join();
        }
        return dto;
    }

    @Override
    public WorkOrderModel changeToModel(WorkOrderDTO dto) {
        WorkOrderModel workOrderModel = new WorkOrderModel();
        WorkOrderModel.DeviseModel deviseModel = new WorkOrderModel.DeviseModel();

        deviseModel.setDeviseOptions(dto.getDeviseOptions());
        deviseModel.setDeviseImg1(dto.getDeviseImg1());
        deviseModel.setDeviseImg2(dto.getDeviseImg2());
        deviseModel.setDevisePdf1(dto.getDevisePdf1());
        deviseModel.setDevisePdf2(dto.getDevisePdf2());
        workOrderModel.setDeviseOrder(deviseModel);

        workOrderModel.setTechnologyOptions(dto.getTechnologyOptions());

        workOrderModel.setAfterSaleOrders(dto.getAfterSaleOrders());

        WorkOrderModel.QualityModel qualityModel = new WorkOrderModel.QualityModel();
        qualityModel.setModelCheckLog(dto.getModelCheckLog());
        qualityModel.setSparePartsLog(dto.getSparePartsLog());
        qualityModel.setQualityOrderFile1(dto.getQualityOrderFile1());
        qualityModel.setQualityOrderFile2(dto.getQualityOrderFile2());
        qualityModel.setQualityOrderFile3(dto.getQualityOrderFile3());
        qualityModel.setQualityOrderFile4(dto.getQualityOrderFile4());
        workOrderModel.setQualityModel(qualityModel);

        workOrderModel.setModelInstall(dto.getModelInstall());

        dto.setDeviseOptions(null);
        dto.setDeviseImg1(null);
        dto.setDeviseImg2(null);
        dto.setTechnologyOptions(null);
        dto.setModelCheckLog(null);
        dto.setSparePartsLog(null);
        dto.setQualityOrderFile1(null);
        dto.setQualityOrderFile2(null);
        dto.setQualityOrderFile3(null);
        dto.setQualityOrderFile4(null);
        dto.setAfterSaleOrders(null);
        dto.setDeptName(DeptTypeModel.TYPE_MAP.get(dto.getDeptId()));

        workOrderModel.setWorkOrderDTO(dto);

        return JSON.parseObject(JSON.toJSONString(workOrderModel, SerializerFeature.DisableCircularReferenceDetect),WorkOrderModel.class);
    }

    @Override
    public Map<String, Object> listNeedOrders(Integer userId, Filter filter) {

        User user = userService.getById(userId);
        if (user==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"用户信息异常");
        }
        DeptRole deptRole = deptRoleService.getByAdd(user.getDeptId(), user.getDeptRoleId());
        if (user.getDeptRoleId()==null || !user.getDeptRoleId().equals(deptRole.getId())){
            user.setDeptRoleId(deptRole.getId());
        }
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`deptId`",user.getDeptId());
        wrapper.eq("verifyStatus",1);
        wrapper.eq("`Status`",0);


        List<String> outOfNumbers = new ArrayList<>();
        if (user.getDeptId().equals(DeptTypeModel.DEPT_DESIGN)){
            DeviseOrderDTO dto = new DeviseOrderDTO();
            dto.setDraft(0);
            dto.setSubmit(1);
            List<DeviseOrder> list = deviseOrderService.listFilter(dto, new Filter());
            if (!CollectionUtils.isEmpty(list) && list.size()>0){
                outOfNumbers.addAll(list.stream().map(DeviseOrder::getNumber).distinct().collect(Collectors.toList()));
            }
        }else if (user.getDeptId().equals(DeptTypeModel.DEPT_TECHNOLOGY)){
            TechonologyOrderDTO dto = new TechonologyOrderDTO();
            dto.setDraft(0);
            dto.setSubmit(1);
            List<TechnologyOrder> list = technologyOrderService.listFilter(dto, new Filter());
            if (!CollectionUtils.isEmpty(list) && list.size()>0){
                outOfNumbers.addAll(list.stream().map(TechnologyOrder::getNumber).distinct().collect(Collectors.toList()));
            }
        } else if (user.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){
            QualityOrderDTO dto= new QualityOrderDTO();
            dto.setDraft(0);
            dto.setSubmit(1);
            List<QualityOrder> orders = qualityOrderService.listFilter(dto,new Filter());
            if (!CollectionUtils.isEmpty(outOfNumbers) && outOfNumbers.size()>0){
                outOfNumbers.addAll(orders.stream().map(QualityOrder::getNumber).distinct().collect(Collectors.toList()));
            }
            SparePartsLogDTO logDTO = new SparePartsLogDTO();
            logDTO.setDraft(0);
            logDTO.setSubmit(1);
            List<SparePartsLog> logs = sparePartsLogService.listFilter(logDTO,new Filter());
            if (!CollectionUtils.isEmpty(logs) && logs.size()>0){
                outOfNumbers.addAll(logs.stream().map(SparePartsLog::getNumber).distinct().collect(Collectors.toList()));
            }

            ModelCheckLogDTO checkLogDTO = new ModelCheckLogDTO();
            checkLogDTO.setDraft(0);
            checkLogDTO.setSubmit(1);
            List<ModelCheckLog> checkLogs = modelCheckLogService.listFilter(checkLogDTO,new Filter());
            if (!CollectionUtils.isEmpty(checkLogs) && checkLogs.size()>0){
                outOfNumbers.addAll(checkLogs.stream().map(ModelCheckLog::getNumber).distinct().collect(Collectors.toList()));
            }

        }else if (user.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            AssembleOrderDTO dto = new AssembleOrderDTO();
            dto.setDraft(0);
            dto.setSubmit(1);
            List<AssembleOrder> list = assembleOrderService.listFilter(dto,new Filter());
            if (!CollectionUtils.isEmpty(list) && list.size()>0 && user.getAssembleTableType()==0){
                outOfNumbers.addAll(list.stream().map(AssembleOrder::getNumber).distinct().collect(Collectors.toList()));
            }else if (user.getAssembleTableType().equals(AssembleTableTypeModel.TYPE_ALLOY)){
                AssembleCheckAlloyPackageDTO packageDTO = new AssembleCheckAlloyPackageDTO();
                packageDTO.setDraft(0);
                packageDTO.setSubmit(1);
                List<AssembleCheckAlloyPackage> alloyPackages = assembleCheckAlloyPackageService.listFilter(packageDTO,new Filter());
                if (!CollectionUtils.isEmpty(alloyPackages) && alloyPackages.size()>0){
                    outOfNumbers.addAll(alloyPackages.stream().map(AssembleCheckAlloyPackage::getNumber).collect(Collectors.toList()));
                }
            }else if (user.getAssembleTableType().equals(AssembleTableTypeModel.TYPE_PACKAGE)){
                AssembleCheckPackageDTO packageDTO = new AssembleCheckPackageDTO();
                packageDTO.setDraft(0);
                packageDTO.setSubmit(1);
                List<AssembleCheckPackage> packages = assembleCheckPackageService.listFilter(packageDTO,new Filter());
                if (!CollectionUtils.isEmpty(packages) && packages.size()>0){
                    outOfNumbers.addAll(packages.stream().map(AssembleCheckPackage::getNumber).collect(Collectors.toList()));
                }
            }else if (user.getAssembleTableType().equals(AssembleTableTypeModel.TYPE_PLANK)){
                AssemblePlankPackageDTO packageDTO = new AssemblePlankPackageDTO();
                packageDTO.setDraft(0);
                packageDTO.setSubmit(1);
                List<AssemblePlankPackage> packages = assemblePlankPackageService.listFilter(packageDTO,new Filter());
                if (!CollectionUtils.isEmpty(packages) && packages.size()>0){
                    outOfNumbers.addAll(packages.stream().map(AssemblePlankPackage::getNumber).collect(Collectors.toList()));
                }
            }else if (user.getAssembleTableType().equals(AssembleTableTypeModel.TYPE_DOWN)){
                AssembleDownPackageDTO packageDTO = new AssembleDownPackageDTO();
                packageDTO.setSubmit(1);
                packageDTO.setDraft(0);
                List<AssembleDownPackage> packages = assembleDownPackageService.listFilter(packageDTO,new Filter());
                if (!CollectionUtils.isEmpty(packages) && packages.size()>0){
                    outOfNumbers.addAll(packages.stream().map(AssembleDownPackage::getNumber).collect(Collectors.toList()));
                }
            }else if (user.getAssembleTableType().equals(AssembleTableTypeModel.TYPE_MODEL_PUSH)){
                AssembleModelPushPackageDTO packageDTO = new AssembleModelPushPackageDTO();
                packageDTO.setSubmit(1);
                packageDTO.setDraft(0);
                List<AssembleModelPushPackage> packages = assembleModelPushPackageService.listFilter(packageDTO,new Filter());
                if (!CollectionUtils.isEmpty(packages) && packages.size()>0){
                    outOfNumbers.addAll(packages.stream().map(AssembleModelPushPackage::getNumber).collect(Collectors.toList()));
                }
            }else if (user.getAssembleTableType().equals(AssembleTableTypeModel.TYPE_MOULE_BASE)){
                MouldBasePackageDTO packageDTO = new MouldBasePackageDTO();
                packageDTO.setSubmit(1);
                packageDTO.setDraft(0);
                List<MouldBasePackage> packages = mouldBasePackageService.listFilter(packageDTO,new Filter());
                if (!CollectionUtils.isEmpty(packages) && packages.size()>0){
                    outOfNumbers.addAll(packages.stream().map(MouldBasePackage::getNumber).collect(Collectors.toList()));
                }
            }
        }

        if (!CollectionUtils.isEmpty(outOfNumbers) && outOfNumbers.size()>0){
            outOfNumbers=outOfNumbers.stream().distinct().collect(Collectors.toList());
            wrapper.notIn("`number`",outOfNumbers);
        }

        return listNeeds(wrapper,filter);
    }

    @Override
    public WorkOrderDTO getDetailByNumber(String number, boolean detail) {
        WorkOrder order = getByNumber(number);
        if (order==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        WorkOrderDTO dto = getDTO(order);

        if (detail){

            //TODO 设计部
            CompletableFuture<Void> deviseFuture = CompletableFuture.runAsync(() -> {
                DeviseOrderDTO deviseOrderDTO = deviseOrderService.getByWorkOrderId(dto.getId(),false);
                if (deviseOrderDTO!=null){
                    dto.setDeviseUserName(deviseOrderDTO.getSubmitName());
                    dto.setDeviseCreateTime(deviseOrderDTO.getCreateTime());
                    dto.setDeviseImg1(deviseOrderDTO.getImg1());
                    dto.setDeviseImg2(deviseOrderDTO.getImg2());
                    dto.setDevisePdf1(deviseOrderDTO.getPdf1());
                    dto.setDevisePdf2(deviseOrderDTO.getPdf2());
                    if (!CollectionUtils.isEmpty(deviseOrderDTO.getSubmitOptions()) && deviseOrderDTO.getSubmitOptions().size()>0){
                        dto.setDeviseOptions(deviseOrderDTO.getSubmitOptions());
                    }
                }
            }, THREAD_POOL_EXECUTOR);

            //TODO 工艺部
            CompletableFuture<Void> technologyFuture = CompletableFuture.runAsync(() -> {
                TechonologyOrderDTO techonologyOrderDTO = technologyOrderService.getByWorkOrderId(dto.getId(),false);
                if (techonologyOrderDTO!=null){
                    User user = userService.getById(techonologyOrderDTO.getSubmitId());
                    if (user!=null){
                        dto.setTecUserName(user.getName());
                    }
                    dto.setTeCreateTime(techonologyOrderDTO.getCreateTime());
                    if (!CollectionUtils.isEmpty(techonologyOrderDTO.getSubmitOptions()) && techonologyOrderDTO.getSubmitOptions().size()>0){
                        dto.setTechnologyOptions(techonologyOrderDTO.getSubmitOptions());
                    }
                }
            }, THREAD_POOL_EXECUTOR);

            //TODO 质量管理部 文件*4
            CompletableFuture<Void> qualityFilesFuture = CompletableFuture.runAsync(() -> {
                QualityOrderDTO qualityOrderDTO = qualityOrderService.getByOrderId(dto.getId(),false);
                if (qualityOrderDTO!=null){
                    User user = userService.getById(qualityOrderDTO.getSubmitId());
                    if (user!=null){
                        dto.setQualityUserName(user.getName());
                    }
                    dto.setQualityCreateTime(qualityOrderDTO.getCreateTime());
                    if (!CollectionUtils.isEmpty(qualityOrderDTO.getFiles()) && qualityOrderDTO.getFiles().size()>0){
                        for (QualityOrderFileDTO file : qualityOrderDTO.getFiles()) {
                            if (file.getType()==1){
                                dto.setQualityOrderFile1(file);
                            }
                            if (file.getType()==2){
                                dto.setQualityOrderFile2(file);
                            }
                            if (file.getType()==3){
                                dto.setQualityOrderFile3(file);
                            }
                            if (file.getType()==4){
                                dto.setQualityOrderFile4(file);
                            }
                        }
                    }
                    if (qualityOrderDTO.getModelCheckLog()!=null){
                        dto.setModelCheckLog(qualityOrderDTO.getModelCheckLog());
                    }
                    if (qualityOrderDTO.getSparePartsLog()!=null){
                        dto.setSparePartsLog(qualityOrderDTO.getSparePartsLog());
                    }
                }else {
                    ModelCheckLogDTO modelCheckLogDTO = modelCheckLogService.getVerify(dto.getNumber());
                    if (modelCheckLogDTO!=null){
                        User user = userService.getById(modelCheckLogDTO.getSubmitId());
                        if (user!=null){
                            dto.setQualityUserName(user.getName());
                        }
                        dto.setQualityCreateTime(modelCheckLogDTO.getCreateTime());
                        dto.setModelCheckLog(modelCheckLogDTO);
                    }
                    SparePartsLogDTO sparePartsLogDTO = sparePartsLogService.getVerify(dto.getNumber());
                    if (sparePartsLogDTO!=null){
                        User user = userService.getById(sparePartsLogDTO.getSubmitId());
                        if (user!=null){
                            dto.setQualityUserName(user.getName());
                        }
                        dto.setQualityCreateTime(sparePartsLogDTO.getCreateTime());
                        dto.setSparePartsLog(sparePartsLogDTO);
                    }

                }
            }, THREAD_POOL_EXECUTOR);

            //TODO 维修工单
            CompletableFuture<Void> afterSaleFuture = CompletableFuture.runAsync(() -> {
                AfterSaleOrderDTO afterSaleOrderDTO = new AfterSaleOrderDTO();
                afterSaleOrderDTO.setNumber(dto.getNumber());
                afterSaleOrderDTO.setVerifyStatus(1);
                afterSaleOrderDTO.setDraft(0);
                List<AfterSaleOrderDTO> afterSaleOrderDTOS = afterSaleOrderService.listByFilter(afterSaleOrderDTO,new Filter());
                if (!CollectionUtils.isEmpty(afterSaleOrderDTOS) && afterSaleOrderDTOS.size()>0){
                    dto.setAfterSaleOrders(afterSaleOrderDTOS);
                    AfterSaleOrderDTO afterSaleOrderDTO1 = afterSaleOrderDTOS.get(0);
                    User user = userService.getById(afterSaleOrderDTO1.getSubmitId());
                    if (user!=null){
                        dto.setAfterUserName(user.getName());
                    }
                    dto.setAfterCreateTime(afterSaleOrderDTO1.getCreateTime());
                }

            }, THREAD_POOL_EXECUTOR);

            //TODO 维修工单
            CompletableFuture<Void> modelInstallFuture = CompletableFuture.runAsync(() -> {
                ModelInstallDTO modelInstallDTO = modelInstallService.getByNumber(dto.getNumber(),0);
                if (modelInstallDTO!=null){
                    dto.setModelInstall(modelInstallDTO);
                    User user = userService.getById(modelInstallDTO.getSubmitId());
                    if (user!=null){
                        dto.setAfterUserName(user.getName());
                    }
                    dto.setAfterCreateTime(modelInstallDTO.getCreateTime());
                }
            }, THREAD_POOL_EXECUTOR);

            CompletableFuture.allOf(deviseFuture,technologyFuture,qualityFilesFuture,afterSaleFuture,modelInstallFuture).join();
        }
        return dto;
    }

    @Override
    public HomeModel getHomeModel() {
        HomeModel model = new HomeModel();

        //已完成数量
        CompletableFuture<Void> finishFuture = CompletableFuture.runAsync(() -> {
            QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
            wrapper.eq("`Status`", 1);
            Integer count = baseMapper.selectCount(wrapper);
            model.setFinishCount(BigInteger.valueOf(count));
        });

        //生产中数量
        CompletableFuture<Void> unFinishFuture = CompletableFuture.runAsync(() -> {
            QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
            wrapper.eq("`Status`", 0);
            Integer count = baseMapper.selectCount(wrapper);
            model.setFinishCount(BigInteger.valueOf(count));
        });

        //TODO

        return model;
    }

    @Override
    public void checkFinish(UserTableProjectDTO dto) {
        if (dto.getDeptId().equals(DeptTypeModel.DEPT_DESIGN)){
            DeviseOrder deviseOrder = deviseOrderService.getByNumber(dto.getNumber());
            if (deviseOrder!=null){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单已提交");
            }
        }else if (dto.getDeptId().equals(DeptTypeModel.DEPT_TECHNOLOGY)){
            TechnologyOrder technologyOrder = technologyOrderService.getByNumber(dto.getNumber());
            if (technologyOrder!=null){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单已提交");
            }
        }else if (dto.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){
            if (dto.getTableType().equals(TableTypeModel.TABLE_ONE)){
                QualityOrder qualityOrder = qualityOrderService.getByNumber(dto.getNumber());
                if (qualityOrder!=null){
                    throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单已提交");
                }
            }else if (dto.getTableType().equals(TableTypeModel.TABLE_TWO)){
                ModelCheckLog checkLog = modelCheckLogService.getByNumber(dto.getNumber());
                if (checkLog!=null){
                    throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单已提交");
                }
            }else if (dto.getTableType().equals(TableTypeModel.TABLE_THREE)){
                SparePartsLog checkLog = sparePartsLogService.getByNumber(dto.getNumber());
                if (checkLog!=null){
                    throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单已提交");
                }
            }
        }else if (dto.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            if (dto.getTableType().equals(AssembleTableTypeModel.TYPE_ALLOY)){
                AssembleCheckAlloyPackage alloyPackage = assembleCheckAlloyPackageService.getByNumber(dto.getNumber());
                if (alloyPackage!=null){
                    throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单已提交");
                }
            }else if (dto.getTableType().equals(AssembleTableTypeModel.TYPE_PACKAGE)){
                AssembleCheckPackage alloyPackage = assembleCheckPackageService.getByNumber(dto.getNumber());
                if (alloyPackage!=null){
                    throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单已提交");
                }
            }else if (dto.getTableType().equals(AssembleTableTypeModel.TYPE_PLANK)){
                AssemblePlankPackage alloyPackage = assemblePlankPackageService.getByNumber(dto.getNumber());
                if (alloyPackage!=null){
                    throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单已提交");
                }
            }else if (dto.getTableType().equals(AssembleTableTypeModel.TYPE_DOWN)){
                AssembleDownPackage alloyPackage = assembleDownPackageService.getByNumber(dto.getNumber());
                if (alloyPackage!=null){
                    throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单已提交");
                }
            }else if (dto.getTableType().equals(AssembleTableTypeModel.TYPE_MODEL_PUSH)){
                AssembleModelPushPackage alloyPackage = assembleModelPushPackageService.getByNumber(dto.getNumber());
                if (alloyPackage!=null){
                    throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单已提交");
                }
            }else if (dto.getTableType().equals(AssembleTableTypeModel.TYPE_MOULE_BASE)){
                MouldBasePackage alloyPackage = mouldBasePackageService.getByNumber(dto.getNumber());
                if (alloyPackage!=null){
                    throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单已提交");
                }
            }
        }
    }

    @Override
    public Map<String, Object> newListNeedOrders(Integer userId,Integer tableType, Filter filter) {

        User user = userService.getById(userId);
        if (user==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        UserTableProjectDTO dto = new UserTableProjectDTO();
        dto.setUserId(userId);
        dto.setTableType(tableType);
        dto.setDeptId(user.getDeptId());
        dto.setStatus(0);
        List<UserTableProjectDTO> dtos = userTableProjectService.listByFilter(dto, filter);
        BigInteger count = userTableProjectService.listCount(dto, filter);

        if (!CollectionUtils.isEmpty(dtos) && dtos.size()>0){
            Map<String,Object> res = new HashMap<>();

            List<NeedOrderModel> models = dtos.stream().map(tableProjectDTO -> {
                NeedOrderModel model = new NeedOrderModel();
                model.setNumber(tableProjectDTO.getNumber());
                model.setCreateTime(tableProjectDTO.getCreateTime());
                model.setCreateName(tableProjectDTO.getSubmitName());
                return model;
            }).distinct().collect(Collectors.toList());

            res.put("list",models);
            res.put("count",count);
            return res;
        }
        return null;
    }


    @Transactional
    @Override
    public WorkOrder newLinkChange(Integer userId, String number, Integer status, String remark, Integer toUserId) {
        User user = userService.getById(userId);
        if (user==null){
            throw new BusinessException(ResException.QUERY_MISS.getCode(),"用户信息异常");
        }
        DeptRole role = deptRoleService.getByAdd(user.getDeptId(), user.getDeptRoleId());
        if (role.getLinkChangePermission()==0){
            throw new BusinessException(ResException.USER_PER_MISS);
        }

        WorkOrder workOrder = getByNumber(number);
        if (workOrder==null){
            throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
        }
        if (workOrder.getDraft()==1){
            throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
        }
        if (!workOrder.getDeptId().equals(user.getDeptId())){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前环节无操作权限");
        }
        if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_SALE)){//营销部
            workOrder.setDeptId(DeptTypeModel.DEPT_DESIGN);
            workOrder.setSaleStatus(status);
            workOrder.setSaleRemark(remark);

/*            if (toUserId==null){
                toUserId = userService.getMaxDeptUserId(DeptTypeModel.DEPT_DESIGN);
            }

            User toUser = userService.getById(toUserId);
            if (toUser==null || toUser.getStatus()==1){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"推送人信息异常");
            }
            if (!toUser.getDeptId().equals(DeptTypeModel.DEPT_DESIGN)){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"必须推送至"+DeptTypeModel.TYPE_MAP.get(toUser.getDeptId()));
            }
            DeptRole deptRole = deptRoleService.getByAdd(toUser.getDeptId(), toUser.getDeptRoleId());
            if (deptRole.getLinkChangePermission()==0 || deptRole.getCreateAllOrderPermission()==0 || deptRole.getVerifyPermission()==0 || deptRole.getDistributionPermission()==0){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"该用户权限不足");
            }
            UserTableProject userTableProject = new UserTableProject();
            userTableProject.setUserId(toUserId);
            userTableProject.setSubmitId(userId);
            userTableProject.setDeptId(toUser.getDeptId());
            userTableProject.setNumber(number);
            userTableProject.setTableType(TableTypeModel.TABLE_ONE);
            userTableProject.setRemark(remark);
            userTableProjectService.save(userTableProject);*/

        }else if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_DESIGN)){//设计部
            DeviseOrder deviseOrder = deviseOrderService.getByNumber(number);
            if (deviseOrder==null){
                throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
            }
            workOrder.setDeptId(DeptTypeModel.DEPT_TECHNOLOGY);
            deviseOrder.setStatus(status);
            deviseOrderService.updateById(deviseOrder);
        }else if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_TECHNOLOGY)){//工艺科
            TechnologyOrder technologyOrder = technologyOrderService.getByNumber(number);
            if (technologyOrder==null){
                throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
            }
            workOrder.setDeptId(DeptTypeModel.DEPT_QUALITY);
            technologyOrder.setStatus(status);
            technologyOrderService.updateById(technologyOrder);
        }else if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){//质量管理科
            QualityOrder qualityOrder = qualityOrderService.getByNumber(number);
            if (qualityOrder==null){
                throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");
            }
            workOrder.setDeptId(DeptTypeModel.DEPT_WORK_ASSEMBLE);
            qualityOrder.setStatus(status);
            qualityOrderService.updateById(qualityOrder);

            AssembleOrder assembleOrder = new AssembleOrder();
            assembleOrder.setOrderID(workOrder.getId());
            assembleOrder.setNumber(number);
            assembleOrder.setStatus(0);
            assembleOrder.setDraft(0);
            assembleOrder.setVerifyStatus(1);
            assembleOrder.setVerifyTime(LocalDateTime.now());
            assembleOrder.setStatus(0);
            assembleOrderService.save(assembleOrder);

        }else if (workOrder.getDeptId().equals(DeptTypeModel.DEPT_VERIFY)){//装配车间
            AssembleOrder assembleOrder = assembleOrderService.getByNumber(number);
            if (assembleOrder==null){
                assembleOrder = new AssembleOrder();
                assembleOrder.setOrderID(workOrder.getId());
                assembleOrder.setNumber(number);
                assembleOrder.setStatus(1);
                assembleOrder.setDraft(0);
                assembleOrder.setSubmitId(userId);
                assembleOrder.setVerifyId(userId);
                assembleOrder.setVerifyStatus(1);
                assembleOrder.setVerifyTime(LocalDateTime.now());
                /*                throw new BusinessException(ResException.QUERY_MISS.getCode(),"工单信息有误");*/
            }
            workOrder.setDeptId(DeptTypeModel.DEPT_VERIFY);
            workOrder.setStatus(1);
            assembleOrder.setStatus(status);
            assembleOrderService.updateById(assembleOrder);
        }
        workOrder.setUpdateTime(LocalDateTime.now());
        updateById(workOrder);
        return workOrder;
    }

    @Override
    public List<TableTypeDTO> queryTableTypes(Integer deptId) {
        Map<Integer, String> map = TableTypeModel.TYPE_MAP.get(deptId);
        List<TableTypeDTO> list = new ArrayList<>();

        if (deptId.equals(DeptTypeModel.DEPT_QUALITY)){
            TableTypeDTO dto = new TableTypeDTO();
            dto.setTableType(TableTypeModel.TABLE_ONE);
            dto.setTableName("质量管理部工单");
            list.add(dto);
            TableTypeDTO dto1 = new TableTypeDTO();
            dto1.setTableType(TableTypeModel.TABLE_THREE);
            dto1.setTableName("零件检测报告单");
            list.add(dto1);
            TableTypeDTO dto2 = new TableTypeDTO();
            dto2.setTableType(TableTypeModel.TABLE_TWO);
            dto2.setTableName("模具检测报告单");
            list.add(dto2);
        }else {
            for (Integer key : map.keySet()) {
                TableTypeDTO dto = new TableTypeDTO();
                dto.setTableType(key);
                dto.setTableName(map.get(key));
                list.add(dto);
            }
        }

        return list;
    }

    @Override
    public Map<String, Object> listDistributionProjects(Integer tableType, Filter filter, Integer deptId) {
        //TODO
        WorkOrderDTO workOrderDTO = new WorkOrderDTO();
        workOrderDTO.setVerifyStatus(1);
        workOrderDTO.setTableType(tableType);
        workOrderDTO.setTableTypeStatus(0);
        workOrderDTO.setDeptId(deptId);
        List<WorkOrderDTO> dtos = listByFilter(workOrderDTO, filter);
        BigInteger count = listCount(workOrderDTO, filter);

        List<DistributionProjectDTO> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dtos) && dtos.size()>0){
            list = dtos.stream().map(dto->{
                DistributionProjectDTO projectDTO = new DistributionProjectDTO();
                projectDTO.setNumber(dto.getNumber());
                projectDTO.setTableType(tableType);
                projectDTO.setTableName(TableTypeModel.TYPE_MAP.get(deptId).get(tableType));
                projectDTO.setCreateTime(dto.getCreateTime());
                projectDTO.setCreateUserName(dto.getSaleName());
                return projectDTO;
            }).collect(Collectors.toList());
        }

        Map<String, Object> res = new HashMap<>();
        res.put("list",list);
        res.put("count",count);
        return res;
    }


    private Map<String, Object> listNeeds(QueryWrapper<WorkOrder> wrapper, Filter filter) {
        Map<String,Object> res = new HashMap<>();
        List<WorkOrder> list = new ArrayList<>();
        Integer count = 0;
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
            count = baseMapper.selectCount(wrapper);
            if (filter.getPage()!=null && filter.getPageSize()!=null && filter.getPage()!=0 && filter.getPageSize()!=0){
                int fast = filter.getPage()<=1?0:(filter.getPage()-1)*filter.getPageSize();
                wrapper.last(" limit "+fast+", "+filter.getPageSize());
            }
            list = list(wrapper);
        }

        List<User> users = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
            List<Integer> userIds = list.stream().map(WorkOrder::getSubmitId).distinct().collect(Collectors.toList());
            users = (List<User>) userService.listByIds(userIds);
        }

        List<User> finalUsers = users;
        List<NeedOrderModel> models = list.stream().map(workOrder -> {
            NeedOrderModel model = new NeedOrderModel();
            model.setNumber(workOrder.getNumber());
            model.setCreateTime(workOrder.getCreateTime());
            if (!CollectionUtils.isEmpty(finalUsers) && finalUsers.size() > 0) {
                for (User user : finalUsers) {
                    if (user.getId().equals(workOrder.getSubmitId())) {
                        model.setCreateName(user.getName());
                    }
                }
            }
            return model;
        }).distinct().collect(Collectors.toList());

        res.put("list",models);
        res.put("count",BigInteger.valueOf(count));
        return res;
    }

    private List<NeedOrderModel> changeToNeeds(List<WorkOrder> list) {
        List<User> users = (List<User>) userService.listByIds(list.stream().map(WorkOrder::getSubmitId).distinct().collect(Collectors.toList()));
        return list.stream().map(workOrder -> {
            NeedOrderModel model = new NeedOrderModel();
            model.setNumber(workOrder.getNumber());
            model.setCreateTime(workOrder.getCreateTime());
            if (!CollectionUtils.isEmpty(users) && users.size() > 0) {
                for (User user : users) {
                    if (user.getId().equals(workOrder.getSubmitId())) {
                        model.setCreateName(user.getName());
                    }
                }
            }
            return model;
        }).distinct().collect(Collectors.toList());
    }

    private WorkOrderDTO getDTO(WorkOrder order) {
       WorkOrderDTO dto = BeanUtils.copyAs(order, WorkOrderDTO.class);

        Custom custom = customService.getById(order.getCustomId());
        if (custom!=null){
            dto.setCustomName(custom.getName());
        }

        if (order.getSubmitId()!=null){
            User user = userService.getById(order.getSubmitId());
            if (user!=null){
                dto.setSubmitName(user.getName());
            }
        }

        if (dto.getModelTypeId()!=null) {
            Option modeleType = optionService.getById(dto.getModelTypeId());
            if (modeleType != null) {
                dto.setModelTypeName(modeleType.getTitle());
            }
        }

        if (order.getElectricTypeId()!=null) {
            Option electric = optionService.getById(order.getElectricTypeId());
            if (electric != null) {
                dto.setElectricName(electric.getTitle());
            }
        }

        if (dto.getSaleId()!=null) {
            User saleUser = userService.getById(dto.getSaleId());
            if (saleUser != null) {
                dto.setSaleName(saleUser.getName());
            }
        }

        if (order.getCount()!=null) {
            Option count = optionService.getById(order.getCount());
            if (count != null) {
                dto.setCountStr(count.getTitle());
            }
        }

        return dto;
    }

}
