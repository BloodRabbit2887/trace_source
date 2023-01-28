package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.AssembleCheckAlloyPackage;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssembleCheckAlloyPackageDTO extends AssembleCheckAlloyPackage {

    private String modelTitle;

    private List<AssembleComponentDTO> components;

}
