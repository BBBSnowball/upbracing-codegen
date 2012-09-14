/**
 * 
 */
package info.reflectionsofmind.parser;

import info.reflectionsofmind.parser.node.AbstractNode;

/**
 * This class holds result of parsing a part of input string. In addition to the node tree it contains the {@link #rest}
 * field that indicates beginning position of the unparsed part of string.
 */
public final class ResultTree
{
	/** Node containing parsed symbols as a tree. */
	public final AbstractNode root;

	/** Index of the first symbol of the unparsed part of the string. */
	public final int rest;

	public ResultTree(final AbstractNode root, final int rest)
	{
		this.root = root;
		this.rest = rest;
	}

	@Override
	public String toString()
	{
		return this.root.toString();
	}
}