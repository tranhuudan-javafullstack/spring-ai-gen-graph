package vn.com.huudan.ai.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

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
import org.springframework.util.Assert;

import vn.com.huudan.ai.entity.RAGProcessedVectorDocument;
import vn.com.huudan.ai.entity.RAGProcessedVectorDocumentChunk;
import vn.com.huudan.ai.rag.indexing.RAGTikaDocumentReader;
import vn.com.huudan.ai.repository.RAGProcessedVectorDocumentChunkRepository;
import vn.com.huudan.ai.repository.RAGProcessedVectorDocumentRepository;

import reactor.core.publisher.Mono;

@Service
public class RAGVectorIndexingService {

    private static final Logger LOG = LoggerFactory.getLogger(RAGVectorIndexingService.class);

    @Autowired
    private RAGTikaDocumentReader tikaDocumentReader;

    @Autowired
    private TextSplitter textSplitter;

    @Autowired
    private Neo4jVectorStore vectorStore;

    private static final String CUSTOM_KEYWORDS_METADATA_KEY = "custom_keywords";

    @Autowired
    private RAGProcessedVectorDocumentRepository processedVectorDocumentRepository;

    @Autowired
    private RAGProcessedVectorDocumentChunkRepository processedVectorDocumentChunkRepository;

    private void addCustomMetadata(Document document, List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return;
        }

        Assert.notNull(document, "Document must not be null");

        document.getMetadata().putAll(Map.of(CUSTOM_KEYWORDS_METADATA_KEY, keywords));
    }

    private List<Document> processDocument(Resource resource, List<String> keywords) {
        Assert.isTrue(resource != null && resource.exists(), "Resource must not be null and must exist");

        var parsedDocuments = tikaDocumentReader.readFrom(resource);
        var splittedDocuments = textSplitter.split(parsedDocuments);

        splittedDocuments.forEach(document -> addCustomMetadata(document, keywords));

        vectorStore.add(splittedDocuments);

        LOG.info("Original document splitted into {} chunks and saved to vector store", splittedDocuments.size());

        return splittedDocuments;
    }

    public List<Document> indexDocumentFromFilesystem(
            String sourcePath, List<String> keywords) {
        var resource = new FileSystemResource(sourcePath);

        return processDocument(resource, keywords);
    }

    public List<Document> indexDocumentFromURL(
            String sourcePath, List<String> keywords) {
        try {
            var resource = new UrlResource(sourcePath);

            return processDocument(resource, keywords);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL: " + sourcePath, e);
        }
    }

    private String calculateHash(Resource resource) {
        var lastModified = 0l;

        try {
            lastModified = resource.lastModified();
        } catch (Exception e) {
            LOG.warn("Failed to get last modified date for resource: {}", resource, e);
        }

        var original = resource.getDescription().toLowerCase() + "//" + lastModified;

        return DigestUtils.sha256Hex(original);
    }

    @SuppressWarnings("null")
    private Mono<List<Document>> processDocumentReactive(Resource resource, List<String> keywords) {
        Assert.isTrue(resource != null && resource.exists(), "Resource must not be null and must exist");

        var existingFromDb = processedVectorDocumentRepository.findBySourcePath(resource.getDescription());
        var now = OffsetDateTime.now();

        return existingFromDb
                .defaultIfEmpty(
                        new RAGProcessedVectorDocument(null, resource.getDescription(), StringUtils.EMPTY, now, now))
                .flatMap(element -> {
                    var hash = calculateHash(resource);

                    if (StringUtils.equals(element.getHash(), hash)) {
                        LOG.info("Document with hash {} already indexed", hash);

                        return Mono.empty();
                    }

                    var parsedDocuments = tikaDocumentReader.readFrom(resource);
                    var splittedDocuments = textSplitter.split(parsedDocuments);

                    splittedDocuments.forEach(document -> addCustomMetadata(document, keywords));

                    vectorStore.add(splittedDocuments);

                    LOG.info("Original document splitted into {} chunks and saved to vector store",
                            splittedDocuments.size());

                    element.setHash(hash);
                    element.setLastProcessedAt(now);

                    try {
                        processedVectorDocumentChunkRepository
                                .findByProcessedDocumentId(element.getProcessedDocumentId())
                                .subscribe(chunk -> {
                                    processedVectorDocumentChunkRepository.deleteById(chunk.getChunkId()).subscribe();
                                    vectorStore.delete(List.of(chunk.getChunkId()));
                                });

                        processedVectorDocumentRepository.save(element).subscribe(
                                savedDocument -> {
                                    splittedDocuments.forEach(chunk -> {
                                        var chunkEntity = new RAGProcessedVectorDocumentChunk(chunk.getId(),
                                                savedDocument.getProcessedDocumentId());

                                        processedVectorDocumentChunkRepository.save(chunkEntity).subscribe();
                                    });
                                });
                    } catch (Exception e) {
                        LOG.error("Failed to save processed document to database", e);
                        return Mono.empty();
                    }

                    return Mono.just(splittedDocuments);
                });
    }

    public Mono<List<Document>> indexDocumentFromFilesystemReactive(String sourcePath, List<String> keywords)
            throws IOException {
        var resource = new FileSystemResource(sourcePath);

        return processDocumentReactive(resource, keywords);
    }

    public Mono<List<Document>> indexDocumentFromURLReactive(String url, List<String> keywords) throws IOException {
        try {
            var resource = new UrlResource(url);

            return processDocumentReactive(resource, keywords);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("Invalid URL: %s", url), e);
        }
    }

}
