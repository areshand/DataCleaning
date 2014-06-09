package edu.isi.karma.cleaning;

import java.util.ArrayList;

public class ParseTreeNode {
	String type = "";
	String value = "";
	ArrayList<ParseTreeNode> children = new ArrayList<ParseTreeNode>();
	public ParseTreeNode(String type, String value)
	{
		this.type = type;
		this.value = value;
	}
	public void addChildren(ParseTreeNode node)
	{
		this.children.add(node);
	}
	public ArrayList<ParseTreeNode> getChildren()
	{
		return this.children;
	}
	public String toString()
	{
		String root = value+"(";
		String res = "";
		for(ParseTreeNode node: children)
		{
			res += node.toString();
		}
		return root+res+")";
	}
}
