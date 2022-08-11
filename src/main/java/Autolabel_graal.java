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

import com.google.common.io.Files;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import main.java.main_commit_allign.Commit;

public class Autolabel_graal {

	public static void main(String[] args) throws IOException {
		
		/**
		 * Git Log 파일 생성
		 */
		String DIR = "D:\\\\develop2\\\\workspace_ai\\\\graal\\";
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
				if(!"".equals(txt.trim()))tmp.fileList.add(txt.trim());
			}else {
				if(!isTxtStart) {
					tmpStr += "     ";
				}
				tmpStr += txt.trim();
				isTxtStart = false;
			}
		}
		if(tmp != null) tmp.setTxt(tmpStr);//마지막값입력
		System.out.println(commits.size());
		
		int index = 0;
		/*
		 * 커밋 별 처리 
		 */
		for(Commit commit : commits) {
			System.out.println("index : " + ++index);
			/**
			 * 커밋관련 파일 생성
			 */
			String hash = commit.getHash();
			String hash_ = hash+"~1";
			System.out.println("###" + hash + "START");
			List<String> filelist = commit.getFileList();
			int[] charr = new int[48];
			for(int i=0; i<filelist.size(); i++) {
				String filepath = filelist.get(i);
				java.nio.file.Files.createDirectories(Paths.get("D:\\\\develop2\\\\workspace_ai\\\\graal", "commits"));
				java.nio.file.Files.createDirectories(Paths.get("D:\\\\develop2\\\\workspace_ai\\\\graal", "commits", hash));
				String command = "";
				// 이전파일 출력
				command  = "git show " + hash_ + ":" + filepath;
		        Process p = Runtime.getRuntime().exec(command, null, new File("D:\\develop2\\workspace_ai\\graal"));
		        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        String line; 
		        String text = "";
		        while ((line = input.readLine()) != null) {
		            text += line + "\n";
//		            System.out.println("Line: " + line);
		        }
		        String file1 = "commits\\" + hash + "\\" + hash+"_" + i + "_v0";
		        filewrite(text, file1);
		        // 현재파일 출력
		        command  = "git show " + hash + ":" + filepath;
		        p = Runtime.getRuntime().exec(command, null, new File("D:\\develop2\\workspace_ai\\graal"));
		        input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        text = "";
		        System.out.println(text);
		        while ((line = input.readLine()) != null) {
		            text += line+"\n";
//		            System.out.println("Line: " + line);
		        }
		        String file2 = "commits\\" + hash + "\\" + hash+"_" + i + "_v1";
		        filewrite(text, file2);
		        
		        /**
		         * 소스 변경 내용 추출
		         */
		        File left = new File("D:\\develop2\\workspace_ai\\graal\\" + file1);
				File right = new File("D:\\develop2\\workspace_ai\\graal\\" + file2);
				FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
				try {
				    distiller.extractClassifiedSourceCodeChanges(left, right); 
				} catch(Exception e) {
				    System.err.println("Warning: error while change distilling. " + e.getMessage());
				}
				List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
				System.out.println(changes.size());
				if(changes != null) {
				    for(SourceCodeChange change : changes) {
				    	charr[change.getChangeType().ordinal()]++;
				    	//System.out.println(change.getChangeType().ordinal() + " : " + change.getChangeType() + " : " + change.toString());
				    }
				}
		        
			}
			commit.setCharr(charr);
			System.out.println(Arrays.toString(charr));
			System.out.println("###" + hash + "END");
		}
		
		System.out.println(commits.size());
		
		/**
		 * 정리된 파일 오토 레이블링(Create CSV)
		 */
		BufferedWriter bw = new BufferedWriter(new FileWriter(DIR + "githistory_converted3.out"));
		boolean isFirst = true;
		for(Commit commit : commits) {
			if(!isFirst) {
				bw.newLine();
			}
			isFirst=false;
			//String files = "[" + commit.getFileList().stream().map(data -> "'"+data+"'").collect(Collectors.joining(",")) + "]";
			//String line = String.format("%s,%s,%s", commit.getHash(), commit.getTxt(), files);
			String line = String.format("%s||||%s||||%s||||%s||||%s", commit.getTxt(), Arrays.toString(commit.getCharr()), "0", "0", "0");
			bw.write(line);
			bw.flush();
		}
		bw.close();
		

	}
	
	private static void filewrite(String line, String name) throws IOException {
		String DIR = "D:\\\\develop2\\\\workspace_ai\\\\graal\\";
        BufferedWriter bw = new BufferedWriter(new FileWriter(DIR + name));
		bw.write(line);
		bw.flush();
		bw.close();
	}
	
	static class Commit {
		
		public Commit() {};
		public Commit(String hash, ArrayList list) {
			this.hash = hash;
			this.fileList = list;
		}
		String hash;
		String txt;
		List<String> fileList;
		int[] charr;
		public String getHash() {
			return hash;
		}
		public String getTxt() {
			return txt;
		}
		public List<String> getFileList() {
			return fileList;
		}
		public void setHash(String hash) {
			this.hash = hash;
		}
		public void setTxt(String txt) {
			this.txt = txt;
		}
		public void setFileList(List<String> fileList) {
			this.fileList = fileList;
		}
		public int[] getCharr() {
			return charr;
		}
		public void setCharr(int[] charr) {
			this.charr = charr;
		}
		
	}

}
