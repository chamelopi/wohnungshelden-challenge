package searchrequests.dto;

import lombok.Data;
import searchrequests.model.Salutation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UiApplicationDto {
    @Email
    @NotBlank(message = "E-Mail is a required field")
    private String email;

    private Salutation salutation;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Size(max = 1000)
    private String userComment;

    @NotNull
    private Long propertyId;
}
