package searchrequests.dto;

import lombok.Data;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;

import javax.validation.constraints.Size;

/**
 * Defines which fields of a {@link PropertyApplication} can be modified
 */
@Data
public class PropertyApplicationUpdateDto {
    @Size(max = 1000)
    private String userComment;

    private Status status;
}

