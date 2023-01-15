package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.AssembleComponent;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssembleComponentDTO extends AssembleComponent {

    private List<SubmitComponentOptionDTO> submitOptions;
    private List<ComponentOptionDTO> componentOptions;
}
