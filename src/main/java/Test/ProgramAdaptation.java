package Test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.junit.Test;

import edu.isi.karma.cleaning.ExampleTraces;
import edu.isi.karma.cleaning.GrammarTreeNode;
import edu.isi.karma.cleaning.ParseTreeNode;
import edu.isi.karma.cleaning.Partition;
import edu.isi.karma.cleaning.ProgramAdaptator;
import edu.isi.karma.cleaning.ProgramParser;
import edu.isi.karma.cleaning.ProgramRule;
import edu.isi.karma.cleaning.Ruler;
import edu.isi.karma.cleaning.Section;
import edu.isi.karma.cleaning.Segment;
import edu.isi.karma.cleaning.TNode;
import edu.isi.karma.cleaning.Traces;
import edu.isi.karma.cleaning.UtilTools;

public class ProgramAdaptation {

	@Test
	public void testScalability()
	{
		String[] e1 = {"AaBbCcDdEeFf","F E D C B A"};
		String[] e2 = {"A$BbCcDdEeFf","F E D C B A"};
		String[] e3 = {"$a$bCcDdEeFf","F E D C B A"};
		String[] e4 = {"$a$b$cDdEeFf","F E D C B A"};
		String[] e5 = {"$a$b$c$dEeFf","F E D C B A"};
		String[] e6 = {"$a$b$c$d$eFf","F E D C B A"};
		String[] e7 = {"$a$b$c$d$e$f","F E D C B A"};
		ArrayList<String[]> exps = new ArrayList<String[]>();
		exps.add(e1);
		exps.add(e2);
		exps.add(e3);
		exps.add(e4);
		exps.add(e5);
		exps.add(e6);
		exps.add(e7);
		edu.isi.karma.cleaning.Research.Test.test_adaptive_seq(exps);
	}
	
	public void conceptValidation()
	{
		String[] e1 = {"Name: Amber Cmber","Name: Amber NC"};
		String[] e2 = {"Name: Cindie Kindie", "Name: Cindie NK"};
		String[] e3 = {"Name: Kite Ciao", "Name: Kiao NC"};
		ArrayList<String[]> examples = new ArrayList<String[]>();
		examples.add(e1);
		examples.add(e2);
		examples.add(e3);
		ExampleTraces tool = new ExampleTraces();
		Traces t1 = tool.createTrace(e1);
		Traces t2 = tool.createTrace(e2);
		Traces t3 = tool.createTrace(e3);
		Traces t12 = t1.mergewith(t2);
		String program = t12.toProgram();
		HashMap<String, Traces> exp2Space = new HashMap<String, Traces>();
		ArrayList<String[]> klist1 = new ArrayList<String[]>();
		klist1.add(e1);
		String key1 = UtilTools.createkey(klist1);
		exp2Space.put(key1, t1);
		ArrayList<String[]> klist2 = new ArrayList<String[]>();
		klist2.add(e2);
		String key2 = UtilTools.createkey(klist2);
		exp2Space.put(key2, t2);
		ArrayList<String[]> klist4 = new ArrayList<String[]>();
		klist4.add(e2);
		klist4.add(e1);
		String key4 = UtilTools.createkey(klist4);
		exp2Space.put(key4, t12);
		HashMap<String, String> exp2program = new HashMap<String, String>();
		exp2program.put(key4, program);
		ProgramAdaptator pAdapter = new ProgramAdaptator();
		String fprogram= pAdapter.adapt(exp2Space, exp2program, examples);
		//verify the program
		for(String[] exp:examples)
		{
			ProgramRule ptmp1 = new ProgramRule(fprogram);
			String eres = ptmp1.transform(exp[0]);
			System.out.println("Result: "+ eres);
			if(exp[1].compareTo(eres)!=0)
			{
				System.out.println(String.format("%s, %s is incorrect", exp[0],exp[1]));
			}
		}	
	}
	
	//@Test
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

	//@Test
	public void testSubProgramEval() {
		System.out.println("start testing testsubprogramEval.....");
		Vector<String[]> examples = new Vector<String[]>();
		// adding examples
		String[] mt = { "<_START>VVery LLarge DDataBBase<_END>", "VVLLDDBB" };
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
	//@Test
	public void testProgramParsing()
	{
		String prog = "loop(value,r\"substr(value,indexOf(value,'ANY','UWRDUWRD',1*counter),indexOf(value,'UWRD','LWRD',1*counter))\")+substr(value,indexOf(value,'START','UWRD',1*1),indexOf(value,'UWRD','UWRD',1*1))";
		ProgramParser parser = new ProgramParser();
		ParseTreeNode root = parser.parse(prog);
		System.out.println(""+root.toString());
	}
}
