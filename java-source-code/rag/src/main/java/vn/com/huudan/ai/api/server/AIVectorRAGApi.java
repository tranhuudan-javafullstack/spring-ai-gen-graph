package vn.com.huudan.ai.api.server;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.com.huudan.ai.api.request.AIPromptRequest;
import vn.com.huudan.ai.api.request.VectorIndexingRequestFromFilesystem;
import vn.com.huudan.ai.api.request.VectorIndexingRequestFromURL;
import vn.com.huudan.ai.api.response.BasicIndexingResponse;
import vn.com.huudan.ai.service.RAGVectorIndexingService;
import vn.com.huudan.ai.service.RAGVectorProcessorService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai/rag/vector")
@Validated
public class AIVectorRAGApi {

        @Autowired
        private RAGVectorIndexingService ragIndexingService;

        @Autowired
        private RAGVectorProcessorService ragProcessorService;

        @PostMapping(path = "/indexing/document/filesystem", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<BasicIndexingResponse> indexDocumentFromFilesystem(
                        @RequestBody @Valid VectorIndexingRequestFromFilesystem request) {
                var indexedDocuments = ragIndexingService.indexDocumentFromFilesystem(
                                request.path(),
                                request.keywords());

                return ResponseEntity.ok(
                                new BasicIndexingResponse(true,
                                                "Document successfully indexed as " + indexedDocuments.size()
                                                                + " chunks"));
        }

        @PostMapping(path = "/indexing/document/url", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<BasicIndexingResponse> indexDocumentFromURL(
                        @RequestBody @Valid VectorIndexingRequestFromURL request) {
                var indexedDocuments = ragIndexingService.indexDocumentFromURL(
                                request.url(),
                                request.keywords());

                return ResponseEntity.ok(
                                new BasicIndexingResponse(true,
                                                "Document successfully indexed as " + indexedDocuments.size()
                                                                + " chunks"));
        }

        @PostMapping(path = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
        public Mono<String> vectorRAG(@RequestBody @Valid AIPromptRequest request,
                        @RequestParam(name = "top-k", required = false, defaultValue = "0") int topK) {
                var response = ragProcessorService.generateRAGResponse(request.systemPrompt(),
                                request.userPrompt(), topK);

                return Mono.just(response);
        }

        @PostMapping(path = "/ask/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
        public Flux<String> vectorStreamRAG(@RequestBody @Valid AIPromptRequest request,
                        @RequestParam(name = "top-k", required = false, defaultValue = "0") int topK) {
                return ragProcessorService.streamRAGResponse(request.systemPrompt(),
                                request.userPrompt(), topK);
        }

        @PostMapping(path = "/indexing/document/filesystem/reactive", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public Mono<ResponseEntity<BasicIndexingResponse>> indexDocumentFromFilesystemReactive(
                        @RequestBody @Valid VectorIndexingRequestFromFilesystem request) throws IOException {
                var indexedDocuments = ragIndexingService.indexDocumentFromFilesystemReactive(
                                request.path(),
                                request.keywords());

                return indexedDocuments.map(splittedDocuments -> ResponseEntity.ok(
                                new BasicIndexingResponse(true,
                                                "Document successfully indexed as " + splittedDocuments.size()
                                                                + " chunks")))
                                .defaultIfEmpty(
                                                ResponseEntity.ok(new BasicIndexingResponse(false,
                                                                "Document indexing failed")));
        }

        @PostMapping(path = "/indexing/document/url/reactive", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public Mono<ResponseEntity<BasicIndexingResponse>> indexDocumentFromURLReactive(
                        @RequestBody @Valid VectorIndexingRequestFromURL request) throws IOException {
                var indexedDocuments = ragIndexingService.indexDocumentFromURLReactive(
                                request.url(),
                                request.keywords());

                return indexedDocuments.map(splittedDocuments -> ResponseEntity.ok(
                                new BasicIndexingResponse(true,
                                                "Document successfully indexed as " + splittedDocuments.size()
                                                                + " chunks")))
                                .defaultIfEmpty(
                                                ResponseEntity.ok(new BasicIndexingResponse(false,
                                                                "Document indexing failed")));
        }
        
}
