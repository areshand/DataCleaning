package Test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Vector;

import org.junit.Test;

import edu.isi.karma.cleaning.ExampleTraces;
import edu.isi.karma.cleaning.GrammarTreeNode;
import edu.isi.karma.cleaning.ParseTreeNode;
import edu.isi.karma.cleaning.Partition;
import edu.isi.karma.cleaning.ProgramParser;
import edu.isi.karma.cleaning.ProgramRule;
import edu.isi.karma.cleaning.Ruler;
import edu.isi.karma.cleaning.Section;
import edu.isi.karma.cleaning.Segment;
import edu.isi.karma.cleaning.TNode;
import edu.isi.karma.cleaning.UtilTools;

public class ProgramAdaptation {

	@Test
	public void testTrace() {
		// adding examples
		String[] mt = { "HelloHorld", "Horld" };
		ExampleTraces traceRepo = new ExampleTraces();
		traceRepo.createTrace(mt);
		Vector<Vector<Segment>> segs = traceRepo.getCurrentSegments(mt);
		assertEquals(segs.size(), 2);
		for (Vector<Segment> tmp : segs) {
			String r = "";
			for (Segment s : tmp) {
				Vector<int[]> poses = traceRepo.getSegmentPos(s);

				for (int[] p : poses) {
					r += Arrays.toString(p) + " || ";
				}
			}
			if (r.length() < 15)
				assertEquals(r, "[5, 10] || ");
			else {
				assertEquals(r, "[0, 1] || [5, 6] || [6, 10] || ");
			}
		}
	}

	@Test
	public void testSubProgramEval() {
		System.out.println("start testing testsubprogramEval.....");
		Vector<String[]> examples = new Vector<String[]>();
		// adding examples
		String[] mt = { "<_START>Mr. Tom Jerry<_END>", "Jerry, T" };
		String[] mt1 = { "<_START>Mr. Andrew Dusk<_END>", "Dusk, A" };
		examples.add(mt);
		examples.add(mt1);
		Vector<Vector<TNode>> orgVector = new Vector<Vector<TNode>>();
		Vector<Vector<TNode>> tarVector = new Vector<Vector<TNode>>();
		for (int i = 0; i < examples.size(); i++) {
			Ruler r = new Ruler();
			r.setNewInput(examples.get(i)[0]);
			orgVector.add(r.vec);
			Ruler r1 = new Ruler();
			r1.setNewInput(examples.get(i)[1]);
			tarVector.add(r1.vec);
		}
		// adding unlabeled data
		for (int i = 0; i < orgVector.size(); i++) {
			Vector<Vector<TNode>> ovt = new Vector<Vector<TNode>>();
			Vector<Vector<TNode>> tvt = new Vector<Vector<TNode>>();
			ovt.add(orgVector.get(i));
			tvt.add(tarVector.get(i));
			Partition pt = new Partition(ovt, tvt);
			String program = pt.toProgram();
			// ProgramRule programRule = new ProgramRule(pt.getProgram());
			System.out.println("pt program: " + pt.getProgram());
			Vector<GrammarTreeNode> nodes = pt.trace.totalOrderVector
					.get(pt.trace.curState).body;
			for (GrammarTreeNode sec : nodes) {
				Segment seg = (Segment) sec;
				String pos1str = "";
				if (seg.constNodes.size() > 0) {
					pos1str = String.format("\"%s\"",
							UtilTools.print(seg.constNodes));
				} else {
					Section sc = seg.section.get(seg.curState);
					pos1str = sc.pair[0].getProgram();
				}
				String pos2str = "";
				if (seg.constNodes.size() > 0) {
					pos2str = String.format("\"%s\"",
							UtilTools.print(seg.constNodes));
				} else {
					Section sc = seg.section.get(seg.curState);
					pos2str = sc.pair[1].getProgram();
				}
				ProgramRule ptmp = new ProgramRule(pos1str);
				String sres = ptmp.transform("Mr. Tom Jerry");
				ProgramRule ptmp1 = new ProgramRule(pos2str);
				String eres = ptmp1.transform("Mr. Tom Jerry");
				System.out.println(String.format("Position %s, %s", pos1str,
						pos2str));
				System.out.println(String.format(
						"Evalutated Result Start: %s, End: %s", sres, eres));
			}
		}
	}
	@Test
	public void testProgramParsing()
	{
		String x = "'";
		String prog = "substr(value,indexOf(value,'BNK','UWRD',2*1),indexOf(value,'LWRD','END',1*1))+','+substr(value,indexOf(value,'\\.','BNK',1*1),indexOf(value,'UWRD','LWRD',2*1))";
		ProgramParser parser = new ProgramParser();
		ParseTreeNode root = parser.parse(prog);
		System.out.println(""+root.toString());
	}
}
