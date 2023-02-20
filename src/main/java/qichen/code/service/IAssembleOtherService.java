package qichen.code.service;

import qichen.code.entity.AssembleOther;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssembleOtherDTO;

/**
 * <p>
 * 装配车间额外表单 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-02-17
 */
public interface IAssembleOtherService extends IService<AssembleOther> {

    AssembleOtherDTO getOtherModel(Integer userId, String number);

    void add(AssembleOtherDTO dto);
}
