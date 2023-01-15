package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.ComponentOption;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ComponentOptionDTO extends ComponentOption {
    private List<Integer> componentIds;
}
