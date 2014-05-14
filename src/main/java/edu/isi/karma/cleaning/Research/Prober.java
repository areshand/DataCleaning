package edu.isi.karma.cleaning.Research;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import edu.isi.karma.cleaning.Partition;
import edu.isi.karma.cleaning.ProgramRule;
import edu.isi.karma.cleaning.TNode;
import edu.isi.karma.cleaning.UtilTools;

public class Prober {
	public static ArrayList<String> track1 = new ArrayList<String>();
	public static String target;
	public static MultiIndex records = new MultiIndex();
	public static String PartitionDisplay1(Vector<Partition> vp) {
		String res = "";
		for (Partition p : vp) {
			res += "Partition: " + p.label + "\n";
			for (int i = 0; i < p.tarNodes.size(); i++) {
				String line = UtilTools.print(p.orgNodes.get(i)) + "|"
						+ UtilTools.print(p.tarNodes.get(i));
				res += line + "\n";
			}
		}
		return res;
	}
	public static void trackProgram(Vector<Partition> pars, ProgramRule r)
	{
		for(Partition p: pars)
		{
			String rule = r.getStringRule(p.label);
			for(Vector<TNode> nodes: p.orgNodes)
			{
				String exp = UtilTools.print(nodes);
				String[] keys = {exp};
				records.add(Arrays.asList(keys), rule);
			}
		}
	}
	public static void displayProgram()
	{
		List<String> pList = records.getPathes();
		for(String line: pList)
		{
			System.out.println(""+line);
		}
	}
}
