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

        return IntStream.range(0, items.size())
                .filter(index -> index == 0 || !isSimilarToPreviousItem(items, index, smallestDiff * DIFFERENCE_INCREASE_FACTOR))
                .mapToObj(items::get)
                .toList();
    }

    private Double calculateSmallestDiffToNextValue(List<GraphItem> items) {
        return IntStream.range(1, items.size())
                .filter(index -> itemsHavingSameStates(items.get(index - 1), items.get(index)))
                .mapToObj(index -> calculateDiff(items.get(index - 1).value(), items.get(index).value()))
                .min(Double::compareTo).orElse(Double.MAX_VALUE);
    }

    private boolean isSimilarToPreviousItem(List<GraphItem> items, int index, double difference) {
        final GraphItem previousItem = items.get(index - 1);
        final GraphItem currentItem = items.get(index);
        final boolean sameItemState = itemsHavingSameStates(previousItem, currentItem);
        final boolean differenceWithinRange = itemDifferenceWithinRange(previousItem, currentItem, difference);
        return sameItemState && differenceWithinRange;
    }

    private boolean itemsHavingSameStates(GraphItem previousItem, GraphItem currentItem) {
        return currentItem.dataPointColor().equals(previousItem.dataPointColor());
    }

    private boolean itemDifferenceWithinRange(GraphItem previousItem, GraphItem currentItem, double difference) {
        return calculateDiff(currentItem.value(), previousItem.value()) < difference;
    }

    private double calculateDiff(double numberA, double numberB) {
        return Math.abs(numberA - numberB);
    }
}
