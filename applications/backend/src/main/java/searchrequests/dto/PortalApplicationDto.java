package searchrequests.dto;

import lombok.Data;
import searchrequests.model.Salutation;

import javax.validation.constraints.*;
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

    @Future
    private LocalDate earliestMoveInDate;

    private boolean pets;

    @Size(max = 1000)
    private String applicantComment;

    @NotNull
    private Long propertyId;
}
