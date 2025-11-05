package edu.famu.cop3060.resources.controller;

import edu.famu.cop3060.resources.dto.PageEnvelope;
import edu.famu.cop3060.resources.dto.UnitDTO;
import edu.famu.cop3060.resources.dto.request.UnitCreateRequest;
import edu.famu.cop3060.resources.dto.request.UnitUpdateRequest;
import edu.famu.cop3060.resources.service.UnitService;
import jakarta.validation.Valid;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/units")
public class UnitsController {
    private static final Logger log = LoggerFactory.getLogger(UnitsController.class);
    private final UnitService service;
    public UnitsController(UnitService service){ this.service = service; }

    @PostMapping public ResponseEntity<UnitDTO> create(@Valid @RequestBody UnitCreateRequest req){
        var c = service.create(req.name(), req.abbreviation());
        return ResponseEntity.created(java.net.URI.create("/api/units/" + c.id())).body(c);
    }
    @GetMapping public ResponseEntity<PageEnvelope<UnitDTO>> list(@RequestParam(defaultValue="0") int page,
                                                                  @RequestParam(defaultValue="10") int size,
                                                                  @RequestParam(required=false) String sort){
        var env = service.list(page, size, sort);
        log.info("GET /api/units page={} size={} sort={} -> {}", page, size, sort, env.content().size());
        return ResponseEntity.ok(env);
    }
    @GetMapping("/{id}") public ResponseEntity<UnitDTO> get(@PathVariable Long id){ return ResponseEntity.ok(service.get(id)); }
    @PutMapping("/{id}") public ResponseEntity<UnitDTO> update(@PathVariable Long id, @Valid @RequestBody UnitUpdateRequest req){ return ResponseEntity.ok(service.update(id, req.name(), req.abbreviation())); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){ service.delete(id); return ResponseEntity.noContent().build(); }
}
