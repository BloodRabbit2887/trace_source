package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.ErrModel;

@EqualsAndHashCode(callSuper = true)
@Data
public class ErrModelDTO extends ErrModel {

    private String errTypeName;
    private String errTypeRemark;
    private String submitName;

}
