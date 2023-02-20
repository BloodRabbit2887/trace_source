package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.AssembleOrder;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssembleOrderDTO extends AssembleOrder {
    private AssembleCheckAlloyPackageDTO alloyPackage;//合金组装组扭转部位工作检查表
    private Integer submit;
}
