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
		columns.calculateColumnWidths(col_seperator.length());
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
	
	
	/** Represents, parses and prints data that is organized in columns
	 * 
	 * @author benny
	 */
	private class Columns {
		/** map of column list name to columns */
		private TreeMap<Character, ArrayList<Column>> column_map;
		/** the current column list */
		private ArrayList<Column> columns;
		/** regex matchers for line seperators */
		private Matcher line_matcher;
		/** regex matchers for column seperators */
		private Matcher seperator_matcher;
		/** the code to parse */
		private String code;
		/** cells organized in rows */
		private ArrayList<ArrayList<Cell>> rows;
		/** the current row */
		private ArrayList<Cell> row;
		/** column index in the current row (while parsing) */
		private int column_index;
		
		/** parse a String to extract columns, rows and cells
		 * 
		 * @param code the string to parse
		 */
		public void parse(String code) {
			// initialize variables
			column_map = new TreeMap<Character, ArrayList<Column>>();
			columns = new ArrayList<Column>();
			column_map.put('a', columns);
			rows = new ArrayList<ArrayList<Cell>>();
			
			// parse table options
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

			// add a "\n" before the string to make it more regular
			// ("\n" before each line including the first one -> no special case for the first line)
			this.code = code = "\n" + code;

			// create matcher objects for line and column seperators
			line_matcher = line_regex.matcher(code);
			seperator_matcher = seperator_regex.matcher(code);
			
			// set default options
			initOptions();
			
			// handle options of first line
			line_matcher.find(start_index);
			if (seperator_with_options && line_matcher.group(1) != null)
				handleLineOptions(line_matcher.group(1));
			start_index = line_matcher.end();
			
			// parse all lines which are terminated by an instance of the line seperator
			while (line_matcher.find()) {
				handleLine(start_index, line_matcher.start());
				start_index = line_matcher.end();
			}
			
			// parse the last line which goes to the end of the string
			handleLine(start_index, code.length());
		}

		/** parse a line including the line seperator after it
		 * 
		 * @param start_index start of the line
		 * @param end_index end of the line (before the line seperator)
		 */
		private void handleLine(int start_index, int end_index) {
			// start a new row
			column_index = 0;
			row = new ArrayList<Table.Cell>();
			rows.add(row);
			
			// if the row is not empty, add columns
			if (start_index < end_index) {
				// make sure we only find column seperators within this line
				seperator_matcher.region(start_index, end_index);
				
				// for each column seperator, process the cell before it
				while (seperator_matcher.find()) {
					handleColumn(code.substring(start_index, seperator_matcher.start()));
					if (seperator_with_options && seperator_matcher.group(1) != null)
						handleColumnOptions(seperator_matcher.group(1));
					else
						handleColumnNoOptions();
					
					start_index = seperator_matcher.end();
				}
				
				// process the last column
				handleColumn(code.substring(start_index, end_index));
			}
			
			// reset column options
			handleColumnNoOptions();

			// parse options for the next line and its first column
			if (seperator_with_options && !line_matcher.hitEnd() && line_matcher.group(1) != null)
				handleLineOptions(line_matcher.group(1));
		}
		
		/** alignment of the next cell */
		private char alignment;
		/** column span of the next cell */
		private int col_span;

		/** reset options to default values */
		private void initOptions() {
			handleColumnNoOptions();
		}

		/** parse options for a line
		 * 
		 * @param group contents of the regex group with the options
		 */
		private void handleLineOptions(String group) {
			char colset_name = group.charAt(0);
			if (colset_name != '_') {
				columns = column_map.get(colset_name);
				if (columns == null) {
					columns = new ArrayList<Table.Column>();
					column_map.put(colset_name, columns);
				}
			}
			
			handleColumnOptions(group.substring(1));
		}

		/** parse options for a column
		 * 
		 * @param group contents of the regex group with the options
		 */
		private void handleColumnOptions(String group) {
			Matcher m = COLUMN_OPTION_REGEX2.matcher(group);
			if (!m.matches())
				throw new RuntimeException("shouldn't happen -> error in the program");
			
			String align_str = m.group(1) + m.group(3);
			String span_str = m.group(2);
			
			if (span_str.length() > 0)
				col_span = Integer.parseInt(span_str);
			else
				col_span = 1;
			
			if (col_span == 0)
				return;
			
			if (align_str.length() == 0)
				alignment = ' ';
			else if (align_str.length() == 1)
				alignment = align_str.charAt(0);
			else
				throw new IllegalStateException("found more than one alignment character in: " + group);
		}

		/** reset column options */
		private void handleColumnNoOptions() {
			alignment = ' ';
			col_span = 1;
		}

		/** add a column
		 * 
		 * @param contents column contents
		 */
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

		/** calculate the width of all columns
		 * 
		 * @param col_seperator length of the column seperator
		 */
		public void calculateColumnWidths(int col_seperator) {
			for (ArrayList<Column> columns : column_map.values()) {
				LinkedList<Column> todo = new LinkedList<Table.Column>(columns);
				Iterator<Column> it = todo.iterator();
				while (it.hasNext()) {
					Column col = it.next();
					col.calculateMinWidth();
					if (col.isWeightValid())
						// remove this column, as it is wide enough for all the cells
						it.remove();
				}
				
				while (!todo.isEmpty()) {
					float max_weight = Float.NEGATIVE_INFINITY;
					Column max_col = null;
					
					int todo_count = todo.size();
					it = todo.iterator();
					while (it.hasNext()) {
						Column col = it.next();
						int weight = col.getSpanningWeight(todo_count, col_seperator);
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
		}
		
		/** print the contents of all columns and rows into a StringBuffer
		 * 
		 * @param sb StringBuffer to write into
		 * @param col_seperator column seperator to print between columns
		 */
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
					cell.appendTo(sb, spaces_at_end, col_seperator.length());
				}
			}
		}
	}
	
	/** a column */
	@SuppressWarnings("serial")
	private class Column extends ArrayList<Cell> {
		/** width of the column */
		private int width;
		/** alignment of the column */
		private char alignment;
		
		/** constructor
		 * 
		 * @param alignment alignment of the column
		 */
		public Column(char alignment) {
			this.alignment = alignment;
		}
		
		/** get column alignment
		 * 
		 * @return the alignment
		 */
		public char getAlignment() {
			return alignment;
		}

		/** the width
		 * 
		 * @return the width
		 */
		public int getWidth() {
			return width;
		}

		/** increase column width by one */
		public void increaseWidth() {
			++width;
		}

		/** calculate minimum width to contain all non-spanning cells */
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
		
		/** is the column wide enough to contain all cells */
		public boolean isWeightValid() {
			boolean done = true;
			for (Cell cell : this) {
				if (cell.getWidth() > width) {
					done = false;
					break;
				}
			}
			return done;
		}
		
		/** get sum of spanning widths of all contained cells
		 * 
		 * @param col_count count of columns in the column set
		 * @param col_seperator length of the column seperator
		 * @return the spanning weight
		 */
		public int getSpanningWeight(int col_count, int col_seperator) {
			int weight = 0;
			for (Cell cell : this) {
				if (!cell.isSimpleCell()) {
					weight += cell.getSpanningWeight(col_count, col_seperator);
				}
			}
			return weight;
		}
	}
	
	/** a cell within a column */
	private abstract class Cell {
		private String contents;
		private char alignment;
		
		/** constructor
		 * 
		 * @param contents cell contents
		 * @param alignment cell alignment
		 */
		public Cell(String contents, char alignment) {
			super();
			this.contents = contents;
			this.alignment = alignment;
		}
		
		/** get spanning weight
		 * 
		 * @param col_count count of columns in the column set
		 * @param col_seperator length of the column seperator
		 * @return the weight
		 */
		public int getSpanningWeight(int col_count, int col_seperator) {
			return 0;
		}

		/** is it a simple cell?
		 * 
		 * @return true for a simple cell, false otherwise
		 */
		public abstract boolean isSimpleCell();

		/** append contents of the cell to a StringBuffer
		 * 
		 * @param sb the StringBuffer
		 * @param spaces_at_end true, if padding spaces at the end of the cell should be printed
		 * @param col_seperator length of the column seperator
		 */
		public abstract void appendTo(StringBuffer sb, boolean spaces_at_end, int col_seperator);
		
		/** get contents
		 * 
		 * @return the contents of this cell
		 */
		public String getContents() {
			return contents;
		}

		/** get alignment
		 * 
		 * @return the alignment
		 */
		@SuppressWarnings("unused")
		public char getAlignment() {
			return alignment;
		}

		/** get width
		 * 
		 * @return the width
		 */
		public int getWidth() {
			return getContents().length();
		}

		/** append some spaces to a StringBuffer
		 * 
		 * @param sb the buffer
		 * @param count count of spaces
		 */
		private void appendSpaces(StringBuffer sb, int count) {
			for (int i=0;i<count;i++)
				sb.append(' ');
		}
		
		/** append cell contents to a StringBuffer
		 * 
		 * @param sb the buffer
		 * @param width width of the column
		 * @param spaces_at_end print space at the end
		 */
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
	
	/** simple cell */
	private class SimpleCell extends Cell {
		private Column column;
		
		/** constructor
		 * 
		 * @param contents cell contents
		 * @param alignment cell alignment
		 * @param column column this cell belongs to
		 */
		public SimpleCell(String contents, char alignment, Column column) {
			super(contents, alignment);
			this.column = column;
		}

		@Override
		public boolean isSimpleCell() { return true; }

		@Override
		public void appendTo(StringBuffer sb, boolean spaces_at_end, int col_seperator) {
			appendTo(sb, column.getWidth(), spaces_at_end);
		}
	}
	
	/** a cell that spans more than one column */
	private class SpanningCell extends Cell {
		private ArrayList<Column> columns = new ArrayList<Table.Column>();

		/** constructor
		 * 
		 * @param contents cell contents
		 * @param alignment cell alignment
		 */
		public SpanningCell(String contents, char alignment) {
			super(contents, alignment);
		}
		
		/** add a column
		 * 
		 * @param col the column
		 */
		public void addColumn(Column col) {
			columns.add(col);
		}

		/** get the columns
		 * 
		 * @return the list of columns
		 */
		@SuppressWarnings("unused")
		public ArrayList<Column> getColumns() {
			return columns;
		}

		@Override
		public boolean isSimpleCell() { return false; }
		
		private int getTotalColumnWidth() {
			int width = 0;
			for (Column col : columns) {
				width += col.getWidth();
			}
			return width;
		}

		@Override
		public int getSpanningWeight(int col_count, int col_seperator) {
			// get the width that we need
			int width_to_distribute = this.getWidth();
			// subtract the width that we already have
			width_to_distribute -= getTotalColumnWidth();
			// subtract the width that the other columns need for the column seperators
			width_to_distribute -= col_seperator * (columns.size()-1);
			// no negative values because they could cancel positive values from other cells
			if (width_to_distribute < 0)
				width_to_distribute = 0;
			return width_to_distribute * col_count / columns.size();
		}

		@Override
		public void appendTo(StringBuffer sb, boolean spaces_at_end, int col_seperator) {
			appendTo(sb,
					getTotalColumnWidth() + col_seperator * (columns.size()-1),
					spaces_at_end);
		}
	}
}
