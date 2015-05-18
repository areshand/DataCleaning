package edu.isi.karma.cleaning.Research;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import edu.isi.karma.cleaning.DataRecord;
import edu.isi.karma.cleaning.ProgramRule;
import edu.isi.karma.cleaning.Correctness.FormatOutlier;
import edu.isi.karma.cleaning.Correctness.MultiviewChecker;
import au.com.bytecode.opencsv.CSVReader;
class ErrorCnt{
	int runtimeerror = 0;
	int totalerror = 0;
	int totalrecord = 0;
	int recommand = 0;
	int correctrecommand = 0;
	double reductionRate = -1.0;
	double precsion = 0.0;
}

public class CollectResultStatistics {
	// collect the incorrect but successfully transformed results.
	public int all_iter_cnt = 0;
	public int correct_all_iter_cnt = 0;
	public int all_scenario_cnt = 0;		
	public int correct_all_scenario_cnt = 0;
	public MultiviewChecker viewChecker;
	public FormatOutlier formatChecker;
	public String collectIncorrects(String fpath) throws IOException {
		// read a file
		File f = new File(fpath);
		CSVReader cr = new CSVReader(new FileReader(f), ',', '"', '\0');
		String[] pair;
		ArrayList<DataRecord> allrec = new ArrayList<DataRecord>();
		Vector<String[]> allrec_v2 = new Vector<String[]>();
		int seqno = 0;
		while ((pair = cr.readNext()) != null) {
			if (pair == null || pair.length <= 1)
				break;
			DataRecord tmp = new DataRecord();
			tmp.id = seqno+"";
			tmp.origin = pair[0];
			tmp.target = pair[1];
			allrec.add(tmp);
			allrec_v2.add(pair);
			seqno ++;
		}
		assert (!allrec.isEmpty());
		Tools tool = new Tools();
		tool.init(allrec_v2);
		Vector<String[]> wrong = new Vector<String[]>();
		wrong.add(allrec_v2.get(0));
		Vector<String[]> examples = new Vector<String[]>();
		String ret = "";
		while (!wrong.isEmpty()) {
			// select the first one as an example
			String[] exp = tool.constrExample(wrong.get(0));
			System.out.println(""+Arrays.toString(exp));
			examples.add(exp);
			tool.learnProgramRule(examples);
			wrong = tool.transformSet(allrec_v2, examples);
			System.out.println("error cnt: " + wrong.size());
			int runtimeErrorcnt = getFailedCnt(wrong);
			ErrorCnt ecnt = new ErrorCnt();
			ecnt.runtimeerror = runtimeErrorcnt;
			ecnt.totalerror = wrong.size();
			ecnt.totalrecord = allrec.size();
			all_iter_cnt ++;
			assignClassLabel(allrec, tool.getProgramRule());
			if(runtimeErrorcnt == 0){
				viewChecker = new MultiviewChecker(tool.getProgramRule());
				formatChecker = new FormatOutlier(allrec, null);
				ArrayList<DataRecord> recmd = new ArrayList<DataRecord>(); 
				recmd.addAll(viewChecker.checkRecordCollection(allrec));
				//recmd.addAll(formatChecker.getAllOutliers());
				ArrayList<DataRecord> crecmd = getCorrectRecommand(recmd, wrong);
				System.out.println(""+recmd.size());
				ecnt.recommand = recmd.size();
				ecnt.correctrecommand = crecmd.size();
				ecnt.precsion = ecnt.correctrecommand * 1.0 / ecnt.recommand;
				ecnt.reductionRate = ecnt.recommand *1.0/ecnt.totalrecord;
				ret += printResult(wrong, ecnt ) + "\n";				
				if(ecnt.correctrecommand > 0){
					correct_all_iter_cnt ++;
				}
			}
			else{
				correct_all_iter_cnt ++;
			}
		}
		return ret;
	}
	public void assignClassLabel(ArrayList<DataRecord> allrec, ProgramRule prog){
		if(prog.pClassifier == null){
			return;
		}
		for(DataRecord record: allrec){
			record.classLabel = prog.pClassifier.getLabel(record.origin);
		}
	}
	public ArrayList<DataRecord> getCorrectRecommand(ArrayList<DataRecord> recmd,Vector<String[]> wrong){
		ArrayList<DataRecord> ret = new ArrayList<DataRecord>();
		for(DataRecord rec: recmd){
			boolean matched = false;
			for(String[] elem: wrong){
				if(elem[0].compareTo(rec.origin) == 0){
					matched = true;
					break;
				}
			}
			if(matched){
				ret.add(rec);
			}
		}
		return ret;
	}
	public String printResult(Vector<String[]> wrong, ErrorCnt ecnt) {
		String s = "";
		s += String.format("rt, %d, e,%d,t,%d, r,%d, cr, %d, red, %f, pre, %f", ecnt.runtimeerror, ecnt.totalerror, ecnt.totalrecord, ecnt.recommand,ecnt.correctrecommand,ecnt.reductionRate, ecnt.precsion);
		/*for (String[] e : wrong) {
			s += String.format("%s, %s, %s ||", e[0], e[1], e[2]);
		}*/
		return s;
	}

	public int getFailedCnt(Vector<String[]> wrong) {
		int cnt = 0;
		for (String[] s : wrong) {
			if (s[2].contains("_FATAL_ERROR_")) {
				cnt++;
			}
		}
		return cnt;
	}

	public static void main(String[] args) {
		String dirpath = "/Users/bowu/Research/testdata/TestSingleFile/";
		File nf = new File(dirpath);
		File[] allfiles = nf.listFiles();
		CollectResultStatistics collector = new CollectResultStatistics();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					"/Users/bowu/Research/Feedback/result.txt")));
			for (File f : allfiles) {
				if (f.getName().indexOf(".csv") != (f.getName().length() - 4)) {
					continue;
				}
				bw.write(f.getName()+"\n");
				String line = collector.collectIncorrects(f.getAbsolutePath());
				bw.write(line+"\n");
			}
			System.out.println(String.format("%d, %d", collector.correct_all_iter_cnt, collector.all_iter_cnt));
			System.out.println("Percentage of correct iteration: "+ (collector.correct_all_iter_cnt * 1.0 / collector.all_iter_cnt));
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
