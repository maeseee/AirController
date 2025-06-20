package org.air_controller.persistence;

import java.sql.ResultSet;
import java.util.List;

@FunctionalInterface
public interface EntryAdder<T> {
    void addResultIfAvailable(List<T> entries, ResultSet resultSet);
}
