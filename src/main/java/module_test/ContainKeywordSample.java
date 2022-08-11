package main.java.module_test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContainKeywordSample {

	public static void main(String[] args) {
		List<String> fixList = Arrays.asList("fix", "comp");
		String[] fixArr = {"89004", "compd"};
		String commitMessage = "fix compilation in the rescore plugin (#89004)          add source fallback operation when looking up a the factor field added in #88735          resolves #88985";
		System.out.println(Arrays.stream(fixArr).anyMatch(commitMessage::contains));
		
	}

}
