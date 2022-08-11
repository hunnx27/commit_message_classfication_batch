package main.java.module_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import main.java.dto.Commit;

public class GitLogReadAndWriteSample {

	public static void main(String[] args) throws IOException {
		
		// 입력
		String DIR = "D:\\\\develop2\\\\workspace_ai\\\\elasticsearch\\";
		File file = new File(DIR + ""
				+ ".out");
		BufferedReader reader = new BufferedReader(new FileReader(file), 16*1024);
		String str;
//		List<String> list = new ArrayList<>();
		Commit tmp = null;
		List<Commit> commits = new ArrayList<>();
		boolean isFileStart = false;
		boolean isTxtStart = true;
		String tmpStr = "";
		while((str = reader.readLine()) != null){
			//for(int i=0; i<list.size(); i++) {
//				String txt = list.get(i);
				String txt = str;
				
				if(txt.matches("####START####.*")) {
					String txtArr[] = txt.split("####START####");
					if(tmp != null) tmp.setTxt(tmpStr);//마지막값입력
					tmp = new Commit(txtArr[1], new ArrayList());
					tmpStr = new String("");
					commits.add(tmp);
					isFileStart = false;
					isTxtStart=true;
				} else if("####END####".equals(txt)){
					isFileStart = true;
					tmpStr += "";
				} else if(isFileStart) {
					tmp.getFileList().add(txt.trim());
				}else {
					if(!isTxtStart) {
						tmpStr += "     ";
					}
					tmpStr += txt.trim();
					isTxtStart = false;
				}
//			}
		}
		if(tmp != null) tmp.setTxt(tmpStr);//마지막값입력
		System.out.println(commits.size());
		
		// 출력
//		BufferedWriter bw = new BufferedWriter(new FileWriter(DIR + "githistory_converted2.out"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(DIR + "githistory_converted3.out"));
		boolean isFirst = true;
		for(Commit commit : commits) {
			if(!isFirst) {
				bw.newLine();
			}
			isFirst=false;
			//String files = "[" + commit.getFileList().stream().map(data -> "'"+data+"'").collect(Collectors.joining(",")) + "]";
			//String line = String.format("%s,%s,%s", commit.getHash(), commit.getTxt(), files);
			String line = String.format("%s||||%s", commit.getHash(), commit.getTxt());
			bw.write(line);
			bw.flush();
		}
		bw.close();
		
	}

	
}
