package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.SparePartsLog;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SparePartsLogDTO extends SparePartsLog {

    private List<SparePartsSizeDTO> sparePartsSizes;

    private List<TableOptionDTO> tableOptions;

    private List<SubmitTableOptionDTO> submitOptions;

    private String submitName;
    private String verifyName;

    private Integer submit;
}
