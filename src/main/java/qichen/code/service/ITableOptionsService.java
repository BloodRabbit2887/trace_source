package qichen.code.service;

import qichen.code.entity.TableOptions;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.TableOptionDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 表单选项表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-11
 */
public interface ITableOptionsService extends IService<TableOptions> {

    List<TableOptions> addBatch(List<TableOptionDTO> tableOptions);

    TableOptions delete(Integer id);

    TableOptions adminUpdate(TableOptionDTO dto);

    List<TableOptionDTO> listByFilter(TableOptionDTO dto, Filter filter);

    BigInteger listCount(TableOptionDTO dto, Filter filter);

    TableOptionDTO getDetail(Integer id);
}
