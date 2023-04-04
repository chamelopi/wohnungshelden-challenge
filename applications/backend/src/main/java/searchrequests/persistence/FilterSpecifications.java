package searchrequests.persistence;

import org.springframework.data.jpa.domain.Specification;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;

public class FilterSpecifications {
    public static Specification<PropertyApplication> hasPropertyId(long propertyId) {
        return (application, ignored, cb) -> cb.equal(application.get("propertyId"), propertyId);
    }

    public static Specification<PropertyApplication> hasEmail(String email) {
        return (application, ignored, cb) -> cb.equal(application.get("email"), email);
    }

    public static Specification<PropertyApplication> hasNumberOfPersons(int numberOfPersons) {
        return (application, ignored, cb) -> cb.equal(application.get("number_of_persons"), numberOfPersons);
    }

    public static Specification<PropertyApplication> isWbsPresent(boolean wbsPresent) {
        return (application, ignored, cb) -> cb.equal(application.get("wbs_present"), wbsPresent);
    }

    public static Specification<PropertyApplication> hasStatus(Status status) {
        return (application, ignored, cb) -> cb.equal(application.get("status"), status);
    }
}