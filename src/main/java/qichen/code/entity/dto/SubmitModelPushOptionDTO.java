package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.SubmitModelPushOption;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubmitModelPushOptionDTO extends SubmitModelPushOption {
    private Integer must;
    private String title;
}
