package edu.famu.cop3060.resources.controller;

import edu.famu.cop3060.resources.dto.ResourceDTO;
import edu.famu.cop3060.resources.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/resources")
public class ResourcesController {
    private static final Logger log = LoggerFactory.getLogger(ResourcesController.class);
    private final ResourceService service;

    public ResourcesController(ResourceService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ResourceDTO>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q
    ) {
        log.info("GET /api/resources category={} q={}",
                category == null ? "-" : category, q == null ? "-" : q);
        List<ResourceDTO> results = service.list(Optional.ofNullable(category), Optional.ofNullable(q));
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceDTO> getOne(@PathVariable String id) {
        log.info("GET /api/resources/{}", id);
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
