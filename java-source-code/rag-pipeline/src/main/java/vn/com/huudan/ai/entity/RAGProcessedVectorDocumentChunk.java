package vn.com.huudan.ai.entity;

import java.util.UUID;

public class RAGProcessedVectorDocumentChunk {

    private String chunkId;

    private UUID processedDocumentId;

    public RAGProcessedVectorDocumentChunk() {
    }

    public RAGProcessedVectorDocumentChunk(String chunkId, UUID processedDocumentId) {
        this.chunkId = chunkId;
        this.processedDocumentId = processedDocumentId;
    }

    public String getChunkId() {
        return chunkId;
    }

    public void setChunkId(String chunkId) {
        this.chunkId = chunkId;
    }

    public UUID getProcessedDocumentId() {
        return processedDocumentId;
    }

    public void setProcessedDocumentId(UUID processedDocumentId) {
        this.processedDocumentId = processedDocumentId;
    }

    @Override
    public String toString() {
        return "RAGProcessedVectorDocumentChunk{" +
                "chunkId='" + chunkId + '\'' +
                ", processedDocumentId=" + processedDocumentId +
                '}';
    }

}
