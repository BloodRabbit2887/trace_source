package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.ErrModel;
import qichen.code.entity.ErrType;
import qichen.code.entity.User;
import qichen.code.entity.dto.ErrModelDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.ErrModelMapper;
import qichen.code.model.Filter;
import qichen.code.service.IErrModelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IErrTypeService;
import qichen.code.service.IUserService;
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
 * 错误典型库表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-29
 */
@Service
public class ErrModelServiceImpl extends ServiceImpl<ErrModelMapper, ErrModel> implements IErrModelService {

    @Autowired
    private IErrTypeService errTypeService;
    @Autowired
    private IUserService userService;

    @Transactional
    @Override
    public ErrModel commit(ErrModelDTO dto) {

        Map<String,String> params = new HashMap<>();
        params.put("typeId","错误类型");
        params.put("number","模号");
        params.put("title","错误标题");
        params.put("detail","问题描述");
        params.put("temporary","临时措施");
        params.put("longTerm","长期措施");
        JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));

        check(dto);

        if (dto.getId()==null){
            dto.setVerifyStatus(0);
        }

        ErrModel errModel = BeanUtils.copyAs(dto, ErrModel.class);
        saveOrUpdate(errModel);
        return errModel;
    }

    @Override
    public List<ErrModelDTO> listByFilter(ErrModelDTO dto, Filter filter) {
        List<ErrModel> list = listFilter(dto,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }


    private List<ErrModelDTO> listDTO(List<ErrModel> list) {
        List<ErrModelDTO> dtos = BeanUtils.copyAs(list, ErrModelDTO.class);

        List<Integer> typeIds = dtos.stream().map(ErrModelDTO::getTypeId).distinct().collect(Collectors.toList());
        List<ErrType> errTypes = (List<ErrType>) errTypeService.listByIds(typeIds);


        List<User> users = (List<User>) userService.listByIds(list.stream().map(ErrModel::getSubmitId).distinct().collect(Collectors.toList()));

        for (ErrModelDTO dto : dtos) {
            if (!CollectionUtils.isEmpty(errTypes) && errTypes.size()>0){
                for (ErrType errType : errTypes) {
                    if (errType.getId().equals(dto.getTypeId())){
                        dto.setErrTypeName(errType.getTitle());
                        dto.setErrTypeRemark(errType.getRemark());
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

        return dtos;
    }

    private List<ErrModel> listFilter(ErrModelDTO dto, Filter filter) {
        QueryWrapper<ErrModel> wrapper = new QueryWrapper<>();
        addFilter(wrapper,dto,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<ErrModel> wrapper, ErrModelDTO dto, Filter filter) {
        if (dto!=null){
            if (dto.getTypeId()!=null){
                wrapper.eq("typeId",dto.getTypeId());
            }
            if (dto.getStatus()!=null){
                wrapper.eq("`Status`",dto.getStatus());
            }
            if (dto.getVerifyStatus()!=null){
                wrapper.eq("verifyStatus",dto.getVerifyStatus());
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

    @Override
    public BigInteger listCount(ErrModelDTO dto, Filter filter) {
        QueryWrapper<ErrModel> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFilter(wrapper,dto,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public ErrModelDTO getDetail(Integer id) {
        ErrModel errModel = getById(id);
        if (errModel==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(errModel);
    }

    @Transactional
    @Override
    public ErrModel verify(ErrModelDTO modelDTO) {
        ErrModel model = getById(modelDTO.getId());
        if (model==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        org.springframework.beans.BeanUtils.copyProperties(modelDTO,model);
        updateById(model);
        return model;
    }

    @Transactional
    @Override
    public void removeByTypeId(Integer typeId) {
        UpdateWrapper<ErrModel> wrapper = new UpdateWrapper<>();
        wrapper.eq("typeId",typeId);
        remove(wrapper);
    }

    @Transactional
    @Override
    public ErrModel delete(Integer id) {
        ErrModel model = getById(id);
        if (model==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        removeById(id);
        return model;
    }

    private ErrModelDTO getDTO(ErrModel errModel) {
        ErrModelDTO dto = BeanUtils.copyAs(errModel, ErrModelDTO.class);
        ErrType errType = errTypeService.getById(errModel.getTypeId());

        if (errType!=null){
            dto.setErrTypeName(errType.getTitle());
            dto.setErrTypeRemark(errType.getRemark());
        }

        return dto;
    }

    private void check(ErrModelDTO dto) {
        Integer typeId = dto.getTypeId();
        if (typeId!=null){
            ErrType errType = errTypeService.getById(typeId);
            if (errType==null){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"错误类型有误");
            }
        }

        String title = dto.getTitle();
        if (!StringUtils.isEmpty(title) && title.length()>0){
            QueryWrapper<ErrModel> wrapper = new QueryWrapper<>();
            wrapper.eq("`title`",title);
            if (dto.getId()!=null){
                wrapper.ne("ID",dto.getId());
            }
            Integer count = baseMapper.selectCount(wrapper);
            if (count>0){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"标题重复");
            }
        }
    }
}
