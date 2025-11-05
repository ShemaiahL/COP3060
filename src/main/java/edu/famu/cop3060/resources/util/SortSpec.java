package edu.famu.cop3060.resources.util;

import java.util.ArrayList;
import java.util.List;

public class SortSpec {
    public record Order(String field, boolean asc) {}

    public static List<Order> parse(String sortParam) {
        List<Order> orders = new ArrayList<>();
        if (sortParam == null || sortParam.isBlank()) return orders;
        for (String token : sortParam.split(",")) {
            token = token.trim();
            if (token.isEmpty()) continue;
            boolean asc = true;
            if (token.startsWith("-")) { asc = false; token = token.substring(1); }
            orders.add(new Order(token, asc));
        }
        return orders;
    }
}