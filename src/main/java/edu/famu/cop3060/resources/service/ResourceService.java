package edu.famu.cop3060.resources.service;

import edu.famu.cop3060.resources.dto.*;
import edu.famu.cop3060.resources.dto.response.ResourceResponse;
import edu.famu.cop3060.resources.exception.NotFoundException;
import edu.famu.cop3060.resources.util.SortSpec;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ResourceService {
    private final Map<Long, ResourceDTO> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    private final LocationService locationService;
    private final UnitService unitService;
    private final ContactService contactService;

    public ResourceService(LocationService locationService, UnitService unitService, ContactService contactService) {
        this.locationService = locationService;
        this.unitService = unitService;
        this.contactService = contactService;
        locationService.setResourceCountByLocation(this::countByLocationId);
        unitService.setResourceCountByUnit(this::countByUnitId);
        contactService.setResourceCountByContact(this::countByContactId);
    }

    public ResourceResponse create(String name, String description, Long locationId, Long unitId, Long contactId) {
        var loc = locationService.get(locationId);
        var unit = unitService.get(unitId);
        var contact = contactService.get(contactId);
        long id = seq.incrementAndGet();
        var dto = new ResourceDTO(id, name, description, locationId, unitId, contactId);
        store.put(id, dto);
        return expand(dto, loc, unit, contact);
    }

    public PageEnvelope<ResourceResponse> list(Integer page, Integer size, String sort, String unitFilter, String q) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0) ? 10 : size;
        List<ResourceDTO> all = new ArrayList<>(store.values());

        if (unitFilter != null) {
            try {
                long uid = Long.parseLong(unitFilter);
                all.removeIf(r -> !Objects.equals(r.unitId(), uid));
            } catch (NumberFormatException ignored) {}
        }

        if (q != null && !q.isBlank()) {
            String needle = q.toLowerCase();
            all.removeIf(r ->
                    (r.name() == null || !r.name().toLowerCase().contains(needle)) &&
                            (r.description() == null || !r.description().toLowerCase().contains(needle)));
        }

        Comparator<ResourceDTO> cmp = buildComparator(SortSpec.parse(sort));
        if (cmp != null) all.sort(cmp);

        long total = all.size();
        int from = Math.min(p * s, all.size());
        int to = Math.min(from + s, all.size());
        List<ResourceDTO> slice = all.subList(from, to);
        List<ResourceResponse> content = slice.stream().map(this::expand).toList();
        return new PageEnvelope<>(content, p, s, total, (int) Math.ceil(total / (double) s));
    }

    public ResourceResponse get(Long id) {
        var dto = store.get(id);
        if (dto == null) throw new NotFoundException("resource %d not found".formatted(id));
        return expand(dto);
    }

    public ResourceResponse update(Long id, String name, String description, Long locationId, Long unitId, Long contactId) {
        if (!store.containsKey(id)) throw new NotFoundException("resource %d not found".formatted(id));
        var loc = locationService.get(locationId);
        var unit = unitService.get(unitId);
        var contact = contactService.get(contactId);
        var up = new ResourceDTO(id, name, description, locationId, unitId, contactId);
        store.put(id, up);
        return expand(up, loc, unit, contact);
    }

    public void delete(Long id) {
        if (!store.containsKey(id)) throw new NotFoundException("resource %d not found".formatted(id));
        store.remove(id);
    }

    // helpers
    private ResourceResponse expand(ResourceDTO r) {
        var loc = locationService.get(r.locationId());
        var unit = unitService.get(r.unitId());
        var contact = contactService.get(r.contactId());
        return expand(r, loc, unit, contact);
    }
    private ResourceResponse expand(ResourceDTO r, LocationDTO loc, UnitDTO unit, ContactDTO contact) {
        return new ResourceResponse(r.id(), r.name(), r.description(), loc, unit, contact);
    }
    private long countByLocationId(Long id){ return store.values().stream().filter(r -> Objects.equals(r.locationId(), id)).count(); }
    private long countByUnitId(Long id){ return store.values().stream().filter(r -> Objects.equals(r.unitId(), id)).count(); }
    private long countByContactId(Long id){ return store.values().stream().filter(r -> Objects.equals(r.contactId(), id)).count(); }

    private Comparator<ResourceDTO> buildComparator(List<SortSpec.Order> orders) {
        if (orders.isEmpty()) return null;
        Comparator<ResourceDTO> cmp = null;
        for (var o : orders) {
            Comparator<ResourceDTO> c = switch (o.field()) {
                case "name" -> Comparator.comparing(ResourceDTO::name, Comparator.nullsLast(String::compareToIgnoreCase));
                case "unit" -> Comparator.comparing(ResourceDTO::unitId, Comparator.nullsLast(Long::compareTo));
                case "location" -> Comparator.comparing(ResourceDTO::locationId, Comparator.nullsLast(Long::compareTo));
                case "contact" -> Comparator.comparing(ResourceDTO::contactId, Comparator.nullsLast(Long::compareTo));
                case "id" -> Comparator.comparing(ResourceDTO::id, Comparator.nullsLast(Long::compareTo));
                default -> Comparator.comparing(ResourceDTO::name, Comparator.nullsLast(String::compareToIgnoreCase));
            };
            if (!o.asc()) c = c.reversed();
            cmp = (cmp == null) ? c : cmp.thenComparing(c);
        }
        return cmp;
    }
}
