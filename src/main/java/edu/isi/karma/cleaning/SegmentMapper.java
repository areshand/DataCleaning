package edu.isi.karma.cleaning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import org.junit.Test;

import com.hp.hpl.jena.sparql.function.library.e;

import edu.isi.karma.cleaning.internalfunlibrary.InternalTransformationLibrary;
import edu.isi.karma.cleaning.internalfunlibrary.TransformFunction;

class Dataitem {
	public int[] range = { -1, -1 };
	public String funcid = "";
	public int tarpos = -1;
}

public class SegmentMapper {
	public static InternalTransformationLibrary itfl = new InternalTransformationLibrary();

	// only try to find one segment whose starting pos in target is pos
	public static Vector<Segment> findMapping(Vector<TNode> org,
			Vector<TNode> tar, int pos) {
		Vector<Segment> res = new Vector<Segment>();
		Dataitem root = new Dataitem();
		root.tarpos = pos;
		ArrayList<Dataitem> path = new ArrayList<Dataitem>();
		recursiveSearch(org, tar, root, path, res);
		return res;
	}

	public static Segment convert(ArrayList<Dataitem> path) {
		Segment seg = new Segment(null);
		return seg;
	}

	public static void recursiveSearch(Vector<TNode> org, Vector<TNode> tar,
			Dataitem root, ArrayList<Dataitem> path, Vector<Segment> repo) {
		if (root.tarpos >= tar.size()) {
			Segment seg = convert(path);
			repo.add(seg);
		}
		Vector<Dataitem> updated = makeOneMove(org, tar, root);
		for (Dataitem elem : updated) {
			ArrayList<Dataitem> newlist = new ArrayList<Dataitem>();
			newlist.addAll(path);
			newlist.add(elem);
			Dataitem child = new Dataitem();
			child.funcid = elem.funcid;
			child.tarpos = elem.tarpos + 1;
			recursiveSearch(org, tar, child, newlist, repo);
		}
		return;
	}
	//match one token in the target token seq
	public static Vector<Dataitem> makeOneMove(Vector<TNode> org,
			Vector<TNode> tar, Dataitem root) {
		int tpos = root.tarpos;
		TNode t = tar.get(tpos);
		String tstr = t.text;
		Vector<Dataitem> ret = new Vector<Dataitem>();
		for (int i = 0; i < org.size(); i++) {
			String prefix = "";
			if (root.funcid.compareTo("") == 0) {
				for (TransformFunction tf : itfl.getAllFuncs()) {
					int pos = checkOneFunction(tf, prefix, org, i, tstr);
					if (pos != -1) {
						Dataitem nd = new Dataitem();
						nd.range[0] = i;
						nd.range[1] = pos - 1;
						nd.funcid = tf.getId();
						nd.tarpos = root.tarpos;
						ret.add(nd);
					}
				}
			} else {
				int pos = checkOneFunction(itfl.getFunc(root.funcid), prefix,
						org, i, tstr);
				if (pos != -1) {
					Dataitem nd = new Dataitem();
					nd.range[0] = i;
					nd.range[1] = pos - 1;
					nd.funcid = root.funcid;
					nd.tarpos = root.tarpos;
					ret.add(nd);
				}
			}
		}
		return ret;
	}

	// return the ending pos if successful else return -1
	public static int checkOneFunction(TransformFunction tf, String prefix,
			Vector<TNode> org, int i, String tar) {
		if (i >= org.size()) {
			if (prefix.compareTo(tar) == 0) {
				return i;
			} else {
				return -1;
			}
		}
		if (prefix.compareTo(tar) == 0)
			return i;
		Vector<TNode> tNodes = new Vector<TNode>();
		tNodes.add(org.get(i));
		String tmp = prefix + tf.convert(tNodes);
		if (tar.indexOf(tmp) != 0) {
			return -1;
		} else {
			int t = checkOneFunction(tf, tmp, org, i + 1, tar);
			return t;
		}
	}

	@Test
	public void selfTest() {
		String[] s1 = { "Hello World", "W W", "Hello World" };
		String[] s2 = { "W", "W", "World" };
		for (int i = 0; i < s1.length; i++) {
			Vector<TNode> ts1 = UtilTools.convertStringtoTNodes(s1[i]);
			Vector<TNode> ts2 = UtilTools.convertStringtoTNodes(s2[i]);
			Vector<Segment> ret = SegmentMapper.findMapping(ts1, ts2, 0);
			System.out.println("" + ret);
		}

	}

}
