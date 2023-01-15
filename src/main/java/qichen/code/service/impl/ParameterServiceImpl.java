package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.Parameter;
import qichen.code.entity.dto.ParameterDTO;
import qichen.code.entity.dto.ParameterFilterDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.ParameterMapper;
import qichen.code.model.Filter;
import qichen.code.service.IParameterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.ContextUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 参数表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
@Service
public class ParameterServiceImpl extends ServiceImpl<ParameterMapper, Parameter> implements IParameterService {

    @Override
    public List<ParameterDTO> listRef(Byte status, Byte def) {
        QueryWrapper<Parameter> wrapper = new QueryWrapper<>();
        wrapper.eq("Status",status);
        wrapper.eq("DelTF",def);
        return BeanUtils.copyAs(this.list(wrapper),ParameterDTO.class);
    }

    @Override
    public void delete(ParameterDTO parameterDTO) {
        if(null != parameterDTO) {
            deleteInternal(parameterDTO);
        }
    }

    @Override
    public String getText(Integer type) {
        String text = "";
        switch (type){
            case 1:
                text = methodGetParameterByParamName("private.text", null, Byte.valueOf("1")).getParamValue();
                break;
            case 2:
                text = methodGetParameterByParamName("vip.text", null, Byte.valueOf("1")).getParamValue();
                break;
            case 3:
                text = methodGetParameterByParamName("user.text", null, Byte.valueOf("1")).getParamValue();
                break;
            case 4:
                text = methodGetParameterByParamName("player.text", null, Byte.valueOf("1")).getParamValue();
                break;
        }
        return text;
    }

    @Override
    public String getValueByName(String name) {
        return methodGetParameterByParamName(name, null, Byte.valueOf("1")).getParamValue();
    }

    /**
     * 删除参数信息(内部使用)
     */
    private void deleteInternal(ParameterDTO parameterDTO) {
        Parameter parameter = checkParameter(parameterDTO);
        BeanUtils.copyProperties(parameter, parameterDTO);
        ContextUtils.deleteParameter(parameterDTO.getStoreID(), parameterDTO.getParamName());
        this.removeById(parameter.getId());
    }


    @Override
    public ParameterDTO getDetails(ParameterDTO filter) {
        return BeanUtils.copyAs(checkParameter(filter), ParameterDTO.class);
    }

    @Transactional
    @Override
    public void rucUpdate(ParameterDTO parameterDTO) {
        if(null != parameterDTO) {
            Parameter parameter = checkParameter(parameterDTO);
            parameterDTO.setUpdateTime(LocalDateTime.now());
            updateInternal(parameterDTO, parameter);
        }
    }

    /**
     * 更新参数信息(内部使用)
     */
    private void updateInternal(ParameterDTO parameterDTO, Parameter parameter ) {
        BeanUtils.copyPropertiesIgnoreNull(parameterDTO, parameter);

        this.updateById(parameter);

        BeanUtils.copyProperties(parameter, parameterDTO);
        ContextUtils.addOrUpdateParameter(parameterDTO);
    }


    @Transactional
    @Override
    public void rucAdd(ParameterDTO parameterDTO) {
        if(null != parameterDTO) {
            parameterDTO.setCreateTime(LocalDateTime.now());
            addInternal(parameterDTO);
        }
    }

    private void addInternal(ParameterDTO parameterDTO) {
        Parameter parameter = new Parameter(true);
        BeanUtils.copyPropertiesIgnoreNull(parameterDTO, parameter);

        this.save(parameter);

        boolean exist = checkUniqueness(parameter.getId(), parameter.getParamName(), parameter.getStoreID(), null);
        if (exist) {
            throw new BusinessException(ResException.PARMA_ALREADY);
        }

        BeanUtils.copyProperties(parameter, parameterDTO);

        ContextUtils.addOrUpdateParameter(parameterDTO);
    }

    @Override
    public boolean checkUniqueness(Integer id, String paramName, Integer storeId, Byte status) {
        QueryWrapper<Parameter> wrapper = new QueryWrapper<>();
        wrapper.eq("paramName", paramName);
        wrapper.eq("delTf", (byte)0);
        if (null != status) {
            wrapper.eq("status", status);
        }
        if (null != storeId) {
            wrapper.eq("chainId", storeId);
        }
        wrapper.orderByAsc("id");

        List<Parameter> list = this.list(wrapper);
        if (!CollectionUtils.isEmpty(list) && list.size()>2){
            list = list.subList(0,2);
        }
        if (list.isEmpty()) {
            return false;
        }

        Parameter first = list.get(0);
        return !id.equals(first.getId());
    }

    private Parameter checkParameter(ParameterDTO filter) {
        Integer id = filter.getId();
        if (null == id) {
            throw new BusinessException(ResException.PARMA_ERR);
        }

        Parameter parameter = this.getById(id);

        if (null == parameter) {
            throw new BusinessException(ResException.PARMA_MISS);
        }

        Integer chainId = filter.getStoreID();
        if (null != chainId && !chainId.equals(0) && !chainId.equals(parameter.getStoreID())) {
            throw new BusinessException(ResException.ADMIN_PER_MISS);
        }


        return parameter;
    }


    @Override
    public List<ParameterDTO> listByFilter(ParameterFilterDTO filter, Filter filterEx) {
        List<ParameterDTO> list = new ArrayList<>();
        QueryWrapper<Parameter> wrapper = new QueryWrapper<>();
        addFilter(wrapper, filter, filterEx);
        Page<Parameter> page = new Page<>(1,10);
        if (filterEx!=null && filterEx.getPage()!=null && filterEx.getPageSize()!=null){
            page = new Page<>(filterEx.getPage(),filterEx.getPageSize());
        }
        IPage<Parameter> parameterIPage = this.baseMapper.selectPage(page, wrapper);
        if (parameterIPage!=null){
            List<Parameter> parameters = parameterIPage.getRecords();
            /*            System.out.println("parameters---"+ JSON.toJSONString(parameters));*/
            for (Parameter item : parameters) {
                ParameterDTO parameterDTO = BeanUtils.copyAs(item, ParameterDTO.class);
                /*                procNeedTranslationFields(parameterDTO);*/
                list.add(parameterDTO);
            }
        }
        return list;
    }


    private void addFilter(QueryWrapper<Parameter> wrapper, ParameterFilterDTO filter, Filter filterEx) {

        if (filterEx!=null){
            String keyword = filterEx.getKeyword();
            if (StringUtils.hasText(keyword) && StringUtils.hasText(ParameterFilterDTO.KEYWORD)){
                for (String keys : ParameterFilterDTO.KEYWORD.split(",")) {
                    wrapper.or().like(keys,filterEx.getKeyword());
                }
            }
        }
        if (filter!=null){
            if (filter.getStoreId()!=null){
                wrapper.eq("StoreID",filter.getStoreId());
            }
            if (filter.getStatus()!=null){
                wrapper.eq("Status",filter.getStatus());
            }
            if (filter.getDelTf()!=null){
                wrapper.eq("DelTf",filter.getDelTf());
            }
        }
        if (filterEx!=null && StringUtils.hasText(filterEx.getOrders())){
            wrapper.orderByDesc(filterEx.getOrders());
        }else {
            wrapper.orderByDesc("CreateTime");
        }
    }

    @Override
    public BigInteger listCount(ParameterFilterDTO filter, Filter filterEx) {
        QueryWrapper<Parameter> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filterEx.setPage(null);
            filterEx.setPageSize(null);
        }
        addFilter(wrapper, filter, filterEx);
        Integer count = this.baseMapper.selectCount(wrapper);
        return BigInteger.valueOf(count);
    }




    @Override
    public ParameterDTO getParameterByParamName(String paramName, Integer chainId, Byte status, boolean bFromCache) {
        ParameterDTO parameterDTO = null;
        if (bFromCache) {
            parameterDTO = ContextUtils.getParameter(chainId, paramName, false);
        }

        if (null == parameterDTO) {
            Parameter parameter = methodGetParameterByParamName(paramName, chainId, status);
            if (null != parameter) {
                parameterDTO = BeanUtils.copyAs(parameter, ParameterDTO.class);
                ContextUtils.addOrUpdateParameter(parameterDTO);
            }
        }

        return parameterDTO;
    }

    @Override
    public Parameter methodGetParameterByParamName(String paramName, Integer chainId, Byte status) {
        QueryWrapper<Parameter> wrapper = new QueryWrapper<>();
        wrapper.eq("paramName", paramName);
        if (null != status) {
            wrapper.eq("status", status);
        }
        if (null != chainId) {
            wrapper.eq("storeId", chainId);
        }
        return this.getOne(wrapper);
    }
}
