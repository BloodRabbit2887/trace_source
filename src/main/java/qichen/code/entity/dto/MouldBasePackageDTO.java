package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.MouldBasePackage;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MouldBasePackageDTO extends MouldBasePackage {
    private List<AssembleComponentDTO> components;

    private String submitName;
    private String verifyName;

    private Integer submit;
}
