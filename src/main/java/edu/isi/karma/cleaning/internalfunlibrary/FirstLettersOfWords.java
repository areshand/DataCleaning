package edu.isi.karma.cleaning.internalfunlibrary;

import java.util.Vector;

import edu.isi.karma.cleaning.TNode;
import edu.isi.karma.cleaning.UtilTools;

public class FirstLettersOfWords implements TransformFunction {

	@Override
	public boolean convertable(Vector<TNode> sour, Vector<TNode> dest) {
		String ss = this.convert(sour);
		String ts = UtilTools.print(dest);
		if(ss.compareTo(ts) == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public String convert(Vector<TNode> sour) {
		String ret = "";
		try {
			for (TNode t : sour) {
				ret += t.text.substring(0, 1);
			}
			return ret;
		} catch (Exception ex) {
			return "";
		}
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "Firstletter";
	}

}
