package vn.com.huudan.ai.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import vn.com.huudan.ai.entity.RAGProcessedVectorDocument;

import reactor.core.publisher.Mono;

public interface RAGProcessedVectorDocumentRepository extends R2dbcRepository<RAGProcessedVectorDocument, UUID> {

    Mono<RAGProcessedVectorDocument> findBySourcePath(String sourcePath);

}
