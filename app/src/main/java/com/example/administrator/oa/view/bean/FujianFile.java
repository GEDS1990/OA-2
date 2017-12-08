package com.example.administrator.oa.view.bean;

import java.io.File;

public class FujianFile {
    private String fileName;
    private String fileSize;
    private File recordFile;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public File getRecordFile() {
		return recordFile;
	}
	public void setRecordFile(File recordFile) {
		this.recordFile = recordFile;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
}
