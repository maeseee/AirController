package org.air_controller.web_access.graph;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class DynamicItemReducer {
    public static final double DIFFERENCE_INCREASE_FACTOR = 1.1;
    private final double maxNrOfItems;

    public List<GraphItem> reduce(List<GraphItem> items) {
        while (items.size() > maxNrOfItems) {
            items = reduceItems(items);
        }
        return items;
    }

    private List<GraphItem> reduceItems(List<GraphItem> items) {
        final double smallestDiff = calculateSmallestDiffToNextValue(items);

        return IntStream.range(1, items.size() - 1)
                .filter(index -> isSimilarToPreviousAndNextItem(items, index, smallestDiff))
                .mapToObj(items::get)
                .toList();
    }

    private Double calculateSmallestDiffToNextValue(List<GraphItem> items) {
        return IntStream.range(1, items.size() - 1)
                .filter(index -> itemsHavingSameStates(items.get(index - 1), items.get(index)))
                .mapToObj(index -> calculateDiff(items.get(index).value(), items.get(index + 1).value()))
                .min(Double::compareTo).orElse(Double.MAX_VALUE);
    }

    private boolean isSimilarToPreviousAndNextItem(List<GraphItem> items, int index, double smallestDiff) {
        final GraphItem previousItem = items.get(index - 1);
        final GraphItem currentItem = items.get(index);
        return itemsHavingSameStates(previousItem, currentItem) &&
                itemsInDifferenceRange(previousItem, currentItem, smallestDiff * DIFFERENCE_INCREASE_FACTOR);
    }

    private boolean itemsHavingSameStates(GraphItem previousItem, GraphItem currentItem) {
        return currentItem.dataPointColor().equals(previousItem.dataPointColor());
    }

    private boolean itemsInDifferenceRange(GraphItem previousItem, GraphItem currentItem, double difference) {
        return calculateDiff(currentItem.value(), previousItem.value()) < difference;
    }

    private double calculateDiff(double numberA, double numberB) {
        return Math.abs(numberA - numberB);
    }
}
