package Test;

import java.util.ArrayList;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.mahout.math.jet.stat.Probability;

import edu.isi.karma.cleaning.Research.Prober;
import arq.tokens;

public class JavaTest {
	public String print(ArrayList<long[]> res, ArrayList<String[]> itertime, ArrayList<Long> data) {
		String x = "";
		for (long[] elem : res) {
			x += String.format("%d,%d\n", elem[0], elem[1]);
		}
		String y = "";
		y = itertime.get(itertime.size()-1)[0]+"\n"+itertime.get(itertime.size()-1)[1]+"\n";
		
		String z = "";
		for(Long l: data)
		{
			z += l+",";
		}
		return x+"\n"+y+"\n"+z+"\n";
	}

	public ArrayList<String[]> autoDataFeed(int length) {
		ArrayList<String[]> res = new ArrayList<String[]>();
		String[] alp = new String[26];
		for (char c = 'a'; c <= 'z'; c++) {
			alp[c - 'a'] = "" + c;
		}
		String org = "";
		String tar = "";
		for (int i = 0; i < length; i++) {
			org += alp[i].toUpperCase() + alp[i];
			tar += alp[i].toUpperCase() + " ";
		}
		org = org.trim();
		tar = tar.trim();
		for (int i = 0; i < length; i++) {
			// replace the lower case letters with $
			int cnt = 0;
			String torg = org;
			while (cnt < i) {
				torg = torg.replaceFirst("[a-z]", "\\$");
				cnt++;
			}
			String[] xelm = { torg, tar };
			res.add(xelm);
		}
		return res;
	}

	public static void main(String[] args) {
		DOMConfigurator
				.configure("/Users/bowu/projects/DataCleaning/log4j2.xml");

		/*
		 * String[] e1 = { "AaBbCcDdEeFf", "A C B E D F" }; String[] e2 = {
		 * "A$BbCcDdEeFf", "A C B E D F" }; String[] e3 = { "$a$bCcDdEeFf",
		 * "A C B E D F" }; String[] e4 = { "$a$b$cDdEeFf", "A C B E D F" };
		 * String[] e5 = { "$a$b$c$dEeFf", "A C B E D F" }; String[] e6 = {
		 * "$a$b$c$d$eFf", "A C B E D F" }; String[] e7 = { "$a$b$c$d$e$f",
		 * "A C B E D F" }; ArrayList<String[]> exps = new
		 * ArrayList<String[]>(); //load data exps.add(e1); exps.add(e2);
		 * exps.add(e3); exps.add(e4); exps.add(e5); exps.add(e6); exps.add(e7);
		 */
		JavaTest jt = new JavaTest();
		String leng = "";
		String[] e1 = { "AaBbCcDdEeFf", "A C B E D F" };
		ArrayList<String[]> tp = new ArrayList<String[]>();
		tp.add(e1);
		edu.isi.karma.cleaning.Research.Test.test_seq(tp);
		ArrayList<String[]> x = new ArrayList<String[]>();
		ArrayList<ArrayList<Long>> changed = new ArrayList<ArrayList<Long>>();
		for (int l = 8; l < 26; l++) {
		ArrayList<String[]> exps = jt.autoDataFeed(l);
		ArrayList<long[]> timeArrayList = new ArrayList<long[]>();
		ArrayList<Long> elex = new ArrayList<Long>();
			for (int i = 8; i < 9; i++) {
				ArrayList<String[]> testExp = new ArrayList<String[]>(
						exps.subList(0, i));
				long t1 = System.currentTimeMillis();
				String r1 = "";
				//r1 = edu.isi.karma.cleaning.Research.Test.test_seq(testExp);
				long t2 = System.currentTimeMillis();
				String r2 = edu.isi.karma.cleaning.Research.Test.test_adaptive_seq(testExp);
				long cnum = Prober.adaptedProg;
				elex.add(cnum);
				Prober.adaptedProg = 0;
				long t3 = System.currentTimeMillis();
				long span1 = t2 - t1;
				long span2 = t3 - t2;
				long[] elem = { span1, span2 };
				String[] xe = {r1,r2};
				timeArrayList.add(elem);
				x.add(xe);
			}
			leng += "Length: "+l+"\n"+jt.print(timeArrayList, x,elex)+"\n";
		}
		System.out.println(""+leng);

	}
	
}
