package de.upbracing.code_generation.common;

import info.reflectionsofmind.parser.Grammar;
import info.reflectionsofmind.parser.Matchers;
import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.exception.GrammarParsingException;
import info.reflectionsofmind.parser.matcher.Matcher;
import info.reflectionsofmind.parser.node.AbstractNode;
import info.reflectionsofmind.parser.transform.AbstractTransformer;
import info.reflectionsofmind.parser.transform.ChildTransformer2;
import info.reflectionsofmind.parser.transform.ITransform;
import info.reflectionsofmind.parser.transform.Transform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.upbracing.code_generation.fsm.model.ParserException;
import de.upbracing.code_generation.utils.Util;

public class Parser {
	private Map<String, Matcher> definitions;
	private Transform transform;

	public Parser(Map<String, Matcher> definitions, Transform transform) {
		this.definitions = definitions;
		this.transform = transform;
	}
	
	public Parser(Class<?> path_reference, String grammar_file, Transform transform,
			Parser... used_parsers) throws GrammarParsingException {
		Map<String, Matcher> previous_defs = new HashMap<String, Matcher>();
		for (Parser parser : used_parsers)
			previous_defs.putAll(parser.definitions);
		
		this.definitions = Grammar.generateDefinitions(
				Util.loadResource(path_reference, grammar_file), previous_defs);
		
		if (used_parsers.length > 0) {
			Transform transform2 = new Transform();
			for (Parser parser : used_parsers)
				if (parser.transform != null)
					transform2.add(parser.transform);
			if (transform != null)
				transform2.add(transform);
			this.transform = transform2;
		} else
			this.transform = transform;
	}

	public Object parse(String parserName, String text) {
		ResultTree result = parseToTree(parserName, text);
		
		return result.root.value;
	}

	public ResultTree parseToTree(String parserName, String text) {
		List<ResultTree> results = parseToTrees(parserName, text);
		
		if (results.size() < 1)
			//TODO include some helpful information, see the parser in Grammar
			throw new ParserException("no result");
		
		ResultTree result;
		boolean accept_ambiguous = false;
		if (accept_ambiguous) {
			if (results.size() > 1)
				System.err.println("WARN: We got " + results.size() + " trees instead of one.");
			
			result = results.get(0);
			for (ResultTree tree : results) {
				if (tree.rest > result.rest)
					result = tree;
			}
		} else {
			if (results.size() > 1)
				//TODO include some helpful information, see the parser in Grammar
				throw new ParserException(results);
			
			result = results.get(0);
		}

		if (transform != null)
			transform.transform(result.root);
		return result;
	}

	public List<ResultTree> parseToTrees(String parserName, String text) {
		Matcher matcher = definitions.get(parserName);
		if (matcher == null)
			throw new IllegalArgumentException("parser definition not found: " + parserName);
		List<ResultTree> results = Matchers.fullMatch(matcher, text);
		return results;
	}

	private static Parser common_parser = null;
	public static Parser getCommonParser() {
		if (common_parser != null)
			return common_parser;
		
		Transform transform = new Transform();
		
		transform.add(new AbstractTransformer("positive-number") {
			@Override
			public void transform(AbstractNode node, ITransform transform) {
				node.value = Integer.parseInt(node.getText());
			}
		});
		
		transform.add(new AbstractTransformer("float-number", "simple-unsigned-float-number") {
			@Override
			public void transform(AbstractNode node, ITransform transform) {
				node.value = Double.parseDouble(node.getText());
			}
		});
		
		transform.add(new ChildTransformer2<Integer, Double>(Integer.class, Double.class, "ratio-number") {
			protected Object transform(Integer num, Double denom) {
				return num / denom;
			}
		});
		
		try {
			common_parser = new Parser(Parser.class, "common.g", transform);
		} catch (GrammarParsingException e) {
			throw new RuntimeException(e);
		}
		
		return common_parser;
	}
	
	public Map<String, Matcher> getGrammarDefinitions() {
		return  definitions;
	}

	public ITransform getTransform() {
		return transform.asImmutable();
	}
}
