/*******************************************************************************
 * Copyright (c) 2019, 2026 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package fr.obeo.playground.restfulemf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores model values in a rectangular table whose first row is the header.
 */
public class SheetDataTable {

    private static final String EMPTY_VALUE = "";

    private final List<List<String>> values = new ArrayList<>();

    private final Map<String, List<String>> rows = new LinkedHashMap<>();

    public SheetDataTable() {
        this.values.add(new ArrayList<>(List.of("id")));
    }

    public void updateValue(String key, String columnName, String value) {
        List<String> headers = this.values.get(0);
        int columnIndex = headers.indexOf(columnName);
        if (columnIndex == -1) {
            columnIndex = headers.size();
            headers.add(columnName);
        }

        List<String> row = this.rows.computeIfAbsent(key, rowKey -> {
            var newRow = new ArrayList<String>();
            newRow.add(rowKey);
            this.values.add(newRow);
            return newRow;
        });
        while (row.size() <= columnIndex) {
            row.add(EMPTY_VALUE);
        }
        row.set(columnIndex, value);
    }

    public List<List<String>> getValues() {
        return this.values;
    }

    public void fillEmptyCells() {
        int columnCount = this.values.get(0).size();
        this.values.stream().skip(1).forEach(row -> {
            while (row.size() < columnCount) {
                row.add(EMPTY_VALUE);
            }
        });
    }
}
