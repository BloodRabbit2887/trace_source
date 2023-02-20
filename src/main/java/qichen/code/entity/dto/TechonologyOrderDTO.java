package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.TechnologyOrder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TechonologyOrderDTO extends TechnologyOrder {

    private List<TableOptionDTO> tableOptions;

    private List<SubmitTableOptionDTO> submitOptions;

    private String submitName;
    private String verifyName;

    private Integer submit;
}
