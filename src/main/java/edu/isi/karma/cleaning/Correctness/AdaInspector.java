package edu.isi.karma.cleaning.Correctness;

import java.util.ArrayList;
import java.util.List;

import edu.isi.karma.cleaning.DataPreProcessor;
import edu.isi.karma.cleaning.DataRecord;
import edu.isi.karma.cleaning.Messager;
import edu.isi.karma.cleaning.ProgramRule;

public class AdaInspector implements Inspector {
	private ArrayList<Inspector> inspectors = new ArrayList<Inspector>();
	private List<String> inspectorNames = new ArrayList<String>();
	private List<Double> weights = new ArrayList<Double>();
	InspectorFactory factory;

	public AdaInspector() {

	}

	public void initeInspector(DataPreProcessor dpp, Messager msger, ArrayList<DataRecord> records, ArrayList<String> exampleIDs, ProgramRule program) {
		factory = new InspectorFactory(dpp, msger, records, exampleIDs, program);
		inspectors.clear();
		for (int i = 0; i < inspectorNames.size(); i++) {
			inspectors.add(factory.getInspector(inspectorNames.get(i)));
		}
	}
	public void initeParameter2(){
		//inspectorNames.add(OutlierInspector.class.getName());
		//inspectorNames.add(MultiviewInspector.class.getName());
		inspectorNames.add(MembershipAmbiguityInspector.class.getName());
		weights.add(1.0);
	}
	public void initeParameter() {
		CreatingTrainingData cdata = new CreatingTrainingData();
		ArrayList<Instance> all = cdata.runDir();
		List<String> clfs = InspectorFactory.getInspectorNames();
		AdaInspectorTrainer adaTrainer = new AdaInspectorTrainer(all.toArray(new Instance[all.size()]), clfs);
		adaTrainer.adaboost(clfs.size());
		inspectorNames = adaTrainer.classifierList;
		weights = adaTrainer.alphaList;

	}

	@Override
	public double getActionLabel(DataRecord record) {
		double ret = 0.0;
		//String line = "";
		for (int i = 0; i < inspectors.size(); i++) {
			ret += inspectors.get(i).getActionLabel(record) * weights.get(i);
			//line += "|"+inspectors.get(i).getActionLabel(record) +", "+weights.get(i);
		}
		if (ret > 0) {
			return 1;
		} else {
			return -1;
		}
	}
	public double getActionScore(DataRecord record){
		double ret = 0.0;
		//String line = "";
		for (int i = 0; i < inspectors.size(); i++) {
			ret += inspectors.get(i).getActionLabel(record) * weights.get(i);
			//line += "|"+inspectors.get(i).getActionLabel(record) +", "+weights.get(i);
		}
		return ret;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "AdaInspector";
	}
	public static void main(String[] args){
		AdaInspector aInspector = new AdaInspector();
		aInspector.initeParameter();
	}

}
