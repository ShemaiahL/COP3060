package edu.famu.cop3060.resources.store;

import edu.famu.cop3060.resources.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryResourceStore {
    private static final Logger log = LoggerFactory.getLogger(InMemoryResourceStore.class);

    private final Map<String, ResourceDTO> byId = new HashMap<>();
    private final List<ResourceDTO> all = new ArrayList<>();

    public InMemoryResourceStore() {
        seed();
        log.info("Seeded {} resources in memory", all.size());
    }

    private void seed() {
        List<ResourceDTO> seed = List.of(
                new ResourceDTO("tutoring-ace","ACE Tutoring Center","Tutoring",
                        "Science Building 120","https://example.edu/ace", List.of("math","cs","drop-in")),
                new ResourceDTO("advising-cis","CIS Advising","Advising",
                        "CIS Complex 200","https://example.edu/cis-advising", List.of("appointments","registration")),
                new ResourceDTO("lab-open","Open Computing Lab","Lab",
                        "Library 3rd Floor","https://example.edu/open-lab", List.of("printing","windows","linux")),
                new ResourceDTO("writing-center","Writing Resource Center","Tutoring",
                        "Liberal Arts 110","https://example.edu/wrc", List.of("essays","mla","apa")),
                new ResourceDTO("career-center","Career & Internship Center","Advising",
                        "Student Success 1st Floor","https://example.edu/career", List.of("resume","interview","jobs")),
                new ResourceDTO("lab-makerspace","Makerspace Lab","Lab",
                        "Innovation Hub","https://example.edu/makerspace", List.of("3d-printing","arduino","workshop")),
                new ResourceDTO("cs-peer","CS Peer-Led Sessions","Tutoring",
                        "CIS Complex 101","https://example.edu/cs-peer", List.of("java","python","data-structures"))
        );
        for (ResourceDTO r : seed) {
            byId.put(r.id(), r);
            all.add(r);
        }
    }

    /** Unmodifiable list of all resources. */
    public List<ResourceDTO> findAll() {
        return List.copyOf(all);
    }

    /** Lookup by id. */
    public Optional<ResourceDTO> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    /** Filter by optional category and q (name or tags, case-insensitive). */
    public List<ResourceDTO> findByFilters(Optional<String> category, Optional<String> q) {
        return all.stream()
                .filter(r -> category.map(c -> r.category().equalsIgnoreCase(c)).orElse(true))
                .filter(r -> q.map(s -> {
                    String needle = s.toLowerCase(Locale.ROOT);
                    boolean inName = r.name().toLowerCase(Locale.ROOT).contains(needle);
                    boolean inTags = r.tags() != null && r.tags().stream()
                            .anyMatch(t -> t.toLowerCase(Locale.ROOT).contains(needle));
                    return inName || inTags;
                }).orElse(true))
                .toList();
    }
}
