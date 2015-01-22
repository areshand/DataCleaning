package edu.isi.karma.cleaning.internalfunlibrary;

import java.util.Collection;
import java.util.HashMap;


public class InternalTransformationLibrary {
	HashMap<String, TransformFunction> funcs = new HashMap<String, TransformFunction>();
	public InternalTransformationLibrary(){
		//add all the functions
		ExactEqual equal = new ExactEqual();
		funcs.put(equal.getId(), equal);
		CaptializeAll cap = new CaptializeAll();
		funcs.put(cap.getId(), cap);
		FirstLettersOfWords fword = new FirstLettersOfWords();
		funcs.put(fword.getId(), fword);
		LowerCaseAll lca = new LowerCaseAll();
		funcs.put(lca.getId(), lca);
		UpperCaseAll uca = new UpperCaseAll();
		funcs.put(uca.getId(), uca);
	}
	public Collection<String> getAllIDs()
	{
		return funcs.keySet();
	}
	public Collection<TransformFunction> getAllFuncs()
	{
		return funcs.values();
	}
	public TransformFunction getFunc(String Id)
	{
		if(funcs.containsKey(Id))
		{
			return funcs.get(Id);
		}
		else
		{
			return null;
		}
	}
	
}
