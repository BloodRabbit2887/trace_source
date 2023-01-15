package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.OptionType;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OptionTypeDTO extends OptionType {

    private List<OptionDTO> options;
}
