package edu.isi.karma.cleaning;

public class DataRecord implements Comparable{
	public static final String nonexist = "none";
	public static final String unassigned = "none";
	public String id = nonexist;
	public String origin = "";
	public String transformed = "";
	public String target = "";
	public String classLabel = unassigned;
	public double value = 0.0;
	public String toString(){
		String ret = String.format("%s, %s, %s", id, origin, transformed);
		return ret;
	}
	public int compareTo(Object other) {
		DataRecord cmp = (DataRecord)other;
		if(this.value > cmp.value){
			return -1;			
		}
		else if(this.value == cmp.value){
			return 0;
		}
		else{
			return 1;
		}
	}
}
