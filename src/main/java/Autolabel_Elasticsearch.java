package main.java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import main.java.dto.Commit;

public class Autolabel_Elasticsearch {

	public static void main(String[] args) throws IOException {
		boolean isDebug = false; // 로그출력여부
		/**
		 * Git Log 파일 생성
		 */
		String DIR = "D:\\\\develop2\\\\workspace_ai\\\\elasticsearch\\";
		File file = new File(DIR + "githistory_withBody"+ ".out");
		BufferedReader reader = new BufferedReader(new FileReader(file), 16*1024);
		String str;
		Commit tmp = null;
		List<Commit> commits = new ArrayList<>();
		boolean isFileStart = false;
		boolean isTxtStart = true;
		String tmpStr = "";
		while((str = reader.readLine()) != null){
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
				if(!"".equals(txt.trim()))tmp.getFileList().add(txt.trim());
			}else {
				if(!isTxtStart) {
					tmpStr += "     ";
				}
				tmpStr += txt.trim();
				isTxtStart = false;
			}
		}
		if(tmp != null) tmp.setTxt(tmpStr);//마지막값입력
		if(isDebug) System.out.println(commits.size());
		
		int index = 0;
		/*
		 * 커밋 별 처리 
		 */
		for(Commit commit : commits) {
			System.out.println("[Elasc]index : " + ++index);
			/**
			 * 커밋관련 파일 생성
			 */
			String hash = commit.getHash();
			String hash_ = hash+"~1";
			if(isDebug) System.out.println("###" + hash + "START");
			List<String> filelist = commit.getFileList();
			int[] charr = new int[48];
			for(int i=0; i<filelist.size(); i++) {
				String filepath = filelist.get(i);
				java.nio.file.Files.createDirectories(Paths.get("D:\\\\develop2\\\\workspace_ai\\\\elasticsearch", "commits"));
				java.nio.file.Files.createDirectories(Paths.get("D:\\\\develop2\\\\workspace_ai\\\\elasticsearch", "commits", hash));
				String command = "";
				// 이전파일 출력
				command  = "git show " + hash_ + ":" + filepath;
		        Process p = Runtime.getRuntime().exec(command, null, new File("D:\\develop2\\workspace_ai\\elasticsearch"));
		        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        String line; 
		        String text = "";
		        while ((line = input.readLine()) != null) {
		            text += line + "\n";
//		            if(isDebug) System.out.println("Line: " + line);
		        }
		        String file1 = "commits\\" + hash + "\\" + hash+"_" + i + "_v0";
		        filewrite(text, file1);
		        // 현재파일 출력
		        command  = "git show " + hash + ":" + filepath;
		        p = Runtime.getRuntime().exec(command, null, new File("D:\\develop2\\workspace_ai\\elasticsearch"));
		        input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        text = "";
		        if(isDebug) System.out.println(text);
		        while ((line = input.readLine()) != null) {
		            text += line+"\n";
//		            if(isDebug) System.out.println("Line: " + line);
		        }
		        String file2 = "commits\\" + hash + "\\" + hash+"_" + i + "_v1";
		        filewrite(text, file2);
		        
		        /**
		         * 소스 변경 내용 추출
		         */
		        File left = new File("D:\\develop2\\workspace_ai\\elasticsearch\\" + file1);
				File right = new File("D:\\develop2\\workspace_ai\\elasticsearch\\" + file2);
				FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
				try {
				    distiller.extractClassifiedSourceCodeChanges(left, right); 
				} catch(Exception e) {
				    System.err.println("Warning: error while change distilling. " + e.getMessage());
				}
				List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
				if(isDebug) System.out.println(changes.size());
				if(changes != null) {
				    for(SourceCodeChange change : changes) {
				    	charr[change.getChangeType().ordinal()]++;
				    	//if(isDebug) System.out.println(change.getChangeType().ordinal() + " : " + change.getChangeType() + " : " + change.toString());
				    }
				}
		        
			}
			commit.setCharr(charr);
			if(isDebug) System.out.println(Arrays.toString(charr));
			if(isDebug) System.out.println("###" + hash + "END");
		}
		
		if(isDebug) System.out.println(commits.size());
		
		/**
		 * 정리된 파일 오토 레이블링(Create CSV)
		 */
		String[] bugFix_keys = {"fix","test","issu","use","fail","bug_fix","report","error","npe"};
		String[] perfImpr_keys = {"test","remov","use","refactor","method","chang","add","improv","expand"};
		String[] featAdd_keys = {"support","add","implement","allow","use","method","test","set","chang","feat","new"};
	
		BufferedWriter bw = new BufferedWriter(new FileWriter(DIR + "githistory_converted3.out"));
		boolean isFirst = true;
		for(Commit commit : commits) {
			if(!isFirst) {
				bw.newLine();
			}
			isFirst=false;
			//String files = "[" + commit.getFileList().stream().map(data -> "'"+data+"'").collect(Collectors.joining(",")) + "]";
			//String line = String.format("%s,%s,%s", commit.getHash(), commit.getTxt(), files);
			String commitMessage = commit.getTxt().toLowerCase();
			int bugFix = Arrays.stream(bugFix_keys).anyMatch(commitMessage::contains)?1:0;
			int featAdd = Arrays.stream(featAdd_keys).anyMatch(commitMessage::contains)?1:0;
			int perfImpr = Arrays.stream(perfImpr_keys).anyMatch(commitMessage::contains)?1:0;
			int tot = bugFix + featAdd + perfImpr;
			if(tot == 0) perfImpr = 1;
			if(commitMessage.indexOf("fix")!=-1 ) {
				bugFix=1;featAdd=0;perfImpr=0;
			}else if(commitMessage.indexOf("add")!=-1) {
				bugFix=0;featAdd=0;perfImpr=1;
			}
			String line = String.format("%s||||%s||||%s||||%s||||%s", commitMessage, Arrays.toString(commit.getCharr()), bugFix, featAdd, perfImpr);
			bw.write(line);
			bw.flush();
		}
		bw.close();
		

	}
	
	private static void filewrite(String line, String name) throws IOException {
		String DIR = "D:\\\\develop2\\\\workspace_ai\\\\elasticsearch\\";
        BufferedWriter bw = new BufferedWriter(new FileWriter(DIR + name));
		bw.write(line);
		bw.flush();
		bw.close();
	}

}
