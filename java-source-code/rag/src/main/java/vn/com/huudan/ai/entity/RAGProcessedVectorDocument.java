package vn.com.huudan.ai.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

@Table("rag_processed_vector_documents")
public class RAGProcessedVectorDocument implements Persistable<UUID> {

    @Id
    private UUID processedDocumentId;

    private String sourcePath;

    private String hash;

    private OffsetDateTime firstProcessedAt;

    private OffsetDateTime lastProcessedAt;

    public RAGProcessedVectorDocument() {

    }

    public RAGProcessedVectorDocument(UUID processedDocumentId, String sourcePath, String hash,
            OffsetDateTime firstProcessedAt, OffsetDateTime lastProcessedAt) {
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
        return "RAGProcessedVectorDocument [processedDocumentId=" + processedDocumentId + ", sourcePath=" + sourcePath
                + ", hash=" + hash + ", firstProcessedAt=" + firstProcessedAt + ", lastProcessedAt=" + lastProcessedAt
                + "]";
    }

    @Override
    @Nullable
    public UUID getId() {
        if (this.processedDocumentId == null) {
            this.processedDocumentId = UUID.randomUUID();
        }

        return this.processedDocumentId;
    }

    @Override
    public boolean isNew() {
        return this.processedDocumentId == null;
    }

}
