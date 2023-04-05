package searchrequests.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

// Named "PropertyApplication" instead of "Application" to reduce confusion
@Entity(name = "application")
@Data
public class PropertyApplication {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // Time or time zone make no sense here -> LocalDate
    @Column
    private LocalDate earliestMoveInDate;

    @Column
    private boolean pets;

    @Column
    private Status status;

    @Column
    private String applicantComment;

    @Column
    private String userComment;

    // Technical timestamp -> Instant
    @Column
    private Instant creationTimestamp;

    @Column
    private CreationSource creationSource;

    /**
     * This is a foreign key for the "properties" table (which is not part of the challenge)
     */
    @Column
    private long propertyId;
}
