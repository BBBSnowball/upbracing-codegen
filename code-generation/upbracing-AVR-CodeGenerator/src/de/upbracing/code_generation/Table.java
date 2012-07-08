package de.upbracing.code_generation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for the templates: Generates a tabular layout in the generated code
 * 
 * @author benny
 */
public class Table {
	private static final String COLUMN_OPTION_REGEX = "(<?-?[0-9]*-?>?)";
	private static final String LINE_OPTION_REGEX = "([A-Za-z_]<?-?[0-9]*-?>?)";
	private static final String TABLE_OPTION_REGEX = "(?::((?:,?[a-zA-Z]:[-<>lcr]|[-<>lcr])*):)";
	private static final String NL_REGEX = "\n";
	private static final Pattern COLUMN_OPTION_REGEX2 = Pattern.compile("^(<?-?)([0-9]*)(-?>?)");
	
	private StringBuffer sb;
	private Pattern table_options_regex;
	private Pattern line_regex;
	private Pattern seperator_regex;
	private boolean seperator_with_options;
	private int index = -1;
	private boolean spaces_at_end_of_line;
	
	/**
	 * constructor
	 * 
	 * The seperator_regex may contain the special string '%OPTS%' which matches column or line options. If it does, it mustn't
	 * contain any capturing groups. Use '(?:...)' instead of '(...)' for grouping.
	 * 
	 * @param sb stringBuffer object of the template
	 * @param seperator_regex column seperator used in the template; a regular expression (see {@link java.util.regex.Pattern})
	 * @param spaces_at_end_of_line If true, padding spaces are also used for the last column in each row.
	 */
	public Table(StringBuffer sb, String seperator_regex,
			boolean spaces_at_end_of_line) {
		this.sb = sb;
		this.spaces_at_end_of_line = spaces_at_end_of_line;

		if (seperator_regex.contains("%OPT%")) {
			seperator_with_options = true;
			table_options_regex = Pattern.compile("^\\s*(?:" + seperator_regex.replace("%OPT%", TABLE_OPTION_REGEX) + ")");
			line_regex = Pattern.compile(NL_REGEX + "(?:\\s*(?:" + seperator_regex.replace("%OPT%", LINE_OPTION_REGEX) + ")|)");
			seperator_regex = seperator_regex.replace("%OPT%", COLUMN_OPTION_REGEX);
		} else {
			seperator_with_options = false;
			line_regex = Pattern.compile(NL_REGEX);
		}
		this.seperator_regex = Pattern.compile(seperator_regex);
	}

	/**
	 * constructor
	 * @param sb stringBuffer object of the template
	 * @param seperator_regex column seperator used in the template
	 */
	public Table(StringBuffer sb, String seperator_regex) {
		this(sb, seperator_regex, false);
	}

	/**
	 * constructor
	 * @param sb stringBuffer object of the template
	 */
	public Table(StringBuffer sb) {
		this(sb, "&&&");
	}

	/**
	 * Start a table.
	 * 
	 * Every character put into the buffer from now on will be part of the table.
	 * You mustn't call start() again, before you have called finish(...).
	 */
	public void start() {
		if (index >= 0)
			throw new IllegalStateException("start() has already been called.");
		index = sb.length();
	}
	
	/**
	 * Format the table now.
	 * 
	 * You can use start() to begin the next table or use the stringBuffer as before the
	 * call to start().
	 * 
	 * @param col_seperator seperator to put between columns
	 */
	public void finish(String col_seperator) {
		if (index < 0)
			throw new IllegalStateException("You must call start() before you call finish()!");
		
		String code = sb.substring(index);
		sb.setLength(index);
		index = -1;
		
		Columns columns = new Columns();
		columns.parse(code);
		columns.calculateColumnWidths();
		columns.appendTo(sb, col_seperator);
	}

	/**
	 * Format the table now.
	 * 
	 * You can use start() to begin the next table or use the stringBuffer as before the
	 * call to start(). There won't be any seperator between the columns.
	 */
	public void finish() {
		finish("");
	}
	
	
	private class Columns {
		private TreeMap<Character, ArrayList<Column>> column_map;
		private ArrayList<Column> columns;
		private Matcher line_matcher, seperator_matcher;
		private String code;
		private ArrayList<ArrayList<Cell>> rows;
		private ArrayList<Cell> row;
		private int column_index;
		
		public void parse(String code) {
			column_map = new TreeMap<Character, ArrayList<Column>>();
			columns = new ArrayList<Column>();
			column_map.put('a', columns);
			rows = new ArrayList<ArrayList<Cell>>();
			
			int start_index = 0;
			if (table_options_regex != null) {
				Matcher m = table_options_regex.matcher(code);
				if (m.lookingAt()) {
					String options = m.group(1);
					start_index = m.end();
					
					for (int i=0;i<options.length();i++) {
						if (options.charAt(i) == ',')
							continue;
						else if (i+1 < options.length() && options.charAt(i+1)==':') {
							char name = options.charAt(i);
							if (!column_map.containsKey(name))
								column_map.put(name, new ArrayList<Column>());
							columns = column_map.get(name);
							++i;
						} else
							columns.add(new Column(options.charAt(i)));
					}
					if (code.length() > m.end() && code.charAt(m.end()) != '\n')
						throw new IllegalStateException("Table options must be followed by a newline");
				}
			}

			this.code = code = "\n" + code;

			line_matcher = line_regex.matcher(code);
			seperator_matcher = seperator_regex.matcher(code);
			
			initOptions();
			
			line_matcher.find(start_index);
			if (seperator_with_options && line_matcher.group(1) != null)
				handleLineOptions(line_matcher.group(1));
			start_index = line_matcher.end();
			
			while (line_matcher.find()) {
				handleLine(start_index, line_matcher.start());
				start_index = line_matcher.end();
			}
			
			handleLine(start_index, code.length());
		}

		private void handleLine(int start_index, int end_index) {
			column_index = 0;
			row = new ArrayList<Table.Cell>();
			rows.add(row);
			
			if (start_index < end_index) {
				seperator_matcher.region(start_index, end_index);
				while (seperator_matcher.find()) {
					handleColumn(code.substring(start_index, seperator_matcher.start()));
					if (seperator_with_options && seperator_matcher.group(1) != null)
						handleColumnOptions(seperator_matcher.group(1));
					else
						handleColumnNoOptions();
					
					start_index = seperator_matcher.end();
				}
				handleColumn(code.substring(start_index, end_index));
			}
			
			handleColumnNoOptions();

			if (seperator_with_options && !line_matcher.hitEnd() && line_matcher.group(1) != null)
				handleLineOptions(line_matcher.group(1));
		}
		
		private char alignment;
		private int col_span;

		private void initOptions() {
			handleColumnNoOptions();
		}

		private void handleLineOptions(String group) {
			char colset_name = group.charAt(0);
			if (colset_name != '_') {
				columns = column_map.get(colset_name);
				if (columns == null)
					throw new IllegalStateException("invalid column set name: " + group.charAt(0));
			}
			
			handleColumnOptions(group.substring(1));
		}

		private void handleColumnOptions(String group) {
			Matcher m = COLUMN_OPTION_REGEX2.matcher(group);
			if (!m.matches())
				throw new RuntimeException("shouldn't happen -> error in the program");
			
			String align_str = m.group(1) + m.group(3);
			String span_str = m.group(2);
			
			if (span_str.length() > 0)
				col_span = Integer.parseInt(span_str);
			
			if (col_span == 0)
				return;
			
			if (align_str.length() == 0)
				alignment = ' ';
			else if (align_str.length() == 1)
				alignment = align_str.charAt(0);
			else
				throw new IllegalStateException("found more than one alignment character in: " + group);
		}

		private void handleColumnNoOptions() {
			alignment = ' ';
			col_span = 1;
		}

		private void handleColumn(String contents) {
			if (col_span <= 0)
				return;
			
			while (column_index+col_span > columns.size()) {
				if (alignment == ' ')
					alignment = '<';
				columns.add(new Column(alignment));
			}
			
			Column col = columns.get(column_index);
			
			if (alignment == ' ')
				alignment = col.getAlignment();
			
			Cell cell;
			if (col_span == 1) {
				cell = new SimpleCell(contents, alignment, col);
				col.add(cell);
			} else {
				SpanningCell cell2 = new SpanningCell(contents, alignment);
				for (int i=0;i<col_span;i++) {
					Column c = columns.get(column_index+i);
					cell2.addColumn(c);
					c.add(cell2);
				}
				cell = cell2;
			}
			
			row.add(cell);
			
			column_index += col_span;
		}

		public void calculateColumnWidths() {
			LinkedList<Column> todo = new LinkedList<Table.Column>(columns);
			Iterator<Column> it = todo.iterator();
			while (it.hasNext()) {
				Column col = it.next();
				col.calculateMinWidth();
				if (col.isWeightValid())
					// remove this column, as 
					it.remove();
			}
			
			while (!todo.isEmpty()) {
				float max_weight = Float.NEGATIVE_INFINITY;
				Column max_col = null;
				
				int todo_count = todo.size();
				it = todo.iterator();
				while (it.hasNext()) {
					Column col = it.next();
					int weight = col.getSpanningWeight(todo_count);
					if (weight <= 0)
						it.remove();
					else if (weight > max_weight) {
						max_weight = weight;
						max_col = col;
					}
				}
				
				if (max_col != null)
					max_col.increaseWidth();
			}
		}
		
		public void appendTo(StringBuffer sb, String col_seperator) {
			boolean first = true;
			for (ArrayList<Cell> row : rows) {
				if (first)
					first = false;
				else
					sb.append("\n");
				
				boolean first2 = true;
				for (Cell cell : row) {
					if (first2)
						first2 = false;
					else
						sb.append(col_seperator);
					
					boolean spaces_at_end = (spaces_at_end_of_line || cell != row.get(row.size()-1));
					cell.appendTo(sb, spaces_at_end);
				}
			}
		}
	}
	
	
	private class Column extends ArrayList<Cell> {
		private int width;
		private char alignment;
		
		public Column() { this('<'); }
		
		public Column(char alignment) {
			this.alignment = alignment;
		}
		
		public char getAlignment() {
			return alignment;
		}

		public int getWidth() {
			return width;
		}

		public void increaseWidth() {
			++width;
		}

		public void calculateMinWidth() {
			width = 0;
			for (Cell cell : this) {
				if (cell.isSimpleCell()) {
					int w = cell.getWidth();
					if (w > width)
						width = w;
				}
			}
		}
		
		public boolean isWeightValid() {
			boolean done = true;
			for (Cell cell : this) {
				if (!cell.isSimpleCell() && cell.getWidth() > width) {
					done = false;
					break;
				}
			}
			return done;
		}
		
		public int getSpanningWeight(int col_count) {
			int weight = 0;
			for (Cell cell : this) {
				if (!cell.isSimpleCell()) {
					weight += cell.getSpanningWeight(col_count);
				}
			}
			return weight;
		}
	}
	
	private abstract class Cell {
		private String contents;
		private char alignment;
		
		public Cell(String contents, char alignment) {
			super();
			this.contents = contents;
			this.alignment = alignment;
		}
		
		public int getSpanningWeight(int col_count) {
			return 0;
		}

		public abstract boolean isSimpleCell();

		public abstract void appendTo(StringBuffer sb, boolean spaces_at_end);
		
		public String getContents() {
			return contents;
		}

		public char getAlignment() {
			return alignment;
		}

		public int getWidth() {
			return getContents().length();
		}

		private void appendSpaces(StringBuffer sb, int count) {
			for (int i=0;i<count;i++)
				sb.append(' ');
		}
		
		public void appendTo(StringBuffer sb, int width, boolean spaces_at_end) {
			int spaces = width - contents.length();
			switch (alignment) {
			case '<':
			case 'l':
				sb.append(contents);
				if (spaces_at_end)
					appendSpaces(sb, spaces);
				break;
			case '-':
			case 'c':
				appendSpaces(sb, spaces/2);
				sb.append(contents);
				if (spaces_at_end)
					appendSpaces(sb, spaces - spaces/2);
				break;
			case '>':
			case 'r':
				appendSpaces(sb, spaces);
				sb.append(contents);
				break;
			default:
				throw new IllegalStateException("invalid alignment");
			}
		}
	}
	
	private class SimpleCell extends Cell {
		private Column column;
		
		public SimpleCell(String contents, char alignment, Column column) {
			super(contents, alignment);
			this.column = column;
		}

		public boolean isSimpleCell() { return true; }

		@Override
		public void appendTo(StringBuffer sb, boolean spaces_at_end) {
			appendTo(sb, column.getWidth(), spaces_at_end);
		}
	}
	
	private class SpanningCell extends Cell {
		private ArrayList<Column> columns = new ArrayList<Table.Column>();

		public SpanningCell(String contents, char alignment) {
			super(contents, alignment);
		}
		
		public void addColumn(Column col) {
			columns.add(col);
		}

		public ArrayList<Column> getColumns() {
			return columns;
		}

		public boolean isSimpleCell() { return false; }
		
		private int getTotalColumnWidth() {
			int width = 0;
			for (Column col : columns) {
				width += col.getWidth();
			}
			return width;
		}

		public int getSpanningWeight(int col_count) {
			int width_to_distribute = this.getWidth() - getTotalColumnWidth();
			return width_to_distribute * col_count / columns.size();
		}

		@Override
		public void appendTo(StringBuffer sb, boolean spaces_at_end) {
			appendTo(sb, getTotalColumnWidth(), spaces_at_end);
		}
	}
}
