package com.aicrm.module.file.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件存储配置
 * <p>type=local 使用本地磁盘存储（默认）；type=minio 使用 MinIO 对象存储。
 */
@Component
@ConfigurationProperties(prefix = "aicrm.storage")
public class StorageProperties {

    /** 存储类型：local / minio */
    private String type = "local";

    private final Local local = new Local();
    private final Minio minio = new Minio();

    public static class Local {
        /** 本地存储根目录 */
        private String path = "./data/upload";

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class Minio {
        private String endpoint = "http://localhost:9000";
        private String accessKey = "minioadmin";
        private String secretKey = "minioadmin";
        private String bucket = "aicrm";

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Local getLocal() {
        return local;
    }

    public Minio getMinio() {
        return minio;
    }
}
