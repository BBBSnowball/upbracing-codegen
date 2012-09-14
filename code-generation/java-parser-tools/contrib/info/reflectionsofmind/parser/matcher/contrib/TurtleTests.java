package info.reflectionsofmind.parser.matcher.contrib;

import static info.reflectionsofmind.parser.matcher.contrib.TurtleGrammar.DOCUMENT;
import static info.reflectionsofmind.parser.matcher.contrib.TurtleGrammar.LITERAL;
import static info.reflectionsofmind.parser.matcher.contrib.TurtleGrammar.OBJECT_LIST;
import static info.reflectionsofmind.parser.matcher.contrib.TurtleGrammar.PREDICATE_OBJECT_LIST;
import static info.reflectionsofmind.parser.matcher.contrib.TurtleGrammar.STATEMENT;
import static info.reflectionsofmind.parser.matcher.contrib.TurtleGrammar.SUBJECT;
import static info.reflectionsofmind.parser.matcher.contrib.TurtleGrammar.TRIPLES;
import static info.reflectionsofmind.parser.matcher.contrib.TurtleGrammar.URIREF;
import static info.reflectionsofmind.parser.matcher.contrib.TurtleGrammar.VERB;
import info.reflectionsofmind.parser.Grammar;
import info.reflectionsofmind.parser.Matchers;
import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.matcher.Matcher;
import info.reflectionsofmind.parser.node.AbstractNode;
import info.reflectionsofmind.parser.node.Navigation;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TurtleTests extends TestCase
{
	public void testBasicTriples() throws Exception
	{
		String document = "@prefix resource: <meteor:net.sf.meteor.Resource.>.\n" + "@prefix test: <meteor:net.sf.meteor.test.>.\n" + "\n" + "test:CustomerFromManifest resource:type test:Customer ;\n" + "			resource:description \"A Customer defined in a Meteor manifest\" .\n\n\n\n\n";

		Matcher literalMatcher = TurtleGrammar.getMatcher(LITERAL);
		Assert.assertEquals(1, literalMatcher.match("\"A Customer defined in a Meteor manifest\"").size());

		String testgrammar = "uriref ::= (\"<\" relativeURI \">\")\n" + "relativeURI ::= (anychar {anychar})\n" + "anychar ::= (%alpha% | %digit% | \" \" | \":\" | \".\")";
		Matcher relativeURIMatcher = Grammar.generate(testgrammar, "relativeURI");
		Assert.assertEquals(1, Matchers.fullMatch(relativeURIMatcher, "meteor:net.sf.meteor.TestBindType.instance").size());

		Matcher urirefMatcher = TurtleGrammar.getMatcher(URIREF);
		Assert.assertEquals(1, urirefMatcher.match("<meteor:net.sf.meteor.TestBindType.instance>").size());

		Matcher subjectMatcher = TurtleGrammar.getMatcher(SUBJECT);
		Assert.assertEquals(1, subjectMatcher.match("<meteor:net.sf.meteor.TestBindType.instance>").size());
		Assert.assertEquals(1, subjectMatcher.match("test:CustomerFromManifest").size());

		Matcher verbMatcher = TurtleGrammar.getMatcher(VERB);
		Assert.assertEquals(1, verbMatcher.match("resource:type").size());

		Matcher objectListMatcher = TurtleGrammar.getMatcher(OBJECT_LIST);
		Assert.assertEquals(1, objectListMatcher.match("webbench:WebbenchModule").size());

		Matcher predicateObjectListMatcher = TurtleGrammar.getMatcher(PREDICATE_OBJECT_LIST);
		Assert.assertEquals(1, predicateObjectListMatcher.match("resource:type webbench:WebbenchModule").size());
		Assert.assertEquals(1, predicateObjectListMatcher.match("resource:description \"A Customer defined in a Meteor manifest\"").size());
		Assert.assertEquals(1, predicateObjectListMatcher.match("resource:type test:Customer ;\n\t\t\tresource:description \"A Customer defined in a Meteor manifest\"").size());

		Matcher triplesMatcher = TurtleGrammar.getMatcher(TRIPLES);
		String tripleDoc = "<meteor:net.sf.meteor.TestBindType.instance> resource:type webbench:WebbenchModule";
		Assert.assertEquals(1, triplesMatcher.match(tripleDoc).size());
		tripleDoc = "test:CustomerFromManifest resource:type test:Customer ;\n\t\t\tresource:description \"A Customer defined in a Meteor manifest\"";
		Assert.assertEquals(1, triplesMatcher.match(tripleDoc).size());

		Matcher statementMatcher = TurtleGrammar.getMatcher(STATEMENT);
		String statementDoc = tripleDoc + " .\n\n\n\n\n";
		Assert.assertEquals(1, statementMatcher.match(statementDoc).size());

		Matcher matcher = TurtleGrammar.getMatcher(DOCUMENT);
		List<ResultTree> results = matcher.match(document);
		Assert.assertEquals(1, results.size());
		AbstractNode rootNode = results.get(0).root;

		// the AST should have 3 statements in it
		List<AbstractNode> statements = Navigation.findAllDecendentsById(rootNode, "statement");
		Assert.assertEquals(3, statements.size());
	}

}
