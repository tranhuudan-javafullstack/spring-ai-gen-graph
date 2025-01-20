package vn.com.huudan.ai.api.server;

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
import vn.com.huudan.ai.api.request.BasicIndexingRequestFromFilesystem;
import vn.com.huudan.ai.api.request.BasicIndexingRequestFromURL;
import vn.com.huudan.ai.api.response.BasicIndexingResponse;
import vn.com.huudan.ai.service.RAGBasicIndexingService;
import vn.com.huudan.ai.service.RAGBasicProcessorService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai/rag/basic")
@Validated
public class AIBasicRAGApi {

        @Autowired
        private RAGBasicIndexingService ragIndexingService;

        @Autowired
        private RAGBasicProcessorService ragProcessorService;

        @PostMapping(path = "/indexing/document/filesystem", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<BasicIndexingResponse> indexDocumentFromFilesystem(
                        @RequestBody @Valid BasicIndexingRequestFromFilesystem request) {
                var indexedDocuments = ragIndexingService.indexDocumentFromFilesystem(
                                request.path(), request.outputFilename(), request.appendIfFileExists(),
                                request.keywords());

                return ResponseEntity.ok(
                                new BasicIndexingResponse(true,
                                                "Document successfully indexed as " + indexedDocuments.size()
                                                                + " chunks"));
        }

        @PostMapping(path = "/indexing/document/url", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<BasicIndexingResponse> indexDocumentFromURL(
                        @RequestBody @Valid BasicIndexingRequestFromURL request) {
                var indexedDocuments = ragIndexingService.indexDocumentFromURL(
                                request.url(), request.outputFilename(), request.appendIfFileExists(),
                                request.keywords());

                return ResponseEntity.ok(
                                new BasicIndexingResponse(true,
                                                "Document successfully indexed as " + indexedDocuments.size()
                                                                + " chunks"));
        }

        @PostMapping(path = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
        public Mono<String> basicRAG(@RequestBody @Valid AIPromptRequest request,
                        @RequestParam(name = "filename", required = true) @NotBlank String filenameForCustomContext) {
                var response = ragProcessorService.generateRAGResponse(request.systemPrompt(),
                                request.userPrompt(), filenameForCustomContext);

                return Mono.just(response);
        }

        @PostMapping(path = "/ask/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
        public Flux<String> basicStreamRAG(@RequestBody @Valid AIPromptRequest request,
                        @RequestParam(name = "filename", required = true) @NotBlank String filenameForCustomContext) {
                return ragProcessorService.streamRAGResponse(request.systemPrompt(),
                                request.userPrompt(), filenameForCustomContext);
        }

}
