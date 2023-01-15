package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.Option;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OptionDTO extends Option {

    private List<Integer> typeIds;
    private String typeName;
    private Integer tableOptionId;
}
