package searchrequests.dto;

import lombok.Data;
import searchrequests.model.Salutation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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

    private String userComment;

    @NotNull
    private Long propertyId;
}
