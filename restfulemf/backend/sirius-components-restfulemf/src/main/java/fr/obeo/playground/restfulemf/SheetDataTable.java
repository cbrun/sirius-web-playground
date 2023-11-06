package fr.obeo.playground.restfulemf;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class holding values and providing table-like accessors. Consider the first
 * line is a header line and the first column is a key identifying a value line.
 * 
 * @author Cedric Brun <cedric.brun@obeo.fr>
 */
public class SheetDataTable {

	private static final String EMPTY_VALUE = ""; //$NON-NLS-1$

	private List<List<Object>> values = Lists.newArrayList();

	private Map<String, List<Object>> rows = Maps.newLinkedHashMap();

	public SheetDataTable() {
		List<Object> headersLine = Lists.newArrayList("id"); //$NON-NLS-1$
		// we consider the first column name is set by default and is called "id".
		this.values.add(headersLine);
	}

	/**
	 * Updates a table value.
	 * 
	 * @param key        : the row identification
	 * @param columnName : the column name to update
	 * @param value      : the value to set for the row and given column.
	 */
	public void updateValue(String key, String columnName, String value) {
		int columnIndex = -1;
		boolean foundColumn = false;
		if (this.values.size() > 0) {
			for (Object cols : this.values.get(0)) {
				columnIndex++;
				if (cols != null && columnName.equals(cols.toString())) {
					foundColumn = true;
					break;
				}
			}
			if (!foundColumn) {
				columnIndex = this.values.get(0).size();
				this.values.get(0).add(columnName);
			} else {
				if (columnIndex == this.values.get(0).size()) {
					this.values.get(0).add(columnName);
				} else {
					this.values.get(0).set(columnIndex, columnName);
				}
			}
		} else {
			List<Object> newHeader = new ArrayList<>();
			newHeader.add(EMPTY_VALUE);
			newHeader.add(columnName);
			columnIndex = 1;
			this.values.add(newHeader);
		}
		List<Object> row = this.rows.get(key);
		if (row != null) {
			for (int i = row.size(); i < columnIndex + 1; i++) {
				row.add(EMPTY_VALUE);
			}
			row.set(columnIndex, value);
		} else {
			List<Object> newRow = Lists.newArrayList();
			this.rows.put(key, newRow);
			newRow.add(key);
			for (int i = newRow.size(); i < columnIndex + 1; i++) {
				newRow.add(EMPTY_VALUE);
			}
			newRow.set(columnIndex, value);
			this.values.add(newRow);
		}

	}

	/**
	 * Return the data content as a list of rows, each row being a list of values.
	 */
	public List<List<Object>> getValues() {
		return this.values;
	}

	/**
	 * Process the table to explicitly fill non valued cell with the EMPTY_VALUE
	 * constant.
	 */
	public void fillEmptyCells() {
		int nbColumns = this.values.get(0).size();
		for (int i = 1; i < this.values.size(); i++) {
			List<Object> row = this.values.get(i);
			while (row.size() < nbColumns) {
				row.add(EMPTY_VALUE);
			}
		}
	}
}
