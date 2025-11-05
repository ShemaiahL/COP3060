package edu.famu.cop3060.resources.dto.response;

import edu.famu.cop3060.resources.dto.ContactDTO;
import edu.famu.cop3060.resources.dto.LocationDTO;
import edu.famu.cop3060.resources.dto.UnitDTO;

public record ResourceResponse(
        Long id,
        String name,
        String description,
        LocationDTO location,
        UnitDTO unit,
        ContactDTO contact
) {}
