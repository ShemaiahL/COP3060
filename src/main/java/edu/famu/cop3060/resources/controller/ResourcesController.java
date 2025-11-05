package edu.famu.cop3060.resources.controller;

import edu.famu.cop3060.resources.dto.PageEnvelope;
import edu.famu.cop3060.resources.dto.request.ResourceCreateRequest;
import edu.famu.cop3060.resources.dto.request.ResourceUpdateRequest;
import edu.famu.cop3060.resources.dto.response.ResourceResponse;
import edu.famu.cop3060.resources.service.ResourceService;
import jakarta.validation.Valid;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resources")
public class ResourcesController {
    private static final Logger log = LoggerFactory.getLogger(ResourcesController.class);
    private final ResourceService service;
    public ResourcesController(ResourceService service){ this.service = service; }

    @PostMapping
    public ResponseEntity<ResourceResponse> create(@Valid @RequestBody ResourceCreateRequest req){
        var created = service.create(req.name(), req.description(), req.locationId(), req.unitId(), req.contactId());
        return ResponseEntity.created(java.net.URI.create("/api/resources/" + created.id())).body(created);
    }

    @GetMapping
    public ResponseEntity<PageEnvelope<ResourceResponse>> list(@RequestParam(defaultValue="0") Integer page,
                                                               @RequestParam(defaultValue="10") Integer size,
                                                               @RequestParam(required=false) String sort,
                                                               @RequestParam(required=false, name="unit") String unit,
                                                               @RequestParam(required=false, name="q") String q){
        var env = service.list(page, size, sort, unit, q);
        log.info("GET /api/resources p={} s={} sort={} unit={} q={} -> {}", page, size, sort, unit, q, env.content().size());
        return ResponseEntity.ok(env);
    }

    @GetMapping("/{id}") public ResponseEntity<ResourceResponse> get(@PathVariable Long id){ return ResponseEntity.ok(service.get(id)); }
    @PutMapping("/{id}") public ResponseEntity<ResourceResponse> update(@PathVariable Long id, @Valid @RequestBody ResourceUpdateRequest req){
        return ResponseEntity.ok(service.update(id, req.name(), req.description(), req.locationId(), req.unitId(), req.contactId()));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){ service.delete(id); return ResponseEntity.noContent().build(); }
}
