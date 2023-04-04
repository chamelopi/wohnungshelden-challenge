package searchrequests.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.Data;
import searchrequests.model.Salutation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

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

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime earliestMoveInDate;

    private boolean pets;

    private String applicantComment;

    @NotNull
    private Long propertyId;
}
