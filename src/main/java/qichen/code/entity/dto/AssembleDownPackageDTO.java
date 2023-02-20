package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.AssembleDownPackage;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssembleDownPackageDTO extends AssembleDownPackage {

    private List<AssembleComponentDTO> components;

    private String submitName;
    private String verifyName;

    private Integer submit;
}
