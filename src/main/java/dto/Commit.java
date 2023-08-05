package main.java.dto;

import java.util.ArrayList;
import java.util.List;

public class Commit {
	
	public Commit() {};
	public Commit(String hash, ArrayList list) {
		this.hash = hash;
		this.fileList = list;
	}
	String hash;
	String txt;
	List<String> fileList;
	int[] charr; // changeDistiller 소스변경내용 배열
	String diffPlain;
	String diffGpt;
	List<String> fileChangeList;

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

	public String getDiffPlain() {
		return diffPlain;
	}

	public void setDiffPlain(String diffPlain) {
		this.diffPlain = diffPlain;
	}

	public List<String> getFileChangeList() {
		return fileChangeList;
	}

	public void setFileChangeList(List<String> fileChangeList) {
		this.fileChangeList = fileChangeList;
	}

	public String getDiffGpt() {
		return diffGpt;
	}

	public void setDiffGpt(String diffGpt) {
		this.diffGpt = diffGpt;
	}
}
