package org.air_controller.web_access.graph;

import java.util.List;
import java.util.stream.IntStream;

public class GenericItemReducer {

    public static <T> List<T> reduceTo(List<T> items, int maxNumberOfItems) {
        if (items.size() <= maxNumberOfItems) {
            return items;
        }

        final int reduceByFactorOf = (items.size() + maxNumberOfItems - 1) / maxNumberOfItems;
        final int consistLastItem = (items.size() + 1) % reduceByFactorOf;
        return IntStream.range(0, items.size())
                .filter(index -> (index + consistLastItem) % reduceByFactorOf == 0)
                .mapToObj(items::get)
                .toList();
    }
}
