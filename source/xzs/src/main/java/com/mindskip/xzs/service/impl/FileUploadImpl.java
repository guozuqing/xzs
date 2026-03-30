package com.mindskip.xzs.service.impl;

import com.mindskip.xzs.service.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileUploadImpl implements FileUpload {
    private final Logger logger = LoggerFactory.getLogger(FileUpload.class);
    private static final String UPLOAD_DIR = "/home/skydomain/upload/image/";

    @Override
    public String uploadFile(InputStream inputStream, long size, String extName) {
        String ext = "";
        if (extName != null && extName.contains(".")) {
            ext = extName.substring(extName.lastIndexOf(".")).toLowerCase();
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dest = new File(UPLOAD_DIR + fileName);
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            return "/api/image/file/" + fileName;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
