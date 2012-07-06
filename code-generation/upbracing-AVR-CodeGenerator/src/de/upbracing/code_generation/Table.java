package de.upbracing.code_generation;

public class Table {
	private StringBuilder sb;
	private String seperator_regex;
	private int index = -1;
	private boolean spaces_at_end_of_line;
	
	public Table(StringBuilder sb, String seperator_regex,
			boolean spaces_at_end_of_line) {
		this.sb = sb;
		this.seperator_regex = seperator_regex;
		this.spaces_at_end_of_line = spaces_at_end_of_line;
	}

	public Table(StringBuilder sb, String seperator_regex) {
		this(sb, seperator_regex, false);
	}
	
	public Table(StringBuilder sb) {
		this(sb, "&&&");
	}

	public void start() {
		index = sb.length();
	}
	
	public void finish(String col_seperator) {
		if (index < 0)
			throw new IllegalStateException("You must call start() before you call finish()!");
		
		String code = sb.substring(index);
		sb.setLength(index);
		index = -1;
		
		String lines[] = code.split("\r\n?|\n", -1);
		String cells[][] = new String[lines.length][];
		int cols = 0;
		for (int i=0;i<lines.length;i++) {
			cells[i] = lines[i].split(seperator_regex, -1);
			if (cells[i].length > cols)
				cols = cells[i].length;
		}
		
		int col_length[] = new int[cols];
		for (int i=0;i<lines.length;i++) {
			for (int j=0;j<cells[i].length;j++) {
				int len = cells[i][j].length();
				if (len > col_length[j])
					col_length[j] = len;
			}
		}
		
		for (int i=0;i<lines.length;i++) {
			if (cells[i].length > 1 || !cells[i][0].equals("")) {
				for (int j=0;j<cells[i].length;j++) {
					if (j > 0)
						sb.append(col_seperator);
					sb.append(cells[i][j]);
					if (spaces_at_end_of_line || j != cells[i].length-1)
						spaces(col_length[j] - cells[i][j].length());
				}
			}
			if (i != lines.length - 1)
				sb.append("\n");
		}
	}
	
	public void finish() {
		finish("");
	}
	
	private void spaces(int count) {
		for (int i=0;i<count;i++)
			sb.append(' ');
	}
}
