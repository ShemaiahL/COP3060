package edu.famu.cop3060.resources.controller;

import edu.famu.cop3060.resources.dto.ContactDTO;
import edu.famu.cop3060.resources.dto.PageEnvelope;
import edu.famu.cop3060.resources.dto.request.ContactCreateRequest;
import edu.famu.cop3060.resources.dto.request.ContactUpdateRequest;
import edu.famu.cop3060.resources.service.ContactService;
import jakarta.validation.Valid;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
public class ContactsController {
    private static final Logger log = LoggerFactory.getLogger(ContactsController.class);
    private final ContactService service;
    public ContactsController(ContactService service){ this.service = service; }

    @PostMapping public ResponseEntity<ContactDTO> create(@Valid @RequestBody ContactCreateRequest req){
        var c = service.create(req.fullName(), req.email(), req.phone());
        return ResponseEntity.created(java.net.URI.create("/api/contacts/" + c.id())).body(c);
    }
    @GetMapping public ResponseEntity<PageEnvelope<ContactDTO>> list(@RequestParam(defaultValue="0") int page,
                                                                     @RequestParam(defaultValue="10") int size,
                                                                     @RequestParam(required=false) String sort){
        var env = service.list(page, size, sort);
        log.info("GET /api/contacts page={} size={} sort={} -> {}", page, size, sort, env.content().size());
        return ResponseEntity.ok(env);
    }
    @GetMapping("/{id}") public ResponseEntity<ContactDTO> get(@PathVariable Long id){ return ResponseEntity.ok(service.get(id)); }
    @PutMapping("/{id}") public ResponseEntity<ContactDTO> update(@PathVariable Long id, @Valid @RequestBody ContactUpdateRequest req){ return ResponseEntity.ok(service.update(id, req.fullName(), req.email(), req.phone())); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){ service.delete(id); return ResponseEntity.noContent().build(); }
}
