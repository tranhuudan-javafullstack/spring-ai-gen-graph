package vn.com.huudan.ai.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.Neo4jVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import vn.com.huudan.ai.entity.RAGProcessedVectorDocument;
import vn.com.huudan.ai.entity.RAGProcessedVectorDocumentChunk;
import vn.com.huudan.ai.rag.indexing.RAGTikaDocumentReader;

@Service
public class RAGVectorIndexingService {

    private static final String CUSTOM_KEYWORDS_METADATA_KEY = "custom_keywords";

    private static final Logger LOG = LoggerFactory.getLogger(RAGVectorIndexingService.class);

    @Autowired
    private RAGTikaDocumentReader tikaDocumentReader;

    @Autowired
    private TextSplitter textSplitter;

    @Autowired
    private Neo4jVectorStore vectorStore;

    @Autowired
    private RAGProcessedVectorDocumentService processedVectorDocumentService;

    @Autowired
    private RAGProcessedVectorDocumentChunkService processedVectorDocumentChunkService;

    private void addCustomMetadata(Document document, List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return;
        }

        Assert.notNull(document, "Document must not be null");

        document.getMetadata().put(CUSTOM_KEYWORDS_METADATA_KEY, keywords);
    }

    private String calculateHash(Resource resource) {
        var lastModified = 0l;

        try {
            lastModified = resource.lastModified();
        } catch (Exception e) {
            LOG.warn("Could not get lastModified for resource: {}", resource.getDescription());
        }

        var original = resource.getDescription().toLowerCase() + "//" + lastModified;

        return DigestUtils.sha256Hex(original);
    }

    @SuppressWarnings("null")
    @Transactional
    public List<Document> processDocument(Resource resource, List<String> keywords) {
        Assert.isTrue(resource != null && resource.exists(), "Resource must exist");

        var existingFromDb = processedVectorDocumentService.findBySourcePath(resource.getDescription());

        String resourceHash;

        try {
            resourceHash = calculateHash(resource);
        } catch (Exception e) {
            resourceHash = StringUtils.EMPTY;
        }

        if (existingFromDb.isPresent() && StringUtils.equals(existingFromDb.get().getHash(), resourceHash)) {
            LOG.info("Document already indexed: {}", resource.getDescription());
            return List.of();
        }

        var now = OffsetDateTime.now();

        var element = existingFromDb.orElse(new RAGProcessedVectorDocument(
                UUID.randomUUID(),
                resource.getDescription(),
                resourceHash,
                now,
                now));

        var parsedDocument = tikaDocumentReader.readFrom(resource);
        var splittedDocuments = textSplitter.split(parsedDocument);

        splittedDocuments.forEach(document -> {
            addCustomMetadata(document, keywords);
        });

        vectorStore.add(splittedDocuments);

        LOG.info("Original document splitted into {} chunks and saved to vector store", splittedDocuments.size());

        element.setHash(resourceHash);
        element.setLastProcessedAt(now);

        processedVectorDocumentChunkService
                .findByProcessedDocumentId(element.getProcessedDocumentId())
                .forEach(chunk -> {
                    processedVectorDocumentChunkService.deleteById(chunk.getChunkId());
                    vectorStore.delete(List.of(chunk.getChunkId()));
                });

        var savedDocument = processedVectorDocumentService.save(element);

        splittedDocuments.forEach(chunk -> {
            var chunkEntity = new RAGProcessedVectorDocumentChunk(chunk.getId(),
                    savedDocument.getProcessedDocumentId());

            processedVectorDocumentChunkService.save(chunkEntity);
        });

        return splittedDocuments;
    }

    public List<Document> indexDocumentFromFilesystem(String sourcePath, List<String> keywords) {
        var resource = new FileSystemResource(sourcePath);

        return processDocument(resource, keywords);
    }

    public List<Document> indexDocumentFromURL(String url, List<String> keywords) {
        try {
            var resource = new UrlResource(url);

            return processDocument(resource, keywords);
        } catch (Exception e) {
            throw new IllegalArgumentException("URL cannot processed: " + url, e);
        }
    }

}
