package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.Admin;
import qichen.code.entity.OperationLog;
import qichen.code.entity.dto.OperationLogDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.OperationLogMapper;
import qichen.code.model.Filter;
import qichen.code.service.IAdminService;
import qichen.code.service.IOperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 操作记录表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {

    @Autowired
    private IAdminService adminService;

    @Override
    public void saveOperationLog(Integer objectType, Integer objectId, String operation, String remark,
                                 String associationTable, Integer associationId, String dataStr) {
        OperationLog operationLog = new OperationLog();
        operationLog.setObjectType(objectType);
        operationLog.setObjectID(objectId);
        operationLog.setOperation(operation);
        operationLog.setRemark(remark);
        operationLog.setCreateTime(LocalDateTime.now());
        operationLog.setAssociationTable(associationTable);
        operationLog.setAssociationID(associationId);
        operationLog.setDataStr(dataStr);
        this.save(operationLog);
    }

    @Override
    public List<OperationLogDTO> listByFilter(OperationLogDTO dto, Filter filter) {
        List<OperationLog> list = listFilter(dto, filter);
        if (!CollectionUtils.isEmpty(list) && list.size() > 0) {
            return listDTO(list);
        }
        return null;
    }

    private List<OperationLogDTO> listDTO(List<OperationLog> list) {
        List<OperationLogDTO> dtos = BeanUtils.copyAs(list, OperationLogDTO.class);

        List<Admin> admins = (List<Admin>) adminService.listByIds(list.stream().map(OperationLog::getObjectID).distinct().collect(Collectors.toList()));
        for (OperationLogDTO dto : dtos) {
            if (!CollectionUtils.isEmpty(admins) && admins.size() > 0) {
                for (Admin admin : admins) {
                    if (dto.getObjectID().equals(admin.getId())) {
                        dto.setAdminName(admin.getAdminName());
                        dto.setAdminNO(admin.getAdminNO());
                    }
                }
            }
        }

        return dtos;
    }

    private List<OperationLog> listFilter(OperationLogDTO dto, Filter filter) {
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        addFilter(wrapper, dto, filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<OperationLog> wrapper, OperationLogDTO dto, Filter filter) {
        if (dto != null) {
            if (dto.getObjectID() != null) {
                wrapper.eq("ObjectID", dto.getObjectID());
            }
            if (dto.getObjectType() != null) {
                wrapper.eq("ObjectType", dto.getObjectType());
            }
            if (!StringUtils.isEmpty(dto.getOperation()) && dto.getOperation().length() > 0) {
                wrapper.eq("Operation", dto.getOperation());
            }
            if (!StringUtils.isEmpty(dto.getAssociationTable()) && dto.getAssociationTable().length() > 0) {
                wrapper.eq("AssociationTable", dto.getAssociationTable());
            }
        }
        if (filter != null) {
            if (filter.getCreateTimeBegin() != null) {
                wrapper.ge("CreateTime", filter.getCreateTimeBegin());
            }
            if (filter.getCreateTimeEnd() != null) {
                wrapper.le("CreateTime", filter.getCreateTimeEnd());
            }
            if (!StringUtils.isEmpty(filter.getKeyword()) && filter.getKeyword().length() > 0) {
                wrapper.and(queryWrapper -> queryWrapper.like("Remark", filter.getKeyword()).or().like("AssociationTable", filter.getKeyword()));
            }
            if (!StringUtils.isEmpty(filter.getOrders()) && filter.getOrders().length() > 0) {
                if (filter.getOrderBy() != null) {
                    wrapper.orderBy(true, filter.getOrderBy(), filter.getOrders());
                }
            }
            if (filter.getPage() != null && filter.getPageSize() != null && filter.getPage() != 0 && filter.getPageSize() != 0) {
                int fast = filter.getPage() <= 1 ? 0 : (filter.getPage() - 1) * filter.getPageSize();
                wrapper.last(" limit " + fast + ", " + filter.getPageSize());
            }
        }
    }

    @Override
    public BigInteger listCount(OperationLogDTO dto, Filter filter) {
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        if (filter != null) {
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFilter(wrapper, dto, filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public OperationLogDTO getDetail(Integer id) {
        OperationLog operationLog = getById(id);
        if (operationLog == null) {
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(operationLog);
    }

    private OperationLogDTO getDTO(OperationLog operationLog) {
        OperationLogDTO dto = BeanUtils.copyAs(operationLog, OperationLogDTO.class);

        Admin admin = adminService.getById(operationLog.getObjectID());
        if (admin != null) {
            dto.setAdminName(admin.getAdminName());
            dto.setAdminNO(admin.getAdminNO());
        }
        return dto;
    }

}
