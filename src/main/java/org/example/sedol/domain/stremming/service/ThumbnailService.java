package org.example.sedol.domain.stremming.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;

import javax.xml.transform.Source;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

@Service
public class ThumbnailService {

	private final String ROOT_FILE_PATH;
	private final String THUMBNAIL_DIR = "thumbnail";

	public ThumbnailService (@Value("${media.file.path}") String filePath) {
		this.ROOT_FILE_PATH = filePath;
	}

	public Resource getFile(String streamKey) throws MalformedURLException {
		Path thumbnailDir = Paths.get(ROOT_FILE_PATH.replace("\"", "").replace("\\", "/"),
			THUMBNAIL_DIR.replace("\"", "").replace("\\", "/"),  streamKey + "_thumb" + ".jpg").normalize();

		Resource resource = new UrlResource(thumbnailDir.toUri());
		if (resource.exists()) {
			return resource;
		} else {
			throw new RuntimeException("File not Found " + thumbnailDir);
		}
	}

	public String getContentType(Resource resource) {
		String contentType = null;
		try {
			Path path = resource.getFile().toPath(); // Resource에서 File 객체를 얻고, Path 변환
			contentType = Files.probeContentType(path);
		} catch (IOException e) {
			contentType = "application/octat-stream";
		}

		return contentType;
	}

	public String getEncodedFileName(Resource resource) {
		String encodedFileName = null;

		try {
			encodedFileName = URLEncoder.encode(Objects.requireNonNull(resource.getFilename()), StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			encodedFileName = Base64.getEncoder()
				.encodeToString(resource.getFilename().getBytes(StandardCharsets.UTF_8));
			encodedFileName = "=?UTF-8?B?" + encodedFileName + "?=";
		}

		return encodedFileName;
	}
}
