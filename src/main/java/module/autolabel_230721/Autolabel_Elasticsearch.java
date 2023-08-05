package main.java.module.autolabel_230721;

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

import main.java.dto.*;
import org.apache.commons.lang3.StringUtils;

public class Autolabel_Elasticsearch {

	static final int EXTRACT_SIZE = 1000;
	static final String SEP = "|";
	static final String PROJECT_NAME = "elastic";
	static final String LANGUAGE = "java";
//	static final String PROJECT_PATH = "D:\\develop2\\workspace_ai\\ai_sample_projects\\elasticsearch";
	static final String PROJECT_PATH = "D:\\develop_ai\\ai_sample_projects\\elasticsearch";
//	static final String ROOT_PATH = "D:\\\\develop2\\workspace_ai\\commit_message_classfication_batch\\";
	static final String ROOT_PATH = "D:\\develop_ai\\commit_message_classfication_batch\\";
	static final String DATASET_DIR = ROOT_PATH + "dataset\\20230721\\";
	static final String RESULT_DIR = ROOT_PATH + "result\\20230721\\";
	static final String INPUT_LOG_FILE_NAME = "githistory_" + LANGUAGE + "_" + PROJECT_NAME + "_withBody.out";

	static final String OUTPUT_NORMAL_FILE_NAME = "githistory_" + LANGUAGE + "_" + PROJECT_NAME + "_normal.out";
	static final String OUTPUT_NORMAL_GPT_INFO_NAME = "githistory_" + LANGUAGE + "_" + PROJECT_NAME + "_normal_gpt.out"; // 아직 미작업
	static final String OUTPUT_FULL_INFO_NAME = "githistory_" + LANGUAGE + "_" + PROJECT_NAME + "_fullinfo.out";

	public static void main(String[] args) throws IOException {
		boolean isDebug = true; // 로그출력여부
		/**
		 * Git Log 파일 파싱(객체화)
		 */
		File file = new File(DATASET_DIR + INPUT_LOG_FILE_NAME);
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
				if(commits.size() == EXTRACT_SIZE){
					break;
				}
				String txtArr[] = txt.split("####START####");
				if(tmp != null) tmp.setTxt(tmpStr);//마지막값입력
				tmp = new Commit(txtArr[1], new ArrayList());
				tmpStr = new String("");
				commits.add(tmp);
				//commits.add(tmp);
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
		/**
		 * 커밋 별 필요 파일 생성
		 */
		for(Commit commit : commits) {
			System.out.println("["+PROJECT_NAME+"]index : " + ++index);
			/**
			* 커밋
			*/
			String hash = commit.getHash();
			if(isDebug) System.out.println("###" + hash + "START");
			List<String> filelist = commit.getFileList();


			List<String> fileChangeList = new ArrayList<>();
			for(String filepath : filelist){
				String command  = "git diff " + hash + " " + filepath;
				Process p = Runtime.getRuntime().exec(command, null, new File(PROJECT_PATH));
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				String diff = "";
				while ((line = input.readLine()) != null) {
					diff += line + "\n";
//		            if(isDebug) System.out.println("Line: " + line);
				}
				//diff += diff+"\n\n";
				//commit.setDiffPlain(diff);
				fileChangeList.add(diff);
				// TODO
				// gpt결과 받아서 셋팅하기.
				//String gptResult = getGptResult(diff);
				//commit.setDiffGpt(gptResult);

			}
			commit.setFileChangeList(fileChangeList);
			if(isDebug) System.out.println("###" + hash + "END");
		}
		
		if(isDebug) System.out.println(commits.size());


		System.out.println("Commit Size : " + commits.size());
		/***************
		 * 레이블링 섹션
		 */

		/**
		 * (1) 정리된 파일 오토 레이블링(Create CSV) : 오직 커밋데이터 학습용 데이터
		 */
		String[] bugFix_keys = {"fix","test","issu","use","fail","bug_fix","report","error","npe"};
		String[] perfImpr_keys = {"test","remov","use","refactor","method","chang","add","improv","expand"};
		String[] featAdd_keys = {"support","add","implement","allow","use","method","test","set","chang","feat","new"};

		BufferedWriter bw = new BufferedWriter(new FileWriter(RESULT_DIR + OUTPUT_NORMAL_FILE_NAME));
		boolean isFirst = true;
//		for(Commit commit : commits) {
//			if(!isFirst) {
//				bw.newLine();
//			}
//			isFirst=false;
//			//String files = "[" + commit.getFileList().stream().map(data -> "'"+data+"'").collect(Collectors.joining(",")) + "]";
//			//String line = String.format("%s,%s,%s", commit.getHash(), commit.getTxt(), files);
//			String commitMessage = commit.getTxt()!=null? commit.getTxt().toLowerCase() : "";
//			commitMessage = commitMessage.replaceAll("\"", "\'");
//			commitMessage = "\"" + commitMessage + "\"";
//			int bugFix = Arrays.stream(bugFix_keys).anyMatch(commitMessage::contains)?1:0;
//			int featAdd = Arrays.stream(featAdd_keys).anyMatch(commitMessage::contains)?1:0;
//			int perfImpr = Arrays.stream(perfImpr_keys).anyMatch(commitMessage::contains)?1:0;
//			int tot = bugFix + featAdd + perfImpr;
//			if(tot == 0) perfImpr = 1;
//			if(commitMessage.indexOf("fix")!=-1 ) {
//				bugFix=1;featAdd=0;perfImpr=0;
//			}else if(commitMessage.indexOf("add")!=-1) {
//				bugFix=0;featAdd=0;perfImpr=1;
//			}
//			String line = String.format("%s||||%s||||%s||||%s", commitMessage, bugFix, featAdd, perfImpr);
//			bw.write(line);
//			bw.flush();
//		}
//		bw.close();

		/**
		 * (2) 정리된 파일 오토 레이블링(Create CSV) : GPT 학습용 데이터
		 */
//		bw = new BufferedWriter(new FileWriter(RESULT_DIR + OUTPUT_NORMAL_GPT_INFO_NAME));
//		isFirst = true;
//		for(Commit commit : commits) {
//			if(!isFirst) {
//				bw.newLine();
//			}
//			isFirst=false;
//			//String files = "[" + commit.getFileList().stream().map(data -> "'"+data+"'").collect(Collectors.joining(",")) + "]";
//			//String line = String.format("%s,%s,%s", commit.getHash(), commit.getTxt(), files);
//			String commitMessage = commit.getTxt()!=null? commit.getTxt().toLowerCase() : "";
//			commitMessage = commitMessage.replaceAll("\"", "\'");
//			commitMessage = "\"" + commitMessage + "\"";
//			String diffGpt = commit.getDiffGpt()!=null? commit.getDiffGpt().toLowerCase() : "";
//			diffGpt = diffGpt.replaceAll("\n", "CHAR(13)");
//			diffGpt = diffGpt.replaceAll("\r", "CHAR(13)");
//			diffGpt = diffGpt.replaceAll("\"", "\'");
//			diffGpt = "\"" + diffGpt + "\"";
//			String input = commitMessage + "\n" + diffGpt;
//			int bugFix = Arrays.stream(bugFix_keys).anyMatch(commitMessage::contains)?1:0;
//			int featAdd = Arrays.stream(featAdd_keys).anyMatch(commitMessage::contains)?1:0;
//			int perfImpr = Arrays.stream(perfImpr_keys).anyMatch(commitMessage::contains)?1:0;
//			int tot = bugFix + featAdd + perfImpr;
//			if(tot == 0) perfImpr = 1;
//			if(commitMessage.indexOf("fix")!=-1 ) {
//				bugFix=1;featAdd=0;perfImpr=0;
//			}else if(commitMessage.indexOf("add")!=-1) {
//				bugFix=0;featAdd=0;perfImpr=1;
//			}
//			String line = String.format("%s||||%s||||%s||||%s", input, bugFix, featAdd, perfImpr);
//			bw.write(line);
//			bw.flush();
//		}
//		bw.close();

		/**
		 * (3) 정리된 파일 오토 레이블링(Create CSV) : 모든 정보 포함(디버그용)
		 */
		int idx3 = 0;
		bw = new BufferedWriter(new FileWriter(RESULT_DIR + OUTPUT_FULL_INFO_NAME));
		isFirst = true;
		for(Commit commit : commits) {
			idx3++;
			System.out.println("[T3]File Write : " + idx3++);
			if(!isFirst) {
				bw.newLine();
			}
			isFirst=false;
			//String files = "[" + commit.getFileList().stream().map(data -> "'"+data+"'").collect(Collectors.joining(",")) + "]";
			//String line = String.format("%s,%s,%s", commit.getHash(), commit.getTxt(), files);
			int fileSize = commit.getFileChangeList().size();
			String hash = commit.getHash();
			String commitMessage = commit.getTxt()!=null? commit.getTxt().toLowerCase() : "";
			commitMessage = commitMessage.replaceAll("\"", "\'");
			commitMessage = commitMessage.replaceAll("\\"+SEP, "");
			commitMessage = "\"" + commitMessage + "\"";
			String diff = commit.getDiffPlain()!=null? commit.getDiffPlain().toLowerCase() : "";
			diff = diff.replaceAll("\n", "CHAR(13)");
			diff = diff.replaceAll("\r", "CHAR(13)");
			diff = diff.replaceAll("\"", "");
			diff = diff.replaceAll("'", "");
			diff = diff.replaceAll("\\"+SEP, "");
			diff = StringUtils.left(diff, 4097); // 엑셀최대컬럼수:32767, gpt맥스토큰 : 4097
			diff = "\"" + diff + "\"";
			String diffGpt = commit.getDiffGpt()!=null? commit.getDiffGpt().toLowerCase() : "";
			diffGpt = diffGpt.replaceAll("\n", "CHAR(13)");
			diffGpt = diffGpt.replaceAll("\r", "CHAR(13)");
			diffGpt = diffGpt.replaceAll("\"", "\'");
			diffGpt = diffGpt.replaceAll("\\"+SEP, "");
			diff = diff.replaceAll("\\"+SEP, "");
			diffGpt = "\"" + diffGpt + "\"";
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

			String line = String.format("%s"+ SEP +"%s"+ SEP +"%s"+ SEP +"%s"+ SEP +"%s"+ SEP +"%s", hash, commitMessage, bugFix, featAdd, perfImpr, fileSize);
			String fileChangeLine = commit.getFileChangeList().stream().reduce("",
					(a1, a2) ->{
						a2 = a2.replaceAll("\n", "CHAR(13)");
						a2 = a2.replaceAll("\r", "CHAR(13)");
						a2 = a2.replaceAll("\"", "'");
						a2 = a2.replaceAll("\\"+SEP, "");
						if(isDebug) System.out.println(a2);
						String mixedStr = a1 + SEP + "\"" + a2 + "\"";
						return mixedStr;
					}
			);
			String rsLine = line + fileChangeLine;
			bw.write(rsLine);
			bw.flush();
		}
		bw.close();


	}
	
	private static void filewrite(String line, String name) throws IOException {
//		String DIR = "D:\\\\develop2\\\\workspace_ai\\\\elasticsearch\\";
		String DIR = ROOT_PATH;
        BufferedWriter bw = new BufferedWriter(new FileWriter(DIR + name));
		bw.write(line);
		bw.flush();
		bw.close();

	}

}
