package qichen.code.service.impl;

import qichen.code.entity.ModelType;
import qichen.code.mapper.ModelTypeMapper;
import qichen.code.service.IModelTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 模具类别表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Service
public class ModelTypeServiceImpl extends ServiceImpl<ModelTypeMapper, ModelType> implements IModelTypeService {

}
