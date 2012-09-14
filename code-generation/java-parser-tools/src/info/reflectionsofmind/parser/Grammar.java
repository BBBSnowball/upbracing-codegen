package info.reflectionsofmind.parser;

import static info.reflectionsofmind.parser.Matchers.cho;
import static info.reflectionsofmind.parser.Matchers.minc;
import static info.reflectionsofmind.parser.Matchers.mins;
import static info.reflectionsofmind.parser.Matchers.opt;
import static info.reflectionsofmind.parser.Matchers.optc;
import static info.reflectionsofmind.parser.Matchers.opts;
import static info.reflectionsofmind.parser.Matchers.range;
import static info.reflectionsofmind.parser.Matchers.rep;
import static info.reflectionsofmind.parser.Matchers.repc;
import static info.reflectionsofmind.parser.Matchers.reps;
import static info.reflectionsofmind.parser.Matchers.seq;
import static info.reflectionsofmind.parser.Matchers.str;
import info.reflectionsofmind.parser.exception.AmbiguousGrammarException;
import info.reflectionsofmind.parser.exception.GrammarParsingException;
import info.reflectionsofmind.parser.exception.InvalidGrammarException;
import info.reflectionsofmind.parser.exception.UndefinedSymbolException;
import info.reflectionsofmind.parser.matcher.Matcher;
import info.reflectionsofmind.parser.matcher.NamedMatcher;
import info.reflectionsofmind.parser.node.AbstractNode;
import info.reflectionsofmind.parser.node.NamedNode;
import info.reflectionsofmind.parser.node.Navigation;
import info.reflectionsofmind.parser.node.Nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grammar
{
	public static final Matcher GRAMMAR;

	/**
	 * @param grammarCode
	 *            A {@link String} containing the definition of grammar in EBNF. See <a
	 *            href="http://en.wikipedia.org/wiki/Ebnf">wiki article</a> for generic description and <a
	 *            href="http://code.google.com/p/java-parser-tools/wiki/GrammarFormat">project page</a> for detailed
	 *            format specification.
	 * 
	 * @param rootDefinition
	 *            Name of the top-level definition in the grammar.
	 * 
	 * @return A {@link Matcher} corresponding to this grammar.
	 * 
	 * @throws InvalidGrammarException
	 *             If the grammar definition string is invalid.
	 * 
	 * @throws UndefinedSymbolException
	 *             If grammar references a symbol that is not defined in it.
	 * 
	 * @throws AmbiguousGrammarException
	 *             If the grammar definition string is ambiguous - can be parsed as two different grammars. In theory,
	 *             this can not happen. Please contact the author if it did because he would very much grateful to know
	 *             how you did it.
	 */
	public static Matcher generate(final String grammarCode, final String rootDefinition) throws GrammarParsingException
	{
		final List<ResultTree> results = Matchers.fullMatch(GRAMMAR, grammarCode);

		if (results.size() > 1)
			throw new AmbiguousGrammarException(results);

		if (results.isEmpty())
			throw new InvalidGrammarException(GRAMMAR.match(grammarCode));

		final NamedNode grammar = (NamedNode) results.get(0).root;
		final Map<String, NamedMatcher> definitions = new HashMap<String, NamedMatcher>();

		for (final NamedNode definition : grammar.getNamedChildren())
		{
			final String identifier = definition.getNamedChildren().get(0).getText();
			definitions.put(identifier, new NamedMatcher(identifier));
		}

		for (final NamedNode definition : grammar.getNamedChildren())
		{
			final String identifier = definition.getNamedChildren().get(0).getText();
			final NamedNode expression = definition.getNamedChildren().get(1);
			definitions.get(identifier).define(createMatcher(expression, definitions));
		}

		// check for undefined identifiers
		List<AbstractNode> identifierNodes = Navigation.findAllDecendentsById(grammar, "identifier");
		for (AbstractNode identifierNode : identifierNodes)
		{
			String identifier = identifierNode.getText();
			if (definitions.get(identifier) == null)
				throw new UndefinedSymbolException(identifier);
		}

		Matcher matcher = definitions.get(rootDefinition);
		if (matcher == null)
			throw new UndefinedSymbolException(rootDefinition);
		return matcher;
	}

	private static Matcher createMatcher(final NamedNode expression, final Map<String, NamedMatcher> definitions)
	{
		final List<Matcher> matchersList = new ArrayList<Matcher>();

		for (final NamedNode subExpression : expression.getNamedChildren())
		{
			matchersList.add(createMatcher(subExpression, definitions));
		}

		final Matcher[] matchers = matchersList.toArray(new Matcher[] {});

		if ("seq".equals(expression.id))
			return seq(matchers);
		if ("cho".equals(expression.id))
			return cho(matchers);

		if ("rep".equals(expression.id))
			return rep(createMatcher(expression.getNamedChildren().get(0), definitions));
		if ("reps".equals(expression.id))
			return reps(matchers);
		if ("repc".equals(expression.id))
			return repc(matchers);

		if ("opt".equals(expression.id))
			return opt(createMatcher(expression.getNamedChildren().get(0), definitions));
		if ("opts".equals(expression.id))
			return opts(matchers);
		if ("optc".equals(expression.id))
			return optc(matchers);

		if ("string".equals(expression.id))
			return str(expression.getText());
		if ("identifier".equals(expression.id))
			return definitions.get(expression.getText());
		if ("expression".equals(expression.id))
			return createMatcher(expression.getNamedChildren().get(0), definitions);

		if ("anyLower".equals(expression.id))
			return range('a', 'z');
		if ("anyUpper".equals(expression.id))
			return range('A', 'Z');
		if ("anyAlpha".equals(expression.id))
			return cho(range('a', 'z'), range('A', 'Z'));
		if ("anyDigit".equals(expression.id))
			return range('0', '9');
		if ("anyWhitespace".equals(expression.id))
			return minc(1, str(" "), str("\t"), str("\n"), str("\r"));

		throw new RuntimeException("Cannot parse expresion [" + expression.id + "]:\n" + Nodes.toStringFull(expression));
	}

	static
	{
		// Creating the GRAMMAR Matcher for parsing grammar strings.

		final Matcher whitespace = minc(1, str(" "), str("\t"), str("\n"), str("\r"));
		final Matcher optwh = opt(whitespace);

		final Matcher lower = range('a', 'z');
		final Matcher upper = range('A', 'Z');
		final Matcher alpha = cho(lower, upper);
		final Matcher digit = range('0', '9');

		final Matcher normalWord = seq(alpha, repc(digit, alpha));
		final NamedMatcher anyLower = new NamedMatcher("anyLower").define(str("%lower%"));
		final NamedMatcher anyUpper = new NamedMatcher("anyUpper").define(str("%upper%"));
		final NamedMatcher anyAlpha = new NamedMatcher("anyAlpha").define(str("%alpha%"));
		final NamedMatcher anyDigit = new NamedMatcher("anyDigit").define(str("%digit%"));
		final NamedMatcher anyWhitespace = new NamedMatcher("anyWhitespace").define(str("%whitespace%"));

		final NamedMatcher expression = new NamedMatcher("expression");
		final NamedMatcher definition = new NamedMatcher("definition");
		final NamedMatcher grammar = new NamedMatcher("grammar");
		final NamedMatcher identifier = new NamedMatcher("identifier");
		final NamedMatcher string = new NamedMatcher("string");

		final NamedMatcher seq = new NamedMatcher("seq").define( //
				seq(str("("), optwh, expression, mins(1, whitespace, expression), optwh, str(")")));

		final NamedMatcher cho = new NamedMatcher("cho").define( //
				seq(str("("), optwh, expression, mins(1, optwh, str("|"), optwh, expression), optwh, str(")")));

		final NamedMatcher rep = new NamedMatcher("rep").define( //
				seq(str("{"), optwh, expression, optwh, str("}")));

		final NamedMatcher reps = new NamedMatcher("reps").define( //
				seq(str("{"), optwh, expression, mins(1, whitespace, expression), optwh, str("}")));

		final NamedMatcher repc = new NamedMatcher("repc").define( //
				seq(str("{"), optwh, expression, mins(1, optwh, str("|"), optwh, expression), optwh, str("}")));

		final NamedMatcher opt = new NamedMatcher("opt").define( //
				seq(str("["), optwh, expression, optwh, str("]")));

		final NamedMatcher opts = new NamedMatcher("opts").define( //
				seq(str("["), optwh, expression, mins(1, whitespace, expression), optwh, str("]")));

		final NamedMatcher optc = new NamedMatcher("optc").define( //
				seq(str("["), optwh, expression, mins(1, optwh, str("|"), optwh, expression), optwh, str("]")));

		string.define(new Matcher()
		{
			@Override
			public List<ResultTree> match(String input)
			{
				// Firstly, find the closing double-quote skipping all escaped double-quotes.
				int pos = input.indexOf('"');
				while (pos > 0 && input.charAt(pos - 1) == '\'')
					pos = input.indexOf('"', pos + 1);

				// If it is not found, then what we are matching is not a string
				if (pos == -1)
					return Collections.<ResultTree> emptyList();

				// If it is found, extract the string between quotes
				String text = input.substring(0, pos).replaceAll("\'\"", "\"");
				return Arrays.asList(new ResultTree(new NamedNode("string", text), pos));
			}
		});

		identifier.define(seq(normalWord, reps(str("-"), normalWord)));
		definition.define(seq(identifier, optwh, str("::="), optwh, expression));

		expression.define(cho( //
				seq, rep, reps, repc, opt, opts, optc, cho, // 
				identifier, seq(str("\""), string, str("\"")), //
				anyLower, anyUpper, anyAlpha, anyDigit, anyWhitespace));

		grammar.define(seq(optwh, reps(definition, whitespace), opt(definition)));

		GRAMMAR = grammar;
	}
}
