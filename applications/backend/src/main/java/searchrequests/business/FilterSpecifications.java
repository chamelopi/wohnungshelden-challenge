package searchrequests.business;

import org.springframework.data.jpa.domain.Specification;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Creates {@link Specification}s for filtering the PropertyApplication repository.
 * <p><p>
 * Note: could be generified for use with other entities if necessary
 */
public class FilterSpecifications {

    private FilterSpecifications() {
    }

    /**
     * For each field that can be filtered, provides a conversion function from String to its respective type.
     * <p><p>
     * To add a filter parameter, simply extend this map.
     */
    private static final Map<String, Function<String, Object>> FIELD_CONVERTERS = Map.of(
            "propertyId", Long::valueOf,
            "email", String::valueOf,
            "numberOfPersons", Integer::valueOf,
            "wbsPresent", Boolean::valueOf,
            "status", Status::valueOf
    );

    /**
     * Describes which parameters are used by spring boot Pageable and therefore should be ignored by the filter logic
     */
    private static final Set<String> PAGING_PARAMETERS = Set.of("page", "size");

    /**
     * Creates a single "equals" filter Specification for the provided parameter
     *
     * @param name      name of the field of PropertyApplication we want to filter
     * @param converter function which converts a String to the appropriate data type of the field
     * @param value     the value to filter for
     */
    public static <T> Specification<PropertyApplication> getFilterSpecification(String name, Function<String, T> converter, String value) {
        T convertedValue = converter.apply(value);

        return (application, ignored, criteriaBuilder) -> criteriaBuilder.equal(application.get(name), convertedValue);
    }

    /**
     * Creates a conjunction of filter Specifications from a map of query string parameters
     *
     * @param filterParameters map of query parameters as provided by spring boot
     * @return a Specification containing a conjunction (= AND) of all created filters
     */
    public static Specification<PropertyApplication> buildFilter(Map<String, String> filterParameters) {
        Specification<PropertyApplication> spec = null;

        for (var filterParameter : filterParameters.entrySet()) {
            // Ignore paging parameters
            if (PAGING_PARAMETERS.contains(filterParameter.getKey())) {
                continue;
            }

            // Check if this is a known filter parameter
            if (!FIELD_CONVERTERS.containsKey(filterParameter.getKey())) {
                throw new IllegalArgumentException("Invalid filter parameter " + filterParameter.getKey());
            }

            var currentSpec = getFilterSpecification(filterParameter.getKey(), FIELD_CONVERTERS.get(filterParameter.getKey()), filterParameter.getValue());
            // Either append to existing specification or start with "where"
            if (spec == null) {
                spec = Specification.where(currentSpec);
            } else {
                spec = spec.and(currentSpec);
            }
        }

        return spec;
    }
}