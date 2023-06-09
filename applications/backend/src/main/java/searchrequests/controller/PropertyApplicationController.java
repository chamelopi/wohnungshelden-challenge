package searchrequests.controller;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import searchrequests.business.PropertyApplicationService;
import searchrequests.dto.PortalApplicationDto;
import searchrequests.dto.PropertyApplicationUpdateDto;
import searchrequests.dto.UiApplicationDto;
import searchrequests.dto.mapper.ApplicationMapper;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;
import searchrequests.persistence.PropertyApplicationRepository;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class PropertyApplicationController {

    @Autowired
    private PropertyApplicationRepository repo;

    @Autowired
    private PropertyApplicationService service;

    @PostMapping(value = "/ui/applications/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PropertyApplication> createApplicationFromUi(@RequestBody @Valid UiApplicationDto uiApplication) {
        var propertyApplication = Mappers.getMapper(ApplicationMapper.class).uiApplicationToPropertyApplication(uiApplication);
        return createPropertyApplication(propertyApplication);
    }

    @PostMapping(value = "/portal/applications/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PropertyApplication> createApplicationFromPortal(@RequestBody @Valid PortalApplicationDto portalApplication) {
        var propertyApplication = Mappers.getMapper(ApplicationMapper.class).portalApplicationToPropertyApplication(portalApplication);
        return createPropertyApplication(propertyApplication);
    }

    @GetMapping(value = "/applications/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PropertyApplication> getApplicationById(@PathVariable long id) {
        var application = repo.findById(id);
        return application.map(ResponseEntity::ok)
                .orElseThrow(() -> new NoSuchElementException("No property application with id " + id + " exists!"));
    }

    @PutMapping(value = "/applications/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@PathVariable long id, @RequestBody @Valid PropertyApplicationUpdateDto updateDto) {
        var application = repo.findById(id).orElseThrow();
        if (updateDto.getStatus() != null) {
            application.setStatus(updateDto.getStatus());
        }
        if (updateDto.getUserComment() != null) {
            application.setUserComment(updateDto.getUserComment());
        }
        repo.save(application);

        log.info("Updated application with id={}", id);

        return ResponseEntity.noContent()
                .header("Location", buildPropertyApplicationUri(id).toString())
                .build();
    }

    @GetMapping
    @RequestMapping(value = "/applications/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<PropertyApplication>> filterApplications(@RequestParam Map<String, String> filterParameters,
                                                                            @PageableDefault(size = 100)
                                                                            // Sort by newest applications first by default
                                                                            @SortDefault(sort = "creationTimestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        var page = service.filterApplications(filterParameters, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                // For frontend to calculate the total number of pages
                .header("Total-Items", String.valueOf(page.getTotalElements()))
                .body(page.toList());
    }

    // Stays here because it deals with URL/ResponseEntity creation.
    // We *could* move setting the status & creation date into another method in the PropertyApplicationService since
    // that technically is business logic, but that would not really help in terms of readability.
    private ResponseEntity<PropertyApplication> createPropertyApplication(PropertyApplication propertyApplication) {
        propertyApplication.setStatus(Status.CREATED);
        propertyApplication.setCreationTimestamp(Instant.now());
        propertyApplication = repo.save(propertyApplication);

        // We should use the MDC if we wanted log these IDs in more places, but this is ok for now
        log.info("PropertyApplication created with propertyId={}, applicationId={} and source={}",
                propertyApplication.getPropertyId(), propertyApplication.getId(), propertyApplication.getCreationSource().name());

        // Build a URL to the newly created resource
        var url = buildPropertyApplicationUri(propertyApplication.getId());

        return ResponseEntity.created(url).body(propertyApplication);
    }

    private URI buildPropertyApplicationUri(long id) {
        return ServletUriComponentsBuilder.fromCurrentServletMapping()
                .path("api/v1/applications/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
