package com.aicrm.module.file.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地磁盘文件存储（默认实现）
 * <p>按 yyyy/MM/dd 目录分片，文件名使用 UUID 避免冲突。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "aicrm.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorage implements FileStorage {

    private final StorageProperties properties;

    @Override
    public String upload(byte[] data, String fileName, String contentType) throws IOException {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String ext = extractExtension(fileName);
        String key = datePath + "/" + UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = Paths.get(properties.getLocal().getPath(), key);
        Files.createDirectories(target.getParent());
        Files.write(target, data);
        return key;
    }

    @Override
    public byte[] download(String key) throws IOException {
        Path file = Paths.get(properties.getLocal().getPath(), key);
        return Files.readAllBytes(file);
    }

    @Override
    public void delete(String key) throws IOException {
        Path file = Paths.get(properties.getLocal().getPath(), key);
        boolean deleted = Files.deleteIfExists(file);
        if (deleted) {
            log.debug("删除本地文件 {}", key);
        }
    }

    private String extractExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(dot) : "";
    }
}
