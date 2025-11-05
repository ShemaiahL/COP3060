package edu.famu.cop3060.resources.service;

import edu.famu.cop3060.resources.dto.PageEnvelope;
import edu.famu.cop3060.resources.dto.UnitDTO;
import edu.famu.cop3060.resources.exception.ConflictException;
import edu.famu.cop3060.resources.exception.NotFoundException;
import edu.famu.cop3060.resources.util.SortSpec;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@Service
public class UnitService {
    private final Map<Long, UnitDTO> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    private Function<Long, Long> resourceCountByUnit = id -> 0L;
    public void setResourceCountByUnit(Function<Long, Long> fn){ this.resourceCountByUnit = fn; }

    public UnitDTO create(String name, String abbreviation){
        long id = seq.incrementAndGet();
        var dto = new UnitDTO(id, name, abbreviation);
        store.put(id, dto);
        return dto;
    }

    public PageEnvelope<UnitDTO> list(int page, int size, String sort){
        List<UnitDTO> all = new ArrayList<>(store.values());
        Comparator<UnitDTO> cmp = buildComparator(SortSpec.parse(sort)); if (cmp!=null) all.sort(cmp);
        long total = all.size();
        int from = Math.min(page*size, all.size());
        int to   = Math.min(from+size, all.size());
        return new PageEnvelope<>(all.subList(from, to), page, size, total, (int)Math.ceil(total/(double)size));
    }

    public UnitDTO get(Long id){
        var dto = store.get(id);
        if (dto == null) throw new NotFoundException("unit %d not found".formatted(id));
        return dto;
    }

    public UnitDTO update(Long id, String name, String abbreviation){
        get(id);
        var up = new UnitDTO(id, name, abbreviation);
        store.put(id, up);
        return up;
    }

    public void delete(Long id){
        get(id);
        long count = resourceCountByUnit.apply(id);
        if (count > 0) throw new ConflictException("unit %d is in use by %d resources".formatted(id, count));
        store.remove(id);
    }

    private Comparator<UnitDTO> buildComparator(List<SortSpec.Order> orders){
        if (orders.isEmpty()) return null;
        Comparator<UnitDTO> cmp = null;
        for (var o : orders) {
            Comparator<UnitDTO> c = switch (o.field()) {
                case "name"         -> Comparator.comparing(UnitDTO::name, Comparator.nullsLast(String::compareToIgnoreCase));
                case "abbreviation" -> Comparator.comparing(UnitDTO::abbreviation, Comparator.nullsLast(String::compareToIgnoreCase));
                case "id"           -> Comparator.comparing(UnitDTO::id, Comparator.nullsLast(Long::compareTo));
                default             -> Comparator.comparing(UnitDTO::name, Comparator.nullsLast(String::compareToIgnoreCase));
            };
            if (!o.asc()) c = c.reversed();
            cmp = (cmp==null) ? c : cmp.thenComparing(c);
        }
        return cmp;
    }

    public void seed(List<UnitDTO> seeds){
        for (var s : seeds) {
            long id = seq.incrementAndGet();
            store.put(id, new UnitDTO(id, s.name(), s.abbreviation()));
        }
    }
}
