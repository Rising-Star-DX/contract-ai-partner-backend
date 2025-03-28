package com.partner.contract.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RequiredArgsConstructor
public class CustomMultipartFile implements MultipartFile {
    //private final File file;
    private final byte[] fileContent;
    private final String fileName;
    private final String contentType;

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return fileContent.length == 0;
    }

    @Override
    public long getSize() {
        return fileContent.length;
    }

    @Override
    public byte[] getBytes() {
        return (fileContent!=null) ? fileContent : new byte[0];
    }

    @Override
    public InputStream getInputStream() {
        return (fileContent!=null) ? new ByteArrayInputStream(fileContent) : new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public void transferTo(File dest) throws IOException {
        byte[] dataToWrite = (fileContent!=null) ? fileContent : new byte[0];
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(dataToWrite);
        }
    }
}
