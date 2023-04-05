package searchrequests.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;
import searchrequests.persistence.FilterSpecifications;
import searchrequests.persistence.PropertyApplicationRepository;

import java.util.ArrayList;
import java.util.Map;

@Service
public class PropertyApplicationService {

    @Autowired
    private PropertyApplicationRepository repo;

    public Iterable<PropertyApplication> filterApplications(Map<String, String> filterParameters) {
        if (filterParameters.isEmpty()) {
            return repo.findAll();
        }

        // Validate filter parameters - not all fields can be filtered
        if (!FilterSpecifications.AVAILABLE_FIELDS.containsAll(filterParameters.keySet())) {
            throw new IllegalArgumentException("unknown filter parameters");
        }

        var specs = new ArrayList<Specification<PropertyApplication>>();
        if (filterParameters.containsKey("email")) {
            specs.add(FilterSpecifications.hasEmail(filterParameters.get("email")));
        }
        if (filterParameters.containsKey("propertyId")) {
            specs.add(FilterSpecifications.hasPropertyId(Long.parseLong(filterParameters.get("propertyId"))));
        }
        if (filterParameters.containsKey("numberOfPersons")) {
            specs.add(FilterSpecifications.hasNumberOfPersons(Integer.parseInt(filterParameters.get("numberOfPersons"))));
        }
        if (filterParameters.containsKey("wbsPresent")) {
            specs.add(FilterSpecifications.isWbsPresent(Boolean.parseBoolean(filterParameters.get("wbsPresent"))));
        }
        if (filterParameters.containsKey("status")) {
            specs.add(FilterSpecifications.hasStatus(Status.valueOf(filterParameters.get("status"))));
        }

        // We know that there is at least one filter present at this point
        var query = Specification.where(specs.get(0));
        for (int i = 1; i < specs.size(); i++) {
            query = query.and(specs.get(i));
        }
        return repo.findAll(query);
    }
}
