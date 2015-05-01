package edu.isi.karma.cleaning.Research;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import edu.isi.karma.cleaning.DataPreProcessor;
import edu.isi.karma.cleaning.Messager;
import edu.isi.karma.cleaning.ProgSynthesis;
import edu.isi.karma.cleaning.ProgramRule;

public class Sample {
	String[][] examples = { { "Frishmuth, Harriet Whitney", "Harriet Whitney Frishmuth" }, { "Archipenko, Alexander", "Alexander Archipenko" } };
	String[][] test = { { "Benton, Thomas Hart", "Thomas Hart Benton" }, { "Beckwith, James Carroll", "James Carroll Beckwith" }, { "Ritman, Louis", "Louis Ritman" } };
	DataPreProcessor dpp;
	ProgSynthesis progSyn;
	Messager msger = new Messager();

	public void init() {
		ArrayList<String> vtmp = new ArrayList<String>();
		for (int i = 0; i < examples.length; i++) {
			vtmp.add(examples[i][0]);
		}
		for (int i = 0; i < test.length; i++) {
			vtmp.add(test[i][0]);
		}
		dpp = new DataPreProcessor(vtmp);
		dpp.run();
	}

	public ProgramRule learnProgram() {
		Vector<String[]> tmp = new Vector<String[]>();
		for (int i = 0; i < examples.length; i++) {
			String[] mt = { "<_START>" + examples[i][0] + "<_END>", examples[i][1] };
			tmp.add(mt);
		}
		progSyn = new ProgSynthesis();
		// msger is used to store and pass the useful data for future
		progSyn.inite(tmp, dpp, msger);
		/*
		 * to call our implementation of Gulwani's approach
		 * use this line to replace the following line.
		 * Collection<ProgramRule> ps = progSyn.run_main();
		 */
		Collection<ProgramRule> ps = progSyn.adaptive_main();
		
		
		msger.updateCM_Constr(progSyn.partiCluster.getConstraints());
		msger.updateWeights(progSyn.partiCluster.weights);
		if (ps.size() > 0) {
			return ps.iterator().next();
		} else {
			return null;
		}
	}

	public void test(ProgramRule p) {
		for (String[] t : test) {
			String trans = p.transform(t[0]);
			String line = String.format("Raw: %s, Expected: %s,  Transformed: %s", t[0], t[1], trans);
			System.out.println(line);
		}
	}
	/*The program needs about 2 seconds depending on machine to start for the first run
	 * 
	 * */
	public static void main(String[] args) {
		Sample test = new Sample();
		test.init();
		ProgramRule p = test.learnProgram();
		test.test(p);
	}
}
