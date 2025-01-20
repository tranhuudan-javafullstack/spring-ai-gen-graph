package vn.com.huudan.ai.service;

import java.util.HashMap;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.Neo4jVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class RAGVectorProcessorService {

    private static final String KEY_CUSTOM_CONTEXT = "customContext";

    private static final String KEY_QUESTION = "question";

    private static final int TOP_K = 4;

    private static final double SIMILARITY_THRESHOLD = 0.7;

    private final PromptTemplate basicAugmentationTemplate;

    @Autowired
    private Neo4jVectorStore vectorStore;

    @Autowired
    @Qualifier("openAIService")
    private AIService aiService;

    public RAGVectorProcessorService() {
        var ragBasicPromptTemplate = new ClassPathResource("prompts/rag-basic-template.st");
        this.basicAugmentationTemplate = new PromptTemplate(ragBasicPromptTemplate);
    }

    private String retrieveCustomContext(String userPrompt, int topK) {
        var similarDocuments = vectorStore.similaritySearch(
                SearchRequest.defaults()
                        .withQuery(userPrompt)
                        .withTopK(topK > 0 ? topK : TOP_K)
                        .withSimilarityThreshold(SIMILARITY_THRESHOLD));

        var customContext = new StringBuilder();

        similarDocuments.forEach(doc -> customContext.append(
                doc.getContent()).append("\n"));

        return customContext.toString();
    }

    private String augmentUserPrompt(String originalUserPrompt, String customContext) {
        var templateMap = new HashMap<String, Object>();

        templateMap.put(KEY_QUESTION, originalUserPrompt);
        templateMap.put(KEY_CUSTOM_CONTEXT, customContext);

        return basicAugmentationTemplate.render(templateMap);
    }

    public String generateRAGResponse(String systemPrompt, String userPrompt, int topK) {
        var customContext = retrieveCustomContext(userPrompt, topK);
        var augmentedUserPrompt = augmentUserPrompt(userPrompt, customContext);

        return aiService.generateBasicResponse(systemPrompt, augmentedUserPrompt);
    }

    public Flux<String> streamRAGResponse(String systemPrompt, String userPrompt, int topK) {
        var customContext = retrieveCustomContext(userPrompt, topK);
        var augmentedUserPrompt = augmentUserPrompt(userPrompt, customContext);

        return aiService.streamBasicResponse(systemPrompt, augmentedUserPrompt);
    }

}
