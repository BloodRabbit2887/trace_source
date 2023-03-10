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
import qichen.code.mapper.QualityOrderMapper;
import qichen.code.model.DeptTypeModel;
import qichen.code.model.Filter;
import qichen.code.model.TableTypeModel;
import qichen.code.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 质量管理部工单表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Service
public class QualityOrderServiceImpl extends ServiceImpl<QualityOrderMapper, QualityOrder> implements IQualityOrderService {

    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private IQualityOrderFileService qualityOrderFileService;
    @Autowired
    private IModelCheckLogService modelCheckLogService;
    @Autowired
    private ISparePartsLogService sparePartsLogService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IAdminService adminService;
    @Autowired
    private IUserTableProjectService userTableProjectService;

    @Transactional
    @Override
    public QualityOrder createWorkOrder(QualityOrderDTO dto) {

/*        checkDraft(dto);*/
        if (dto.getId()==null){
            removeDraft(dto);
        }

        checkAlready(dto.getNumber());//同时只存在一张工单

        WorkOrder workOrder = workOrderService.getUnFinish(dto.getNumber());
        if (workOrder==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前质管部无待完成工单");
        }
/*        if (!workOrder.getDeptId().equals(DeptTypeModel.DEPT_QUALITY)){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前质管部无待完成工单");
        }*/
        dto.setOrderID(workOrder.getId());
        //空值检验
        if (dto.getDraft()==null || dto.getDraft()==0){
            Map<String,String> params = new HashMap<>();
            params.put("title","模具名称");
            params.put("number","模号");
            params.put("submitId","创建人");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));
            checkFiles(dto);
            userTableProjectService.updateStatus(dto.getNumber(),dto.getSubmitId(),1, TableTypeModel.TABLE_ONE);
        }

        QualityOrder qualityOrder = BeanUtils.copyAs(dto, QualityOrder.class);
        saveOrUpdate(qualityOrder);
        dto.setId(qualityOrder.getId());

        if (!CollectionUtils.isEmpty(dto.getFiles()) && dto.getFiles().size()>0){
            for (QualityOrderFileDTO file : dto.getFiles()) {
                file.setQualityOrderID(qualityOrder.getId());
                file.setSubmitId(qualityOrder.getSubmitId());
                qualityOrderFileService.freshFile(file);
            }
            qualityOrderFileService.freshFileVersion(dto.getId());
        }

        //TODO  正式删除
/*        workOrder.setDeptId(DeptTypeModel.DEPT_QUALITY);
        workOrderService.updateById(workOrder);*/

        return qualityOrder;
    }

    private void removeDraft(QualityOrderDTO dto) {
        UpdateWrapper<QualityOrder> wrapper = new UpdateWrapper<>();
        wrapper.eq("submitId",dto.getSubmitId());
        wrapper.eq("draft",1);
        wrapper.eq("`number`",dto.getNumber());
        remove(wrapper);
    }

    private void checkDraft(QualityOrderDTO dto) {
        QueryWrapper<QualityOrder> wrapper = new QueryWrapper<>();
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
    public QualityOrder getByNumber(String number) {
        QueryWrapper<QualityOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("`draft`",0);
        wrapper.ne("`verifyStatus`",2);
        return getOne(wrapper);
    }

    @Override
    public void removeByNumber(String number, Integer otherId) {
        UpdateWrapper<QualityOrder> wrapper = new UpdateWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.ne("`ID`",otherId);
        remove(wrapper);
    }

    @Override
    public QualityOrderDTO getWorkOrderModel(Integer userId, String number) {
        QualityOrderDTO dto = new QualityOrderDTO();
        WorkOrder workOrder = workOrderService.getUnFinish(number);
        if (workOrder==null){
            return null;
        }
        QualityOrder order = getDraft(userId);
        if (order!=null){
            dto = BeanUtils.copyAs(order,QualityOrderDTO.class);
        }
        dto.setNumber(number);

        if (dto.getId()!=null){
            QualityOrderFileDTO fileDTO = new QualityOrderFileDTO();
            fileDTO.setQualityOrderID(dto.getId());
            fileDTO.setNewVersion(1);
            List<QualityOrderFileDTO> files = qualityOrderFileService.listByFilter(fileDTO,new Filter());
            if (!CollectionUtils.isEmpty(files) && files.size()>0){
                dto.setFiles(files);
            }
        }

        return dto;

    }

    @Override
    public QualityOrderDTO getByOrderId(Integer workerOrderId, boolean draft) {
        QueryWrapper<QualityOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`orderID`",workerOrderId);
        wrapper.eq("`draft`",draft?1:0);
        if (!draft){
            wrapper.eq("`verifyStatus`",1);
        }
        QualityOrder qualityOrder = getOne(wrapper);
        if (qualityOrder==null){
            return null;
        }
        return getDTO(qualityOrder);
    }

    @Override
    public List<QualityOrder> listFilter(QualityOrderDTO qualityOrderDTO, Filter filter) {
        QueryWrapper<QualityOrder> wrapper = new QueryWrapper<>();
        addFilter(wrapper,qualityOrderDTO,filter);
        return list(wrapper);
    }

    @Override
    public List<QualityOrderDTO> listByFilter(QualityOrderDTO dto, Filter filter) {
        List<QualityOrder> list = listFilter(dto,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    private List<QualityOrderDTO> listDTO(List<QualityOrder> list) {
        List<QualityOrderDTO> dtos = BeanUtils.copyAs(list, QualityOrderDTO.class);


        List<User> users = (List<User>) userService.listByIds(list.stream().map(QualityOrder::getSubmitId).distinct().collect(Collectors.toList()));
        List<Admin> admins = new ArrayList<>();
        List<QualityOrder> verifys = list.stream().filter(checkPackage -> checkPackage.getVerifyId() != null).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(verifys) && verifys.size()>0){
            admins = (List<Admin>) adminService.listByIds(verifys.stream().map(QualityOrder::getVerifyId).distinct().collect(Collectors.toList()));
        }

        for (QualityOrderDTO dto : dtos) {
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
    public BigInteger listCount(QualityOrderDTO dto, Filter filter) {
        QueryWrapper<QualityOrder> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFilter(wrapper,dto,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public QualityOrderDTO getDetail(Integer id) {
        QualityOrder order = getById(id);
        if (order==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(order);
    }

    private void addFilter(QueryWrapper<QualityOrder> wrapper, QualityOrderDTO qualityOrderDTO, Filter filter) {
        if (qualityOrderDTO!=null){
            if (qualityOrderDTO.getDraft()!=null){
                wrapper.eq("`draft`",qualityOrderDTO.getDraft());
            }
            if (qualityOrderDTO.getVerifyStatus()!=null){
                wrapper.eq("verifyStatus",qualityOrderDTO.getVerifyStatus());
            }
            if (qualityOrderDTO.getSubmitId()!=null){
                wrapper.eq("submitId",qualityOrderDTO.getSubmitId());
            }
            if (!StringUtils.isEmpty(qualityOrderDTO.getNumber()) && qualityOrderDTO.getNumber().length()>0){
                wrapper.eq("number",qualityOrderDTO.getNumber());
            }
            if (qualityOrderDTO.getSubmit()!=null){
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

    private QualityOrderDTO getDTO(QualityOrder qualityOrder) {
        QualityOrderDTO dto = BeanUtils.copyAs(qualityOrder, QualityOrderDTO.class);

        QualityOrderFileDTO fileDTO = new QualityOrderFileDTO();
        fileDTO.setQualityOrderID(qualityOrder.getId());
        fileDTO.setNewVersion(1);
        List<QualityOrderFileDTO> fileDTOS = qualityOrderFileService.listByFilter(fileDTO, null);
        if (!CollectionUtils.isEmpty(fileDTOS) && fileDTOS.size()>0){
            dto.setFiles(fileDTOS);
        }

        ModelCheckLogDTO modelCheckLogDTO = modelCheckLogService.getVerify(qualityOrder.getNumber());
        if (modelCheckLogDTO!=null){
            dto.setModelCheckLog(modelCheckLogDTO);
        }

        SparePartsLogDTO sparePartsLogDTO = sparePartsLogService.getVerify(qualityOrder.getNumber());
        if (sparePartsLogDTO!=null){
            dto.setSparePartsLog(sparePartsLogDTO);
        }

        return dto;
    }

    private QualityOrder getDraft(Integer userId) {
        QueryWrapper<QualityOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",userId);
        wrapper.eq("`draft`",1);
        return getOne(wrapper);
    }


    private void checkFiles(QualityOrderDTO dto) {
        if (dto.getId()==null){
            List<QualityOrderFileDTO> files = dto.getFiles();
            if (CollectionUtils.isEmpty(files) || files.size()<2){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"请上传必要文件");
            }
            List<QualityOrderFileDTO> downModelFiles = files.stream().filter(fileDTO -> fileDTO.getType() == 1).distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(downModelFiles) || downModelFiles.size()!=1){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"请上传下模配磨检查记录文件");
            }

            List<QualityOrderFileDTO> mateModelFiles = files.stream().filter(fileDTO -> fileDTO.getType() == 2).distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(mateModelFiles) || mateModelFiles.size()!=1){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"请上传配磨导正钉检查记录文件");
            }
        }
    }

    private void checkAlready(String number) {
        QueryWrapper<QualityOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("`draft`",0);
        wrapper.eq("`verifyStatus`",1);
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前模号存在已审核工单");
        }
    }
}
