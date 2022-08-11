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
