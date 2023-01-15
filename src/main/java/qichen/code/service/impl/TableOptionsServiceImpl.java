package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.TableOptions;
import qichen.code.entity.dto.TableOptionDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.TableOptionsMapper;
import qichen.code.model.Filter;
import qichen.code.service.ISubmitTableOptionsService;
import qichen.code.service.ITableOptionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 表单选项表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-11
 */
@Service
public class TableOptionsServiceImpl extends ServiceImpl<TableOptionsMapper, TableOptions> implements ITableOptionsService {

    @Autowired
    private ISubmitTableOptionsService submitTableOptionsService;


    @Transactional
    @Override
    public List<TableOptions> addBatch(List<TableOptionDTO> tableOptions) {
        for (TableOptionDTO tableOption : tableOptions) {
            Map<String,String> params = new HashMap<>();
            params.put("title","标题");
            params.put("type","选项类型");
            params.put("tableType","表单类型");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(tableOption)));
            checkAlready(tableOption);
        }
        List<TableOptions> options = BeanUtils.copyAs(tableOptions, TableOptions.class);
        saveBatch(options);
        return options;
    }

    @Transactional
    @Override
    public TableOptions delete(Integer id) {
        TableOptions options = getById(id);
        if (options==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        submitTableOptionsService.removeByOptionId(id);
        removeById(id);
        return options;
    }

    @Override
    public TableOptions adminUpdate(TableOptionDTO dto) {
        TableOptions options = getById(dto.getId());
        if (options==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }

        if (!StringUtils.isEmpty(dto.getTitle()) && dto.getTitle().length()>0){
            if (dto.getTableType()==null){
                dto.setTableType(options.getTableType());
            }
            checkAlready(dto);
        }

        TableOptions res = BeanUtils.copyAs(dto, TableOptions.class);
        updateById(res);
        return res;
    }

    @Override
    public List<TableOptionDTO> listByFilter(TableOptionDTO dto, Filter filter) {
        List<TableOptions> list = listFilter(dto,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    private List<TableOptions> listFilter(TableOptionDTO dto, Filter filter) {
        QueryWrapper<TableOptions> wrapper = new QueryWrapper<>();
        addFitler(wrapper,dto,filter);
        return list(wrapper);
    }

    private void addFitler(QueryWrapper<TableOptions> wrapper, TableOptionDTO dto, Filter filter) {
        if (dto!=null){
            if (dto.getType()!=null){
                wrapper.eq("`type`",dto.getType());
            }
            if (dto.getTableType()!=null){
                wrapper.eq("tableType",dto.getTableType());
            }
            if (dto.getStatus()!=null){
                wrapper.eq("`Status`",dto.getStatus());
            }
            if (dto.getLevel()!=null){
                wrapper.eq("`level`",dto.getLevel());
            }
            if (dto.getMust()!=null){
                wrapper.eq("`must`",dto.getMust());
            }
            if (!CollectionUtils.isEmpty(dto.getTableTypes()) && dto.getTableTypes().size()>0){
                wrapper.in("tableType",dto.getTableTypes());
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
                wrapper.like("`title`",filter.getKeyword());
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

    private List<TableOptionDTO> listDTO(List<TableOptions> list) {
        List<TableOptionDTO> dtos = BeanUtils.copyAs(list, TableOptionDTO.class);
        //TODO
        return dtos;
    }

    @Override
    public BigInteger listCount(TableOptionDTO dto, Filter filter) {
        QueryWrapper<TableOptions> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public TableOptionDTO getDetail(Integer id) {
        TableOptions options = getById(id);
        if (options==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(options);
    }

    private TableOptionDTO getDTO(TableOptions options) {
        TableOptionDTO dto = BeanUtils.copyAs(options, TableOptionDTO.class);
        //TODO
        return dto;
    }

    private void checkAlready(TableOptionDTO tableOption) {
        QueryWrapper<TableOptions> wrapper = new QueryWrapper<>();
        wrapper.eq("`title`",tableOption.getTitle());
        if (tableOption.getId()!=null){
            wrapper.ne("ID",tableOption.getId());
        }
        if (tableOption.getTableType()!=null){
            wrapper.eq("tableType",tableOption.getTableType());
        }
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"名称重复");
        }
    }
}
