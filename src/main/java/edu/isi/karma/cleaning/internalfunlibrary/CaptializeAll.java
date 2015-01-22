package edu.isi.karma.cleaning.internalfunlibrary;

import java.util.Vector;

import edu.isi.karma.cleaning.TNode;
import edu.isi.karma.cleaning.UtilTools;

public class CaptializeAll implements TransformFunction {

	@Override
	public boolean convertable(Vector<TNode> sour, Vector<TNode> dest) {
		// TODO Auto-generated method stub
		String target = UtilTools.print(dest);
		String tran = this.convert(sour);
		if(target.compareTo(tran) == 0)
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
				ret += Character.toUpperCase(t.text.charAt(0))
						+ t.text.substring(1);
			}
			return ret;
		} catch (Exception e) {
			return "";
		}
	}

	@Override
	public String getId() {
		return "Cap";
	}

}
