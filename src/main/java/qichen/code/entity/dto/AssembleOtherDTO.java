package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.AssembleOther;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssembleOtherDTO extends AssembleOther {

    private Integer need;
}
