package edu.isi.karma.cleaning.Correctness;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import libsvm.svm_parameter;
import edu.isi.karma.cleaning.DataPreProcessor;
import edu.isi.karma.cleaning.features.RecordClassifier;
import edu.isi.karma.cleaning.features.RecordFeatureSet;

public class OutlierDetector {
	RecordClassifier clf;
	DataPreProcessor dpp;
	RecordFeatureSet rfs = new RecordFeatureSet();
	public OutlierDetector() {
	}

	public void train(ArrayList<String> tdata) {
		dpp = new DataPreProcessor(tdata);
		dpp.run();
		rfs.updateVocabulary(dpp.getFeatureName());
		clf = new RecordClassifier(rfs, svm_parameter.ONE_CLASS);
		try {
			for (String text : dpp.data) {
				double[] values = dpp.getNormalizedreScaledVector(text); 
				System.out.println("values: "+Arrays.toString(values));
				clf.addTrainingData(text, values, "c0");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void learnDetector(){
		rfs.updateVocabulary(dpp.getAllFeatures());
		clf.learnClassifer();		
	}

	public boolean isOutlier(String input) {
		String label = clf.getLabel(input);
		System.out.println(label);
		if (label.compareTo("c0") != 0) {
			return true;
		}
		return false;
	}

	@Test
	public void test() {
		OutlierDetector outDet = new OutlierDetector();
		String[] dat = { "A", "AA", "B", "BB" };
		String[] tst = { "A", "AA", "B", "BB", "AAAAAAAAAAAA", "." };
		ArrayList<String> data = new ArrayList<String>(Arrays.asList(dat));
		outDet.train(data);
		outDet.learnDetector();
		for (String l : tst) {
			if (outDet.isOutlier(l))
				System.out.println(l + " is an outlier");
		}
	}

}
