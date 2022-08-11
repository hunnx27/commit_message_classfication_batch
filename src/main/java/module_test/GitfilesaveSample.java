package main.java.module_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GitfilesaveSample {
	public static void main(String[] args) throws IOException {
		Integer[] arr = new Integer[48];
		Arrays.fill(arr, 0);
		System.out.println("###END");
		
		arr[31]++;
		arr[32]++;
		arr[47]++;
		arr[47]++;
		arr[47]++;
		arr[47]++;
		arr[47]++;
		
		System.out.println(Arrays.toString(arr));
		
		String command = "";
		command  = "git show a67920e1dcc~1:build-tools/src/main/java/org/elasticsearch/gradle/testclusters/ElasticsearchNode.java";
        Process p = Runtime.getRuntime().exec(command, null, new File("D:\\develop2\\workspace_ai\\elasticsearch"));
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line; 
        String text = "";
        while ((line = input.readLine()) != null) {
            text += line + "\n";
            System.out.println("Line: " + line);
        }
        filewrite(text, "file1.java");
        
        // 이전파일 출력
		command  = "git show a67920e1dcc:build-tools/src/main/java/org/elasticsearch/gradle/testclusters/ElasticsearchNode.java";
        p = Runtime.getRuntime().exec(command, null, new File("D:\\develop2\\workspace_ai\\elasticsearch"));
        input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        text = "";
        System.out.println(text);
        while ((line = input.readLine()) != null) {
            text += line+"\n";
            System.out.println("Line: " + line);
        }
        filewrite(text, "file2.java");
        
        // 파일 작성 
        
	}
	
	private static void filewrite(String line, String name) throws IOException {
		String DIR = "D:\\\\develop2\\\\workspace_ai\\\\elasticsearch\\";
        BufferedWriter bw = new BufferedWriter(new FileWriter(DIR + name));
		bw.write(line);
		bw.flush();
		bw.close();
	}

}
