package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.AssembleCheckPackage;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssembleCheckPackageDTO extends AssembleCheckPackage {

    private List<AssembleComponentDTO> components;
}
