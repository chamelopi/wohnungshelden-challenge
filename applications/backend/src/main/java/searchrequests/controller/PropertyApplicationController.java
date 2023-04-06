package searchrequests.controller;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import searchrequests.business.PropertyApplicationService;
import searchrequests.dto.PortalApplicationDto;
import searchrequests.dto.UiApplicationDto;
import searchrequests.dto.mapper.ApplicationMapper;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;
import searchrequests.persistence.PropertyApplicationRepository;

import javax.validation.Valid;
import java.time.Instant;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class PropertyApplicationController {

    // TODO/Nice to have: ControllerAdvice to return nice error messages for frontend

    @Autowired
    private PropertyApplicationRepository repo;

    @Autowired
    private PropertyApplicationService service;

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

    @GetMapping
    @RequestMapping(value = "/applications/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<PropertyApplication>> filterApplications(@RequestParam Map<String, String> filterParameters,
                                                                            Pageable pageable) {
        var page = service.filterApplications(filterParameters, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                // For frontend to calculate the total number of pages
                .header("Total-Items", String.valueOf(page.getTotalElements()))
                .body(page.toList());
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
