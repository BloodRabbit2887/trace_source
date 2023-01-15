package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.QualityOrderFile;
import qichen.code.entity.dto.QualityOrderFileDTO;
import qichen.code.mapper.QualityOrderFileMapper;
import qichen.code.model.Filter;
import qichen.code.service.IQualityOrderFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 质量管理部工单内文件表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Service
public class QualityOrderFileServiceImpl extends ServiceImpl<QualityOrderFileMapper, QualityOrderFile> implements IQualityOrderFileService {

    @Override
    public void freshFile(QualityOrderFileDTO file) {
        QualityOrderFile oldFile = getByVersion(file.getVersion(),file.getQualityOrderID(),file.getType());
        if (oldFile!=null){
            file.setId(oldFile.getId());
            updateById(BeanUtils.copyAs(file,QualityOrderFile.class));
        }else {
            Map<String,String> params = new HashMap<>();
            params.put("title","文件标题");
            params.put("pdfLink","图片");
            params.put("imgLink","图片");
            params.put("version","版本号");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(file)));
            QualityOrderFile qualityOrderFile = BeanUtils.copyAs(file, QualityOrderFile.class);
            save(qualityOrderFile);
        }
    }

    @Override
    public List<QualityOrderFileDTO> listByFilter(QualityOrderFileDTO fileDTO, Filter filter) {
        List<QualityOrderFile> list = listFilter(fileDTO,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    private List<QualityOrderFile> listFilter(QualityOrderFileDTO fileDTO, Filter filter) {
        QueryWrapper<QualityOrderFile> wrapper = new QueryWrapper<>();
        addFilter(wrapper,fileDTO,filter);
        return list(wrapper);
    }

    @Override
    public void addFilter(QueryWrapper<QualityOrderFile> wrapper, QualityOrderFileDTO fileDTO, Filter filter) {
        if (fileDTO!=null){
            if (fileDTO.getQualityOrderID()!=null){
                wrapper.eq("qualityOrderID",fileDTO.getQualityOrderID());
            }
            if (fileDTO.getNewVersion()!=null){
                wrapper.eq("`newVersion`",fileDTO.getNewVersion());
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

    @Override
    public void freshFileVersion(Integer orderId) {
        QualityOrderFileDTO qualityOrderFileDTO = new QualityOrderFileDTO();
        qualityOrderFileDTO.setQualityOrderID(orderId);
        List<QualityOrderFile> list = listFilter(qualityOrderFileDTO, null);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
            for (QualityOrderFile file : list) {
                file.setNewVersion(0);
            }
            updateBatchById(list);
            freshVersionByType(list,1);
            freshVersionByType(list,2);
            freshVersionByType(list,3);
            freshVersionByType(list,4);
        }
    }

    private void freshVersionByType(List<QualityOrderFile> list, Integer type) {
        List<QualityOrderFile> files = list.stream().filter(file -> file.getType().equals(type)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(files) && files.size()>0){
            QualityOrderFile file = files.stream().max(Comparator.comparing(QualityOrderFile::getVersion)).get();
            file.setNewVersion(1);
            updateById(file);
        }
    }

    private List<QualityOrderFileDTO> listDTO(List<QualityOrderFile> list) {
        List<QualityOrderFileDTO> dtos = BeanUtils.copyAs(list, QualityOrderFileDTO.class);
        //TODO
        return dtos;
    }

    private QualityOrderFile getByVersion(BigDecimal version, Integer qualityOrderID, Integer type) {
        QueryWrapper<QualityOrderFile> wrapper = new QueryWrapper<>();
        wrapper.eq("`version`",version);
        wrapper.eq("qualityOrderID",qualityOrderID);
        wrapper.eq("`type`",type);
        return getOne(wrapper);
    }
}
