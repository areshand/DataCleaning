package edu.isi.karma.cleaning;

import java.util.Vector;

public class ProgramAdaptator {
	public ParseTreeNode program;
	public Vector<Vector<Segment>> traces;
	public ProgramAdaptator(ParseTreeNode program, Vector<Vector<Segment>> traces)
	{
		this.program = program;
		this.traces = traces;
	}
	
}
