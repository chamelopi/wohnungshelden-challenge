package searchrequests.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;
import searchrequests.persistence.PropertyApplicationRepository;

import java.util.ArrayList;
import java.util.Map;

@Service
public class PropertyApplicationService {

    @Autowired
    private PropertyApplicationRepository repo;

    public Page<PropertyApplication> filterApplications(Map<String, String> filterParameters, Pageable pageable) {
        if (filterParameters.isEmpty()) {
            // null -> no filter
            return repo.findAll(null, pageable);
        }

        var filterQuery = FilterSpecifications.buildFilter(filterParameters);
        return repo.findAll(filterQuery, pageable);
    }
}
