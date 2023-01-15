package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.QualityOrder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QualityOrderDTO extends QualityOrder {

    List<QualityOrderFileDTO> files;

    private ModelCheckLogDTO modelCheckLog;//模具检测报告(质量管理部)
    private SparePartsLogDTO sparePartsLog;//零件检测报告(质量管理部)

}
