package edu.famu.cop3060.resources.service;

import edu.famu.cop3060.resources.dto.LocationDTO;
import edu.famu.cop3060.resources.dto.PageEnvelope;
import edu.famu.cop3060.resources.exception.ConflictException;
import edu.famu.cop3060.resources.exception.NotFoundException;
import edu.famu.cop3060.resources.util.SortSpec;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@Service
public class LocationService {
    private final Map<Long, LocationDTO> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    private Function<Long, Long> resourceCountByLocation = id -> 0L;
    public void setResourceCountByLocation(Function<Long, Long> fn){ this.resourceCountByLocation = fn; }

    public LocationDTO create(String building, String room, String notes){
        long id = seq.incrementAndGet();
        var dto = new LocationDTO(id, building, room, notes);
        store.put(id, dto);
        return dto;
    }

    public PageEnvelope<LocationDTO> list(int page, int size, String sort){
        List<LocationDTO> all = new ArrayList<>(store.values());
        Comparator<LocationDTO> cmp = buildComparator(SortSpec.parse(sort)); if (cmp!=null) all.sort(cmp);
        long total = all.size();
        int from = Math.min(page*size, all.size());
        int to   = Math.min(from+size, all.size());
        return new PageEnvelope<>(all.subList(from, to), page, size, total, (int)Math.ceil(total/(double)size));
    }

    public LocationDTO get(Long id){
        var dto = store.get(id);
        if (dto == null) throw new NotFoundException("location %d not found".formatted(id));
        return dto;
    }

    public LocationDTO update(Long id, String building, String room, String notes){
        get(id);
        var up = new LocationDTO(id, building, room, notes);
        store.put(id, up);
        return up;
    }

    public void delete(Long id){
        get(id);
        long count = resourceCountByLocation.apply(id);
        if (count > 0) throw new ConflictException("location %d is in use by %d resources".formatted(id, count));
        store.remove(id);
    }

    private Comparator<LocationDTO> buildComparator(List<SortSpec.Order> orders){
        if (orders.isEmpty()) return null;
        Comparator<LocationDTO> cmp = null;
        for (var o : orders) {
            Comparator<LocationDTO> c = switch (o.field()) {
                case "building" -> Comparator.comparing(LocationDTO::building, Comparator.nullsLast(String::compareToIgnoreCase));
                case "room"     -> Comparator.comparing(LocationDTO::room,     Comparator.nullsLast(String::compareToIgnoreCase));
                case "id"       -> Comparator.comparing(LocationDTO::id,       Comparator.nullsLast(Long::compareTo));
                default         -> Comparator.comparing(LocationDTO::building, Comparator.nullsLast(String::compareToIgnoreCase));
            };
            if (!o.asc()) c = c.reversed();
            cmp = (cmp==null) ? c : cmp.thenComparing(c);
        }
        return cmp;
    }

    public void seed(List<LocationDTO> seeds){
        for (var s : seeds) {
            long id = seq.incrementAndGet();
            store.put(id, new LocationDTO(id, s.building(), s.room(), s.notes()));
        }
    }
}
