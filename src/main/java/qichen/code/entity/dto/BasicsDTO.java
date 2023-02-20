package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.Basics;

@EqualsAndHashCode(callSuper = true)
@Data
public class BasicsDTO extends Basics {
    private String partsName;
    private String submitName;
    private String errName;
}
