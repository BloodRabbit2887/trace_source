package qichen.code.service;

import qichen.code.entity.ComponentOption;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.ComponentOptionDTO;
import qichen.code.model.Filter;

import java.util.List;

/**
 * <p>
 * 装配车间部件检测项目表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-05
 */
public interface IComponentOptionService extends IService<ComponentOption> {

    void removeByComponentID(Integer componentId);

    List<ComponentOptionDTO> listByFilter(ComponentOptionDTO componentOptionDTO, Filter filter);
}
