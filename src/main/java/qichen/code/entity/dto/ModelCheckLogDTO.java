package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.ModelCheckLog;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ModelCheckLogDTO extends ModelCheckLog {

    private List<Item> items;

    @Data
    public static class Item{
        private String title;
        private List<SubmitTableOptionDTO> submitOptions;
    }
}
