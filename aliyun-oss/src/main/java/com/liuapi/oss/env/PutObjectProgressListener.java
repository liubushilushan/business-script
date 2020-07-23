package com.liuapi.oss.env;

import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class PutObjectProgressListener implements ProgressListener {
    private long bytesWritten = 0;
    private boolean succeed = false;
    private final String objectName;
    private long totalBytes;

    public PutObjectProgressListener(String objectName, long totalBytes) {
        this.objectName = objectName;
        this.totalBytes = totalBytes;
    }

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType) {
            case TRANSFER_STARTED_EVENT:
                log.info("[{}]开始上传......", objectName);
                break;
            case REQUEST_BYTE_TRANSFER_EVENT:
                long bytes = progressEvent.getBytes();
                this.bytesWritten += bytes;
                break;
            case TRANSFER_COMPLETED_EVENT:
                this.succeed = true;
                log.info("[{}]上传成功, 累计上传 {} 字节", objectName, totalBytes);
                break;
            case TRANSFER_FAILED_EVENT:
                int percent = (int) (this.bytesWritten * 100.0 / this.totalBytes);
                log.info("[{}]上传失败, 累计上传进度 {} %", percent);
                break;
            default:
                break;
        }
    }

    public boolean isSucceed() {
        return succeed;
    }

    public static void main(String[] args) {
    }

}