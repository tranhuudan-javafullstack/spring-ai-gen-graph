package vn.com.huudan.ai.api.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.com.huudan.ai.api.request.AIPromptRequest;
import vn.com.huudan.ai.service.AIService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai")
@Validated
public class AIBasicApi {

    @Autowired
    @Qualifier("openAIService")
    private AIService aiService;

    @PostMapping(path = "/v1/basic", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> basicAI(@RequestBody @Valid AIPromptRequest request) {
        var response = aiService.generateBasicResponse(request.systemPrompt(), request.userPrompt());

        return Mono.just(response);
    }

    @PostMapping(path = "/v1/basic/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> basicStreamAI(@RequestBody @Valid AIPromptRequest request) {
        var response = aiService.streamBasicResponse(request.systemPrompt(), request.userPrompt());

        return response;
    }

}
