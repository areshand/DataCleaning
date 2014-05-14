package edu.isi.karma.cleaning;

import java.util.Vector;
//used to carry information accross iterations

public class Messager {
	double[] weights = null;
	Vector<Vector<String[]>> cm_constr = new Vector<Vector<String[]>>();
	public Messager()
	{
		
	}
	public void updateCM_Constr(Vector<Vector<String[]>> conVector)
	{
		this.cm_constr.addAll(conVector);
	}
	public void updateWeights(double[] weights)
	{
		this.weights = weights;
	}
	public double[] getWeights()
	{
		return this.weights;
	}
	public Vector<Vector<String[]>> getConstraints()
	{
		return cm_constr;
	}
}
