package Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestNumberofPrograms {
	
	public static void main(String[] args){
		String prog = "Firstletter(substr(value,indexOf(value,'START','NUM',1*1),indexOf(value,'NUM','BNK',1*1)))";
		String regex = "([a-zA-Z]+)\\(.+\\)";
		Matcher matcher = Pattern.compile(regex).matcher(prog);
		matcher.find();
		System.out.println(""+matcher.group(1));
	}
}
