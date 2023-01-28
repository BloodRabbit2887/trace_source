package qichen.code.service;

import qichen.code.entity.AssembleComponent;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssembleComponentDTO;
import qichen.code.model.Filter;

import java.util.List;

/**
 * <p>
 * 装配车间检测部件表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-05
 */
public interface IAssembleComponentService extends IService<AssembleComponent> {

    AssembleComponent add(AssembleComponentDTO assembleComponentDTO);

    AssembleComponent adminUpdate(AssembleComponentDTO assembleComponentDTO);

    AssembleComponent delete(Integer id);

    List<AssembleComponentDTO> listDTO(List<AssembleComponent> components);

    List<AssembleComponentDTO> listByFilter(AssembleComponentDTO dto, Filter filter);
}
