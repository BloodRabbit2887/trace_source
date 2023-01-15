package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.SparePartsSize;

@EqualsAndHashCode(callSuper = true)
@Data
public class SparePartsSizeDTO extends SparePartsSize {
    private String toolName;
}
