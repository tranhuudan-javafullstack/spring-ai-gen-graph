package vn.com.huudan.ai.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RAGProcessedVectorDocument {

    private UUID processedDocumentId;

    private String sourcePath;

    private String hash;

    private OffsetDateTime firstProcessedAt;

    private OffsetDateTime lastProcessedAt;

    public RAGProcessedVectorDocument() {
    }

    public RAGProcessedVectorDocument(UUID processedDocumentId, String sourcePath, String hash, OffsetDateTime firstProcessedAt, OffsetDateTime lastProcessedAt) {
        this.processedDocumentId = processedDocumentId;
        this.sourcePath = sourcePath;
        this.hash = hash;
        this.firstProcessedAt = firstProcessedAt;
        this.lastProcessedAt = lastProcessedAt;
    }

    public UUID getProcessedDocumentId() {
        return processedDocumentId;
    }

    public void setProcessedDocumentId(UUID processedDocumentId) {
        this.processedDocumentId = processedDocumentId;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public OffsetDateTime getFirstProcessedAt() {
        return firstProcessedAt;
    }

    public void setFirstProcessedAt(OffsetDateTime firstProcessedAt) {
        this.firstProcessedAt = firstProcessedAt;
    }

    public OffsetDateTime getLastProcessedAt() {
        return lastProcessedAt;
    }

    public void setLastProcessedAt(OffsetDateTime lastProcessedAt) {
        this.lastProcessedAt = lastProcessedAt;
    }

    @Override
    public String toString() {
        return "RAGProcessedVectorDocument{" +
                "processedDocumentId=" + processedDocumentId +
                ", sourcePath='" + sourcePath + '\'' +
                ", hash='" + hash + '\'' +
                ", firstProcessedAt=" + firstProcessedAt +
                ", lastProcessedAt=" + lastProcessedAt +
                '}';
    }

}
