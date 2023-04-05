package searchrequests.controller;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import searchrequests.dto.PortalApplicationDto;
import searchrequests.dto.UiApplicationDto;
import searchrequests.dto.mapper.ApplicationMapper;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;
import searchrequests.persistence.FilterSpecifications;
import searchrequests.persistence.PropertyApplicationRepository;

import javax.validation.Valid;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class PropertyApplicationController {

    // TODO/Nice to have: ControllerAdvice to return nice error messages for frontend

    @Autowired
    PropertyApplicationRepository repo;

    @PostMapping
    @RequestMapping(value = "/ui/applications/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PropertyApplication> createApplicationFromUi(@RequestBody @Valid UiApplicationDto uiApplication) {
        var propertyApplication = Mappers.getMapper(ApplicationMapper.class).uiApplicationToPropertyApplication(uiApplication);
        return createPropertyApplication(propertyApplication);
    }

    @PostMapping
    @RequestMapping(value = "/portal/applications/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PropertyApplication> createApplicationFromPortal(@RequestBody @Valid PortalApplicationDto portalApplication) {
        var propertyApplication = Mappers.getMapper(ApplicationMapper.class).portalApplicationToPropertyApplication(portalApplication);
        return createPropertyApplication(propertyApplication);
    }

    @GetMapping
    @RequestMapping(value = "/applications/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PropertyApplication> getApplicationById(@PathVariable @Valid long id) {
        var application = repo.findById(id);
        // TODO: maybe add error response/error message here, too
        return application.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // FIXME: Refactor into business layer
    // TODO: API might not be optimal - could conflict with paging later
    @GetMapping
    @RequestMapping(value = "/applications/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<PropertyApplication>> filterApplications(@RequestParam Map<String, String> filterParameters) {
        if (filterParameters.isEmpty()) {
            return ResponseEntity.ok(repo.findAll());
        }

        // Validate filter parameters - not all fields can be filtered
        if (!FilterSpecifications.AVAILABLE_FIELDS.containsAll(filterParameters.keySet())) {
            return ResponseEntity.badRequest().build();
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
        return ResponseEntity.ok(repo.findAll(query));
    }

    // This could be refactored into a business layer later if necessary
    private ResponseEntity<PropertyApplication> createPropertyApplication(PropertyApplication propertyApplication) {
        propertyApplication.setStatus(Status.CREATED);
        propertyApplication.setCreationTimestamp(Instant.now());
        propertyApplication = repo.save(propertyApplication);

        // We should use the MDC if we wanted log these IDs in more places, but this is ok for now
        log.info("PropertyApplication created with propertyId={}, applicationId={} and source={}",
                propertyApplication.getPropertyId(), propertyApplication.getId(), propertyApplication.getCreationSource().name());

        // Build a URL to the newly created resource
        var url = ServletUriComponentsBuilder.fromCurrentServletMapping()
                .path("api/v1/applications/{id}")
                .buildAndExpand(propertyApplication.getId())
                .toUri();

        return ResponseEntity.created(url).body(propertyApplication);
    }
}
