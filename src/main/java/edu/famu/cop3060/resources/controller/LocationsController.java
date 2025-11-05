package edu.famu.cop3060.resources.controller;

import edu.famu.cop3060.resources.dto.LocationDTO;
import edu.famu.cop3060.resources.dto.PageEnvelope;
import edu.famu.cop3060.resources.dto.request.LocationCreateRequest;
import edu.famu.cop3060.resources.dto.request.LocationUpdateRequest;
import edu.famu.cop3060.resources.service.LocationService;
import jakarta.validation.Valid;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
public class LocationsController {
    private static final Logger log = LoggerFactory.getLogger(LocationsController.class);
    private final LocationService service;
    public LocationsController(LocationService service){ this.service = service; }

    @PostMapping
    public ResponseEntity<LocationDTO> create(@Valid @RequestBody LocationCreateRequest req){
        var created = service.create(req.building(), req.room(), req.notes());
        return ResponseEntity.created(java.net.URI.create("/api/locations/" + created.id())).body(created);
    }

    @GetMapping
    public ResponseEntity<PageEnvelope<LocationDTO>> list(@RequestParam(defaultValue="0") int page,
                                                          @RequestParam(defaultValue="10") int size,
                                                          @RequestParam(required=false) String sort){
        var env = service.list(page, size, sort);
        log.info("GET /api/locations page={} size={} sort={} -> {}", page, size, sort, env.content().size());
        return ResponseEntity.ok(env);
    }

    @GetMapping("/{id}") public ResponseEntity<LocationDTO> get(@PathVariable Long id){ return ResponseEntity.ok(service.get(id)); }
    @PutMapping("/{id}") public ResponseEntity<LocationDTO> update(@PathVariable Long id, @Valid @RequestBody LocationUpdateRequest req){ return ResponseEntity.ok(service.update(id, req.building(), req.room(), req.notes())); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){ service.delete(id); return ResponseEntity.noContent().build(); }
}
