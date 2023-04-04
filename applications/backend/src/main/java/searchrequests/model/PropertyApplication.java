package searchrequests.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;

// Named "PropertyApplication" instead of "Application" to reduce confusion
@Entity(name = "application")
@Data
public class PropertyApplication {

    @Id
    @Column
    private long id;

    @Column
    private String email;

    @Column
    private Salutation salutation;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private int numberOfPersons;

    /**
     * Has certificate for subsidized housing? (Wohnungsberechtigungsschein)
     */
    @Column
    private boolean wbsPresent;

    @Column
    private ZonedDateTime earliestMoveInDate;

    @Column
    private boolean pets;

    @Column
    private Status status;

    @Column
    private String applicantComment;

    @Column
    private String userComment;

    @Column
    private ZonedDateTime creationTimestamp;

    @Column
    private CreationSource creationSource;

    /**
     * This is a foreign key for the "properties" table (which is not part of the challenge)
     */
    @Column
    private long propertyId;
}
