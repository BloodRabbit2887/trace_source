package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.Admin;
import qichen.code.entity.Basics;
import qichen.code.entity.Option;
import qichen.code.entity.dto.BasicsDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.BasicsMapper;
import qichen.code.model.Filter;
import qichen.code.service.IAdminService;
import qichen.code.service.IBasicsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IOptionService;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 基础库表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-30
 */
@Service
public class BasicsServiceImpl extends ServiceImpl<BasicsMapper, Basics> implements IBasicsService {

    @Autowired
    private IOptionService optionService;
    @Autowired
    private IAdminService adminService;

    @Override
    public Basics add(BasicsDTO dto) {

        checkDraft(dto);

        Map<String,String> params = new HashMap<>();
        if (dto.getDraft()==null || dto.getDraft()==0){
            params.put("number","模号");
            params.put("errTypeId","故障类型");
            params.put("measure","处理措施");
            params.put("question","装配问题");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));
        }

        Basics basics = BeanUtils.copyAs(dto, Basics.class);
        saveOrUpdate(basics);

        return basics;
    }

    private void checkDraft(BasicsDTO dto) {
        QueryWrapper<Basics> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",dto.getSubmitId());
        wrapper.eq("`draft`",1);
        if (dto.getId()!=null){
            wrapper.ne("`ID`",dto.getId());
        }
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前存在未完成草稿");
        }
    }

    @Override
    public List<BasicsDTO> listByFilter(BasicsDTO dto, Filter filter) {
        List<Basics> list = listFilter(dto,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    private List<BasicsDTO> listDTO(List<Basics> list) {
        List<BasicsDTO> dtos = BeanUtils.copyAs(list, BasicsDTO.class);

        List<Option> errs = (List<Option>) optionService.listByIds(dtos.stream().map(BasicsDTO::getErrTypeId).distinct().collect(Collectors.toList()));
        List<Admin> admins = (List<Admin>) adminService.listByIds(dtos.stream().map(BasicsDTO::getSubmitId).distinct().collect(Collectors.toList()));
        List<Option> parts = (List<Option>) optionService.listByIds(dtos.stream().map(BasicsDTO::getPartsId).distinct().collect(Collectors.toList()));

        for (BasicsDTO dto : dtos) {
            if (!CollectionUtils.isEmpty(errs) && errs.size()>0){
                for (Option err : errs) {
                    if (err.getId().equals(dto.getErrTypeId())){
                        dto.setErrName(err.getTitle());
                    }
                }
            }
            if (!CollectionUtils.isEmpty(admins) && admins.size()>0){
                for (Admin admin : admins) {
                    if (admin.getId().equals(dto.getSubmitId())){
                        dto.setSubmitName(admin.getAdminName());
                    }
                }
            }
            if (!CollectionUtils.isEmpty(parts) && parts.size()>0){
                for (Option part : parts) {
                    if (part.getId().equals(dto.getPartsId())){
                        dto.setPartsName(part.getTitle());
                    }
                }
            }
        }

        return dtos;
    }

    private List<Basics> listFilter(BasicsDTO dto, Filter filter) {
        QueryWrapper<Basics> wrapper = new QueryWrapper<>();
        addFilter(wrapper,dto,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<Basics> wrapper, BasicsDTO dto, Filter filter) {
        if (dto!=null){
            if (!StringUtils.isEmpty(dto.getNumber()) && dto.getNumber().length()>0){
                wrapper.eq("`number`",dto.getNumber());
            }
            if (dto.getErrTypeId()!=null){
                wrapper.eq("errTypeId",dto.getErrTypeId());
            }
            if (dto.getDraft()!=null){
                wrapper.eq("`draft`",dto.getDraft());
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
                wrapper.like("`number`",filter.getKeyword());
            }

            if (filter.getPage()!=null && filter.getPageSize()!=null && filter.getPage()!=0 && filter.getPageSize()!=0){
                int fast = filter.getPage()<=1?0:(filter.getPage()-1)*filter.getPageSize();
                wrapper.last(" limit "+fast+", "+filter.getPageSize());
            }
        }
    }

    @Override
    public BigInteger listCount(BasicsDTO dto, Filter filter) {
        QueryWrapper<Basics> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFilter(wrapper,dto,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public BasicsDTO getDetail(Integer id) {
        Basics basics = getById(id);
        if (basics==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(basics);
    }

    @Override
    public Basics adminUpdate(BasicsDTO dto) {
        Basics basics = getById(dto.getId());
        if (basics==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        check(dto);
        Basics entity = BeanUtils.copyAs(dto, Basics.class);
        updateById(entity);
        return entity;
    }

    @Override
    public Basics delete(Integer id) {
        Basics basics = getById(id);
        if (basics==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        removeById(id);
        return basics;
    }

    @Override
    public BasicsDTO getDraft(Integer id) {
        QueryWrapper<Basics> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",id);
        wrapper.eq("`draft`",1);
        Basics basics = getOne(wrapper);
        if (basics!=null){
            return getDTO(basics);
        }
        return null;
    }

    private void check(BasicsDTO dto) {
        //TODO
    }

    private BasicsDTO getDTO(Basics basics) {
        BasicsDTO dto = BeanUtils.copyAs(basics, BasicsDTO.class);

        Admin admin = adminService.getById(basics.getSubmitId());
        if (admin!=null){
            dto.setSubmitName(admin.getAdminName());
        }

        Option part = optionService.getById(basics.getPartsId());
        if (part!=null){
            dto.setPartsName(part.getTitle());
        }

        Option errType = optionService.getById(basics.getErrTypeId());
        if (errType!=null){
            dto.setErrName(errType.getTitle());
        }

        return dto;
    }
}
