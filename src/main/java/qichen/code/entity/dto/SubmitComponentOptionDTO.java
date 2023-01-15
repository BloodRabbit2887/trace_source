package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.SubmitComponentOption;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubmitComponentOptionDTO extends SubmitComponentOption {

    private Integer number;
    private String checkDetail;
    private String needs;
}
