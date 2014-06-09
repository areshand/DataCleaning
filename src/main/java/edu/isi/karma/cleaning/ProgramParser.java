package edu.isi.karma.cleaning;


public class ProgramParser {
	public ParseTreeNode root = null;
	public ProgramParser()
	{
		
	}
	public ProgramParser(String program)
	{
		this.root = this.parse(program);
		
	}
	public ParseTreeNode parse(String prog)
	{
		ParseTreeNode root = new ParseTreeNode("root", "null");
		//find segments
		String[] tokens = prog.split("\\+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		for(String tok:tokens)
		{
			ParseTreeNode node = new ParseTreeNode("Segment", tok);
			root.addChildren(node);
			//find the startposition 
			int sposS = tok.indexOf("indexOf(value,",0);
			if(sposS == -1)
			{
				continue;
			}
			int sposE = tok.indexOf("),indexOf");
			String sposExpre = tok.substring(sposS,sposE+1);
			//find the endPosition
			int eposS = tok.indexOf("indexOf(value,",sposE);
			int eposE = tok.indexOf("))",eposS);
			String eposExpre = tok.substring(eposS,eposE+1);
			ParseTreeNode sPosNode = new ParseTreeNode("position", sposExpre);
			ParseTreeNode ePosNode = new ParseTreeNode("position", eposExpre);
			node.addChildren(sPosNode);
			node.addChildren(ePosNode);
		}
		return root;
	}

}
