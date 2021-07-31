package com.iktakademija.e_diary.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

public interface FileDownload {
	public Set<String> listFiles(String dir);
	public byte[] downloadFile(String fileName) throws FileNotFoundException, IOException;
}
