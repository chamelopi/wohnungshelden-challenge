package searchrequests.dto;

import lombok.Data;
import searchrequests.model.Salutation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class PortalApplicationDto {
    @Email
    @NotBlank(message = "E-Mail is a required field")
    private String email;

    private Salutation salutation;

    // Note: First name is optional here - different from UiApplicationDto
    private String firstName;

    @NotBlank
    private String lastName;

    private int numberOfPersons;

    private boolean wbsPresent;

    private LocalDate earliestMoveInDate;

    private boolean pets;

    private String applicantComment;

    @NotNull
    private Long propertyId;
}
