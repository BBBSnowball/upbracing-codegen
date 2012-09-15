package info.reflectionsofmind.parser.matcher;

import info.reflectionsofmind.parser.ResultTree;

import java.util.List;

/**
 * Implementations of this interface parse a part of the input string returning a list of different {@link ResultTree}s
 * representing different ways to parse this tree.
 */
public interface Matcher
{
	List<ResultTree> match(String input);
}