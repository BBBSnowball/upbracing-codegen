package de.upbracing.code_generation.fsm.model;

import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.node.Nodes;

import java.util.List;

@SuppressWarnings("serial")
public class ParserException extends RuntimeException {
	
	public ParserException() {
		super();
	}

	public ParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParserException(String message) {
		super(message);
	}

	public ParserException(Throwable cause) {
		super(cause);
	}
	
	public ParserException(List<ResultTree> ambiguous_trees) {
		super(formatTrees(ambiguous_trees));
	}

	private static String formatTrees(List<ResultTree> ambiguous_trees) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("ambiguous parser result: We have " + ambiguous_trees.size());
		sb.append('\n');
		
		//TODO print difference instead of full tree
		int trees_to_print = 3;
		if (ambiguous_trees.size() > trees_to_print) {
			sb.append("This are the first " + trees_to_print + " trees:");
		} else {
			sb.append("This are the trees:\n\n");
			trees_to_print = ambiguous_trees.size();
		}
		
		for (int i=0;i<trees_to_print;i++) {
			sb.append("\n\n======= TREE " + (i+1) + " =======\n\n");
			
			Nodes.toStringFull(ambiguous_trees.get(i).root, "", sb);
		}
		
		return sb.toString();
	}
}
