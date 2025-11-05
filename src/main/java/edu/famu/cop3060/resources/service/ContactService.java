package edu.famu.cop3060.resources.service;

import edu.famu.cop3060.resources.dto.ContactDTO;
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
public class ContactService {
    private final Map<Long, ContactDTO> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    private Function<Long, Long> resourceCountByContact = id -> 0L;
    public void setResourceCountByContact(Function<Long, Long> fn){ this.resourceCountByContact = fn; }

    public ContactDTO create(String fullName, String email, String phone){
        long id = seq.incrementAndGet();
        var dto = new ContactDTO(id, fullName, email, phone);
        store.put(id, dto);
        return dto;
    }

    public PageEnvelope<ContactDTO> list(int page, int size, String sort){
        List<ContactDTO> all = new ArrayList<>(store.values());
        Comparator<ContactDTO> cmp = buildComparator(SortSpec.parse(sort)); if (cmp!=null) all.sort(cmp);
        long total = all.size();
        int from = Math.min(page*size, all.size());
        int to   = Math.min(from+size, all.size());
        return new PageEnvelope<>(all.subList(from, to), page, size, total, (int)Math.ceil(total/(double)size));
    }

    public ContactDTO get(Long id){
        var dto = store.get(id);
        if (dto == null) throw new NotFoundException("contact %d not found".formatted(id));
        return dto;
    }

    public ContactDTO update(Long id, String fullName, String email, String phone){
        get(id);
        var up = new ContactDTO(id, fullName, email, phone);
        store.put(id, up);
        return up;
    }

    public void delete(Long id){
        get(id);
        long count = resourceCountByContact.apply(id);
        if (count > 0) throw new ConflictException("contact %d is in use by %d resources".formatted(id, count));
        store.remove(id);
    }

    private Comparator<ContactDTO> buildComparator(List<SortSpec.Order> orders){
        if (orders.isEmpty()) return null;
        Comparator<ContactDTO> cmp = null;
        for (var o : orders) {
            Comparator<ContactDTO> c = switch (o.field()) {
                case "fullName" -> Comparator.comparing(ContactDTO::fullName, Comparator.nullsLast(String::compareToIgnoreCase));
                case "email"    -> Comparator.comparing(ContactDTO::email, Comparator.nullsLast(String::compareToIgnoreCase));
                case "id"       -> Comparator.comparing(ContactDTO::id, Comparator.nullsLast(Long::compareTo));
                default         -> Comparator.comparing(ContactDTO::fullName, Comparator.nullsLast(String::compareToIgnoreCase));
            };
            if (!o.asc()) c = c.reversed();
            cmp = (cmp==null) ? c : cmp.thenComparing(c);
        }
        return cmp;
    }

    public void seed(List<ContactDTO> seeds){
        for (var s : seeds) {
            long id = seq.incrementAndGet();
            store.put(id, new ContactDTO(id, s.fullName(), s.email(), s.phone()));
        }
    }
}
