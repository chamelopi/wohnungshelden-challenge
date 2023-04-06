package searchrequests.business;

import org.springframework.data.jpa.domain.Specification;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;

import java.util.Set;

public class FilterSpecifications {

    private FilterSpecifications() {
    }

    public static final Set<String> AVAILABLE_FIELDS = Set.of("propertyId", "email", "numberOfPersons", "wbsPresent", "status");

    public static Specification<PropertyApplication> hasPropertyId(long propertyId) {
        return (application, ignored, cb) -> cb.equal(application.get("propertyId"), propertyId);
    }

    public static Specification<PropertyApplication> hasEmail(String email) {
        return (application, ignored, cb) -> cb.equal(application.get("email"), email);
    }

    public static Specification<PropertyApplication> hasNumberOfPersons(int numberOfPersons) {
        return (application, ignored, cb) -> cb.equal(application.get("numberOfPersons"), numberOfPersons);
    }

    public static Specification<PropertyApplication> isWbsPresent(boolean wbsPresent) {
        return (application, ignored, cb) -> cb.equal(application.get("wbsPresent"), wbsPresent);
    }

    public static Specification<PropertyApplication> hasStatus(Status status) {
        return (application, ignored, cb) -> cb.equal(application.get("status"), status);
    }
}