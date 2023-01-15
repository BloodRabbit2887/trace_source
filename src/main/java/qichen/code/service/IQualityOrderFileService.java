package qichen.code.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import qichen.code.entity.QualityOrderFile;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.QualityOrderFileDTO;
import qichen.code.model.Filter;

import java.util.List;

/**
 * <p>
 * 质量管理部工单内文件表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
public interface IQualityOrderFileService extends IService<QualityOrderFile> {

    void freshFile(QualityOrderFileDTO file);

    List<QualityOrderFileDTO> listByFilter(QualityOrderFileDTO fileDTO, Filter filter);

    void addFilter(QueryWrapper<QualityOrderFile> wrapper, QualityOrderFileDTO fileDTO, Filter filter);

    void freshFileVersion(Integer orderId);
}
