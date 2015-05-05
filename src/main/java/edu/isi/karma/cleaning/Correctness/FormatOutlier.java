package edu.isi.karma.cleaning.Correctness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.crypto.Data;

import org.junit.Test;

import edu.isi.karma.cleaning.DataPreProcessor;
import edu.isi.karma.cleaning.DataRecord;
import edu.isi.karma.cleaning.UtilTools;
import edu.isi.karma.cleaning.features.Feature;
import edu.isi.karma.cleaning.features.RecordFeatureSet;

/*
 * unseen formats detections: 
 * distance to the class center and find the largest distance as the threshold
 * 
 * boundary formats:
 * difference of distances to two classes are below 5% 
 *
 * record = [id, org, tar, label]
 * 
 */

public class FormatOutlier {
	private int funid = 1;
	private HashMap<String, double[]> cmeans = new HashMap<String, double[]>();
	private HashMap<String, double[]> mean_var = new HashMap<String, double[]>();
	private double[] dmetric= null;
	private DataPreProcessor dpp;
	private ArrayList<DataRecord> allrecords = new ArrayList<DataRecord>();
	public FormatOutlier(ArrayList<DataRecord> records, double[] dmetric)
	{
		//dmetric = UtilTools.initArray(dmetric, 1.0);
		this.dmetric = dmetric;
		allrecords = records;
		getMeanandDists(records, dmetric);
		
	}
	private String genKey(DataRecord record){
		return record.origin + " " + record.transformed;
	}
	private double getMedian(double[] values){
		if(values.length == 0){
			return 0.0;
		}
		double ret = 0.0;
		if(values.length %2 == 0){
			int ind = values.length /2 -1;
			ret = (values[ind] + values[ind+1]) *1.0/2;
		}
		else{
			int ind = values.length /2 ;
			ret = values[ind];
		}
		return ret;
	}
	//identify the mean vector of each cluster
	private void getMeanandDists(ArrayList<DataRecord> records, double[] dmetric)
	{
		HashMap<String, ArrayList<DataRecord>> tmp = new HashMap<String,ArrayList<DataRecord>>();
		ArrayList<String> allpairs = new ArrayList<String>();
		for(DataRecord rec:records)
		{
			
			String pair = genKey(rec);
			allpairs.add(pair);
			if(tmp.containsKey(rec.classLabel))
			{
				tmp.get(rec.classLabel).add(rec);
			}
			else
			{
				ArrayList<DataRecord> x = new ArrayList<DataRecord>();
				x.add(rec);
				tmp.put(rec.classLabel, x);
			}
		}
		dpp = new DataPreProcessor(allpairs);
		dpp.run();
		// find the means 
		for(String key: tmp.keySet())
		{
			ArrayList<double[]> classVectors = new ArrayList<double[]>();
			ArrayList<DataRecord> tdata = tmp.get(key);
			for(DataRecord dr: tdata){
				String pair = genKey(dr);
				classVectors.add(dpp.getFeatureArray(pair));
			}
			double[] tmean = UtilTools.sum(classVectors);
			tmean = UtilTools.produce(1.0/tdata.size(), tmean);
			cmeans.put(key, tmean);
			
			double d_median = 0;
			double d_mu = 0;
			double[] alldists = new double[tdata.size()];
			for(int i =0; i< tdata.size(); i++)
			{
				alldists[i]= UtilTools.distance(dpp.getFeatureArray(genKey(tdata.get(i))), tmean, dmetric);
			}
			Arrays.sort(alldists);
			d_median = getMedian(alldists);
			for(int i =0; i< tdata.size(); i++)
			{
				d_mu += Math.pow(UtilTools.distance(dpp.getFeatureArray(genKey(tdata.get(i))), tmean, dmetric)-d_median, 2);
			}
			d_mu = Math.sqrt(d_mu/tdata.size());
			double[] x = {d_median,d_mu};
			mean_var.put(key, x);	
		}
		
		//Prober.printFeatureandWeight(tmp, cmeans, dmetric);
	}
	public ArrayList<DataRecord> getAllOutliers(){
		ArrayList<DataRecord> ret = new ArrayList<DataRecord>();
		for(DataRecord record:allrecords){
			if(isoutlier(record)){
				ret.add(record);
			}
		}
		return ret;
	}
	private boolean isoutlier(DataRecord record) {
		double[] vector = dpp.getFeatureArray(genKey(record));			
		double dist = UtilTools.distance(vector, cmeans.get(record.classLabel), dmetric);
		//difference STRICTLY bigger than 2 standard deviations [68, 95, 99.7] rule
		System.out.println(record.origin+": "+dist +", "+ Arrays.toString(mean_var.get(record.classLabel)));
		if(Math.abs(dist - mean_var.get(record.classLabel)[0]) > 1.8*mean_var.get(record.classLabel)[1])
		{
			return true;
		}
		return false;
	}
	public static void main(String[] args){
		String[] dat = { "A", "AA", "B", "BB" };
		String[] tst = { "A", "AA", "B", ".........." };
		ArrayList<DataRecord> records = new ArrayList<DataRecord>();
		for(int i = 0; i < dat.length; i++){
			DataRecord r = new DataRecord();
			r.classLabel = "c";
			r.origin = dat[i];
			r.transformed = tst[i]; 
			records.add(r);
		}
		double[] dmetric = null;
		FormatOutlier fout = new FormatOutlier(records, dmetric);
		Collection<DataRecord> ret = fout.getAllOutliers();
		for(DataRecord rec:ret){
			System.out.println(String.format("%s, %s", rec.origin, rec.transformed));
		}
		
	}
}
