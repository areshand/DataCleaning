package Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.junit.Test;
import org.python.antlr.PythonParser.return_stmt_return;

import au.com.bytecode.opencsv.CSVReader;
import edu.isi.karma.cleaning.DataPreProcessor;
import edu.isi.karma.cleaning.InterpreterType;
import edu.isi.karma.cleaning.Messager;
import edu.isi.karma.cleaning.ProgSynthesis;
import edu.isi.karma.cleaning.ProgramRule;
import edu.isi.karma.cleaning.UtilTools;
import edu.isi.karma.cleaning.Correctness.FormatFunc;
import edu.isi.karma.cleaning.Correctness.TransRecord;
import edu.isi.karma.cleaning.Correctness.ViewFunc;
import edu.isi.karma.cleaning.features.RecordClassifier;

public class TestDataProvider {
	public Collection<ProgramRule> testVarApp(ProgSynthesis progsyn, int opt)
	{
		Collection<ProgramRule> res = new ArrayList<ProgramRule>();
		if(opt == 1)
		{
			res = progsyn.run_main_all();
			System.out.println("Program number: "+res.size());
		}
		if(opt == 2)
		{
			res = progsyn.adaptive_main();
		}
		if(opt == 3)
		{
			res = progsyn.run_main();
		}
		return res;
	}
	@Test
	public void run() {
		HashMap<String, Vector<String>> records = new HashMap<String, Vector<String>>();
		String dirpath = "/Users/bowu/Research/testdata/TestSingleFile";
		File nf = new File(dirpath);
		File[] allfiles = nf.listFiles();
		RecordClassifier rcf;
		for (File f : allfiles) {
			Vector<String[]> examples = new Vector<String[]>();
			Vector<String[]> addExamples = new Vector<String[]>();
			Vector<String[]> entries = new Vector<String[]>();
			try {
				if (f.getName().indexOf(".csv") != -1
						&& f.getName().indexOf(".csv") == (f.getName().length() - 4)) {
					HashMap<String, String[]> xHashMap = new HashMap<String, String[]>();
					CSVReader cr = new CSVReader(new FileReader(f), ',', '"',
							'\0');
					String[] pair;
					int index = 0;
					Vector<String> vtmp = new Vector<String>();
					while ((pair = cr.readNext()) != null) {
						if (pair == null || pair.length <= 1)
							break;
						entries.add(pair);
						vtmp.add(pair[0]);
						String[] line = { pair[0], pair[1], "", "", "wrong" }; 
						xHashMap.put(index + "", line);
						index++;
					}
					DataPreProcessor dpp = new DataPreProcessor(vtmp);
					dpp.run();
					Messager msger = new Messager();
					if (entries.size() <= 1)
						continue;
					// identify record to provide as example
					String[] mt = {
							"<_START>" + entries.get(0)[0] + "<_END>",
							entries.get(0)[1] };
					examples.add(mt);
					while (true) // repeat as no incorrect answer appears.
					{
						xHashMap = new HashMap<String, String[]>();
						ProgSynthesis psProgSynthesis = new ProgSynthesis();
						psProgSynthesis.inite(examples, dpp, msger);
						Collection<ProgramRule> ps = this.testVarApp(psProgSynthesis, 3);
						//Collection<ProgramRule> ps = psProgSynthesis.run_main();
						msger.updateCM_Constr(psProgSynthesis.partiCluster
								.getConstraints());
						msger.updateWeights(psProgSynthesis.partiCluster.weights);

						rcf = (RecordClassifier) psProgSynthesis.classifier;
						if (ps == null) {
							System.out.println("Cannot generate any rules");
						}
						ProgramRule script = ps.iterator().next();
						boolean exit = true;
						String[] wrong = null;
						ArrayList<TransRecord> allData = new ArrayList<TransRecord>();
						for (int j = 0; j < entries.size(); j++) {
							InterpreterType worker = script
									.getRuleForValue(entries.get(j)[0]);
							String classlabel = script.getClassForValue(entries
									.get(j)[0]);
							String tmps = worker
									.execute_debug(entries.get(j)[0]);
							HashMap<String, String> dict = new HashMap<String, String>();
							dict.put("class", classlabel);
							UtilTools.StringColorCode(entries.get(j)[0], tmps,
									dict);
							String s = dict.get("Tar");
							if (isExample(entries.get(j)[0], examples)) {
								s = entries.get(j)[1];
							}
							if(s.compareTo(entries.get(j)[1])!=0)
							{
								wrong = entries.get(j);
								exit = false;
							}
							TransRecord tRecord = new TransRecord("", dict.get("Org"), dict.get("Tar"), classlabel, dpp.getFeatureArray(dict.get("Org")));
							if(dict.get("Tar").compareTo(entries.get(j)[1])==0)
							{
								tRecord.correct = "t";
							}
							else {
								tRecord.correct ="f";
							}
							allData.add(tRecord);
						}					
						double[] dists = psProgSynthesis.partiCluster.weights;
						ViewFunc vfun = new ViewFunc(allData, psProgSynthesis, script);
						FormatFunc ffun = new FormatFunc(allData, dists);
						ClusterVisualizer cView = new ClusterVisualizer();
						
						for(TransRecord r: allData)
						{
							String xString = ffun.verify(r);
							cView.addData(r, xString);
						}
						if(examples.size()%2 == 1)
						{
							cView.createAndShowGUI();
						}
						if(exit)
						{
							break;
						}
						String[] nwrong = {"<_START>" + wrong[0] + "<_END>",wrong[1] };
						examples.add(nwrong);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static boolean isExample(String var, Vector<String[]> examples) {
		boolean is = false;
		String s = "<_START>" + var + "<_END>";
		for (String[] x : examples) {
			if (s.compareTo(x[0]) == 0) {
				is = true;
				break;
			}
		}
		return is;
	}
}
