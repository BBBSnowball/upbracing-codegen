package info.reflectionsofmind.parser.matcher;

import info.reflectionsofmind.parser.ResultTree;
import info.reflectionsofmind.parser.node.StringNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class RangeMatcher extends CharacterMatcher
{
	private final char to;
	private final char from;
	
	public RangeMatcher(final char from, final char to)
	{
		this.to = to;
		this.from = from;
	}
	
	@Override
	public boolean predicate(char c) {
		return from <= c && c <= to;
	}
}