package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.DeviseOrder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeviseOrderDTO extends DeviseOrder {

    private List<TableOptionDTO> tableOptions;

    private List<SubmitTableOptionDTO> submitOptions;

    private String submitName;
}
