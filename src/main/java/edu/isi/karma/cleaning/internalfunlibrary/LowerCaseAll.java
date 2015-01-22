package edu.isi.karma.cleaning.internalfunlibrary;

import java.util.Vector;

import edu.isi.karma.cleaning.TNode;
import edu.isi.karma.cleaning.UtilTools;

public class LowerCaseAll implements TransformFunction {

	@Override
	public boolean convertable(Vector<TNode> sour, Vector<TNode> dest) {
		String ss = this.convert(sour);
		String ts = UtilTools.print(dest);
		if(ss.compareTo(ts) == 0)
		{
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public String convert(Vector<TNode> sour) {
		String ret = "";
		for(TNode t: sour){
			ret += t.text.toLowerCase();
		}
		return ret;
	
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "Lowercase";
	}

}
