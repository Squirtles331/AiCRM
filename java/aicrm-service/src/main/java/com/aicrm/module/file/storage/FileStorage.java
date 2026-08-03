package com.aicrm.module.file.storage;

import java.io.IOException;

/**
 * 文件存储抽象：屏蔽本地磁盘 / MinIO / OSS 差异
 * <p>实现类通过 aicrm.storage.type 条件装配，默认本地存储。
 */
public interface FileStorage {

    /**
     * 上传文件
     *
     * @param data        文件字节
     * @param fileName    原始文件名（用于推断扩展名）
     * @param contentType 内容类型，可为空
     * @return 存储 key（后续下载/删除使用）
     */
    String upload(byte[] data, String fileName, String contentType) throws IOException;

    /**
     * 下载文件字节
     */
    byte[] download(String key) throws IOException;

    /**
     * 删除文件（不存在时静默忽略）
     */
    void delete(String key) throws IOException;
}
