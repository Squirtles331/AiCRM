package com.aicrm.module.file.storage;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * MinIO 对象存储实现（aicrm.storage.type=minio 时启用）
 * <p>启动时自动创建 bucket；对象名按 yyyy/MM/dd 目录分片。
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "aicrm.storage.type", havingValue = "minio")
public class MinioFileStorage implements FileStorage {

    private final StorageProperties properties;
    private MinioClient client;

    public MinioFileStorage(StorageProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() throws Exception {
        StorageProperties.Minio minio = properties.getMinio();
        this.client = MinioClient.builder()
                .endpoint(minio.getEndpoint())
                .credentials(minio.getAccessKey(), minio.getSecretKey())
                .build();
        boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(minio.getBucket()).build());
        if (!exists) {
            client.makeBucket(MakeBucketArgs.builder().bucket(minio.getBucket()).build());
            log.info("MinIO bucket {} 已创建", minio.getBucket());
        }
        log.info("MinIO 文件存储初始化完成：{}", minio.getEndpoint());
    }

    @Override
    public String upload(byte[] data, String fileName, String contentType) throws IOException {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String ext = extractExtension(fileName);
        String objectName = datePath + "/" + UUID.randomUUID().toString().replace("-", "") + ext;
        try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            client.putObject(PutObjectArgs.builder()
                    .bucket(properties.getMinio().getBucket())
                    .object(objectName)
                    .contentType(contentType)
                    .stream(in, data.length, -1)
                    .build());
        } catch (Exception e) {
            throw new IOException("MinIO 上传失败: " + e.getMessage(), e);
        }
        return objectName;
    }

    @Override
    public byte[] download(String key) throws IOException {
        try (InputStream in = client.getObject(GetObjectArgs.builder()
                .bucket(properties.getMinio().getBucket())
                .object(key)
                .build()); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            in.transferTo(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IOException("MinIO 下载失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String key) throws IOException {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.getMinio().getBucket())
                    .object(key)
                    .build());
        } catch (Exception e) {
            throw new IOException("MinIO 删除失败: " + e.getMessage(), e);
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
