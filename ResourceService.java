package edu.famu.cop3060.resources.service;

import edu.famu.cop3060.resources.dto.ResourceDTO;
import edu.famu.cop3060.resources.store.InMemoryResourceStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {
    private final InMemoryResourceStore store;

    public ResourceService(InMemoryResourceStore store) {
        this.store = store;
    }

    public List<ResourceDTO> list(Optional<String> category, Optional<String> q) {
        if (category.isPresent() || q.isPresent()) {
            return store.findByFilters(category, q);
        }
        return store.findAll();
    }

    public Optional<ResourceDTO> getById(String id) {
        return store.findById(id);
    }
}
