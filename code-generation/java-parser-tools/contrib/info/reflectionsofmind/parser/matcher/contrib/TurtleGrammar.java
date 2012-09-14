package info.reflectionsofmind.parser.matcher.contrib;

import static info.reflectionsofmind.parser.Matchers.cho;
import static info.reflectionsofmind.parser.Matchers.min;
import static info.reflectionsofmind.parser.Matchers.mins;
import static info.reflectionsofmind.parser.Matchers.opt;
import static info.reflectionsofmind.parser.Matchers.opts;
import static info.reflectionsofmind.parser.Matchers.range;
import static info.reflectionsofmind.parser.Matchers.reps;
import static info.reflectionsofmind.parser.Matchers.seq;
import static info.reflectionsofmind.parser.Matchers.str;
import static info.reflectionsofmind.parser.contrib.ContribMatchers.longs;
import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.matcher.Matcher;
import info.reflectionsofmind.parser.matcher.NamedMatcher;
import info.reflectionsofmind.parser.matcher.common.BooleanMatcher;
import info.reflectionsofmind.parser.matcher.common.DecimalMatcher;
import info.reflectionsofmind.parser.matcher.common.DoubleMatcher;
import info.reflectionsofmind.parser.matcher.common.IntegerMatcher;
import info.reflectionsofmind.parser.matcher.common.QuotedStringMatcher;
import info.reflectionsofmind.parser.matcher.common.WhitespaceMatcher;
import info.reflectionsofmind.parser.node.StringNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Defines a grammer for the TURTLE RDF language.
 * 
 * @see http://www.dajobe.org/2004/01/turtle/
 * 
 *      Use TurtleGrammar.getMatcher() to get a matcher for parsing a Turtle RDF document.
 * 
 * @author Ted Stockwell [emorning@yahoo.com]
 */
public class TurtleGrammar
{
	/**
	 * Symbols used in this grammar
	 */
	public static final String DOCUMENT = "turtleDocument";
	public static final String STATEMENT = "statement";
	public static final String DIRECTIVE = "directive";
	public static final String TRIPLES = "triples";
	public static final String PREDICATE_OBJECT_LIST = "predicateObjectList";
	public static final String VERB = "verb";
	public static final String OBJECT_LIST = "objectList";
	public static final String OBJECT = "object";
	public static final String LITERAL = "literal";
	public static final String LANGUAGE = "language";
	public static final String DATATYPE_STRING = "datatypeString";
	public static final String INTEGER = "integer";
	public static final String DOUBLE = "double";
	public static final String DECIMAL = "decimal";
	public static final String BOOL = "bool";
	public static final String PREFIX_ID = "prefixID";
	public static final String BASE = "base";
	public static final String SUBJECT = "subject";
	public static final String RESOURCE = "resource";
	public static final String URIREF = "uriref";
	public static final String PREFIX_NAME = "prefixName";
	public static final String NODE_ID = "nodeID";
	public static final String QNAME = "qname";
	public static final String QUOTED_STRING = "quotedString";

	public static class URIRefMatcher implements Matcher
	{
		@Override
		public List<ResultTree> match(String input)
		{
			if (input.length() <= 0 || input.charAt(0) != '<')
				return Collections.<ResultTree> emptyList();

			int pos = input.indexOf('>', 1);
			if (pos < 0)
				return Collections.<ResultTree> emptyList();

			pos++;
			String text = input.substring(0, pos);
			return Arrays.asList(new ResultTree(new StringNode(text), pos));
		}
	}

	public static class NameMatcher implements Matcher
	{
		@Override
		public List<ResultTree> match(String input)
		{
			int len = input.length();
			if (len <= 0)
				return Collections.<ResultTree> emptyList();
			char ch = input.charAt(0);
			if (!Character.isLetter(ch) || Character.isWhitespace(ch))
				return Collections.<ResultTree> emptyList();
			int i = 1;
			for (; i < len; i++)
			{
				ch = input.charAt(i);
				if (!Character.isLetterOrDigit(ch) || Character.isWhitespace(ch))
					if (ch != '-')
						break;
			}
			return Arrays.asList(new ResultTree(new StringNode(input.substring(0, i)), i));
		}
	}

	static HashMap<String, Matcher> __matchersById = new HashMap<String, Matcher>();

	static
	{
		Matcher lower = range('a', 'z');
		Matcher digit = range('0', '9');
		Matcher ws = new WhitespaceMatcher();
		Matcher optws = opt(ws);
		Matcher name = new NameMatcher();

		NamedMatcher quotedString = defineMatcher(QUOTED_STRING, new QuotedStringMatcher());
		NamedMatcher qname = defineMatcher(QNAME, longs(opt(name), str(":"), opt(name)));
		NamedMatcher nodeID = defineMatcher(NODE_ID, seq(str("_:"), name));
		NamedMatcher prefixName = defineMatcher(PREFIX_NAME, name);
		NamedMatcher uriref = defineMatcher(URIREF, new URIRefMatcher());
		NamedMatcher resource = defineMatcher(RESOURCE, cho(uriref, qname));
		NamedMatcher subject = defineMatcher(SUBJECT, cho(resource, nodeID, str("[]")));
		NamedMatcher base = defineMatcher(BASE, seq(str("@base"), ws, uriref));
		NamedMatcher prefixID = defineMatcher(PREFIX_ID, seq(str("@prefix"), ws, opt(prefixName), str(":"), ws, uriref));
		NamedMatcher bool = defineMatcher(BOOL, new BooleanMatcher());
		NamedMatcher decimal = defineMatcher(DECIMAL, new DecimalMatcher());
		NamedMatcher doubl = defineMatcher(DOUBLE, new DoubleMatcher());
		NamedMatcher integer = defineMatcher(INTEGER, new IntegerMatcher());
		NamedMatcher datatypeString = defineMatcher(DATATYPE_STRING, seq(quotedString, str("^^"), resource));
		NamedMatcher language = defineMatcher(LANGUAGE, seq(min(1, lower), reps(str("-"), min(1, seq(cho(lower, digit))))));
		NamedMatcher literal = defineMatcher(LITERAL, cho(seq(quotedString, opts(str("@"), language)), datatypeString, integer, doubl, decimal, bool));
		NamedMatcher object = defineMatcher(OBJECT, cho(resource, literal, nodeID, str("[]")));
		NamedMatcher objectList = defineMatcher(OBJECT_LIST, seq(object, reps(optws, str(","), optws, object)));
		NamedMatcher verb = defineMatcher(VERB, cho(resource, str("a")));
		NamedMatcher predicateObjectList = defineMatcher(PREDICATE_OBJECT_LIST, longs(verb, ws, objectList, reps(optws, str(";"), optws, verb, ws, objectList)));
		NamedMatcher triples = defineMatcher(TRIPLES, seq(subject, ws, predicateObjectList));
		NamedMatcher directive = defineMatcher(DIRECTIVE, cho(prefixID, base));
		NamedMatcher statement = defineMatcher(STATEMENT, cho(seq(directive, optws, str(".")), seq(triples, optws, str("."))));
		defineMatcher(DOCUMENT, longs(mins(1, optws, statement), optws));
	}

	private static NamedMatcher defineMatcher(String symbol, Matcher definition)
	{
		NamedMatcher matcher = new NamedMatcher(symbol).define(definition);
		__matchersById.put(symbol, matcher);
		return matcher;
	}

	public static Matcher getMatcher(String symbol)
	{
		return __matchersById.get(symbol);
	}

	public static Matcher getMatcher()
	{
		return getMatcher(DOCUMENT);
	}

}
