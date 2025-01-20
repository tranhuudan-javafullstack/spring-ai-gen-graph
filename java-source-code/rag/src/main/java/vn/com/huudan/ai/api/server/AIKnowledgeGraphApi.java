package vn.com.huudan.ai.api.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.com.huudan.ai.api.request.AIPromptRequest;
import vn.com.huudan.ai.service.KnowledgeGraphProcessorService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai/rag/kg")
@Validated
public class AIKnowledgeGraphApi {

    @Autowired
    private KnowledgeGraphProcessorService kgProcessorService;

    @PostMapping(path = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> knowledgeGraphRAG(@RequestBody @Valid AIPromptRequest request,
            @RequestParam(name = "top-k", required = false, defaultValue = "0") int topK) {
        var response = kgProcessorService.generateRAGResponse(request.systemPrompt(), request.userPrompt(),
                topK);

        return Mono.just(response);
    }

    @PostMapping(path = "/ask/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> knowledgeGraphStreamRAG(@RequestBody @Valid AIPromptRequest request,
            @RequestParam(name = "top-k", required = false, defaultValue = "0") int topK) {
        return kgProcessorService.streamRAGResponse(request.systemPrompt(), request.userPrompt(),
                topK);
    }

}
