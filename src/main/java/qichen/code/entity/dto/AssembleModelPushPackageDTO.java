package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.AssembleModelPushPackage;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssembleModelPushPackageDTO extends AssembleModelPushPackage {

    private List<ModelPushOptionDTO> modelPushOptions;
    private List<SubmitModelPushOptionDTO> submitOptions;
}
