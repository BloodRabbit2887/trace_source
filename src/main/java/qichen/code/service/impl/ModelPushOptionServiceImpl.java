package qichen.code.service.impl;

import qichen.code.entity.ModelPushOption;
import qichen.code.mapper.ModelPushOptionMapper;
import qichen.code.service.IModelPushOptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 模具入库点检事项表 (装配车间) 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
@Service
public class ModelPushOptionServiceImpl extends ServiceImpl<ModelPushOptionMapper, ModelPushOption> implements IModelPushOptionService {

    @Override
    public List<ModelPushOption> commitModelPushLogBatch(List<String> list) {
        list = list.stream().distinct().collect(Collectors.toList());
        List<ModelPushOption> optionList = list.stream().map(detail -> {
            ModelPushOption option = new ModelPushOption();
            option.setDetail(detail);
            option.setCreateTime(LocalDateTime.now());
            return option;
        }).collect(Collectors.toList());
        saveBatch(optionList);
        return optionList;
    }
}
