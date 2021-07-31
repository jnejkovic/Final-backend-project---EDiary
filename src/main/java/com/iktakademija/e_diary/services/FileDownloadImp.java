package com.iktakademija.e_diary.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
public class FileDownloadImp implements FileDownload {
	
	@Override
	public Set<String> listFiles(String dir) {
		return Stream.of(new File(dir).listFiles()).filter(file -> !file.isDirectory()).map(File::getName)
				.collect(Collectors.toSet());
	}

	@Override
	public byte[] downloadFile(String fileName) throws FileNotFoundException, IOException {
		File file = new File(fileName);
		byte[] fileContent = Files.readAllBytes(file.toPath());
		return fileContent;
	}

}
