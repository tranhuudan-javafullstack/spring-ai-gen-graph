package vn.com.huudan.ai.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import vn.com.huudan.ai.entity.RAGProcessedVectorDocumentChunk;

import reactor.core.publisher.Flux;

public interface RAGProcessedVectorDocumentChunkRepository extends R2dbcRepository<RAGProcessedVectorDocumentChunk, String> {

    Flux<RAGProcessedVectorDocumentChunk> findByProcessedDocumentId(UUID processedDocumentId);

}
