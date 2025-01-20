package vn.com.huudan.ai.service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.Neo4jVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.neo4j.core.ReactiveNeo4jClient;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class KnowledgeGraphProcessorService {

    private static final String KEY_VECTOR_CONTEXT = "vectorContext";

    private static final String KEY_KNOWLEDGE_GRAPH_CONTEXT = "knowledgeGraphContext";

    private static final String KEY_QUESTION = "question";

    private static final int TOP_K = 4;

    private static final double SIMILARITY_THRESHOLD = 0.7;

    private final PromptTemplate knowledgeGraphAugmentationTemplate;

    @Autowired
    private Neo4jVectorStore vectorStore;

    @Autowired
    @Qualifier("openAIService")
    private AIService aiService;

    @Autowired
    private ReactiveNeo4jClient neo4jClient;

    @Autowired
    private CypherGeneratorService cypherGeneratorService;

    public KnowledgeGraphProcessorService() {
        var template = new ClassPathResource("prompts/rag-knowledge-graph-template.st");
        knowledgeGraphAugmentationTemplate = new PromptTemplate(template);
    }

    private String retrieveVectorStoreContext(String userPrompt, int topK) {
        var similarDocuments = vectorStore.similaritySearch(
                SearchRequest.defaults()
                        .withQuery(userPrompt)
                        .withTopK(topK > 0 ? topK : TOP_K)
                        .withSimilarityThreshold(SIMILARITY_THRESHOLD));

        var customContext = new StringBuilder();

        similarDocuments.forEach(doc -> customContext.append(doc.getContent()).append("\n"));

        return customContext.toString();
    }

    private Mono<String> queryFromKnowledgeGraph(String cypher) {
        var sb = new StringBuilder();

        return neo4jClient.query(cypher)
                .fetch()
                .all()
                .collectList()
                .flatMap(result -> {
                    if (result.isEmpty()) {
                        return Mono.just("");
                    }

                    var resultHeader = String.join(",", result.getFirst().keySet());
                    sb.append(resultHeader).append("\n");

                    return Flux.fromIterable(result)
                            .flatMap(record -> {
                                try (
                                        var writer = new StringWriter();
                                        var csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
                                    csvPrinter.printRecord(record.values());
                                    sb.append(writer.toString());
                                } catch (Exception e) {
                                    return Mono.error(e);
                                }
                                return Mono.empty();
                            })
                            .collectList()
                            .map(list -> sb.toString());
                })
                .onErrorResume(error -> {
                    error.printStackTrace();
                    return Mono.just("");
                });
    }

    private String retrieveKnowledgeGraphContext(String userPrompt) {
        var cypher = cypherGeneratorService.generateCypherQuery(userPrompt);
        String result = "";

        try {
            result = queryFromKnowledgeGraph(cypher).toFuture().get(3, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String augmentUserPrompt(String originalUserPrompt, String vectorContext, String knowledgeGraphContext) {
        var templateMap = new HashMap<String, Object>();

        templateMap.put(KEY_QUESTION, originalUserPrompt);
        templateMap.put(KEY_VECTOR_CONTEXT, vectorContext);
        templateMap.put(KEY_KNOWLEDGE_GRAPH_CONTEXT, knowledgeGraphContext);

        return knowledgeGraphAugmentationTemplate.render(templateMap);
    }

    public String generateRAGResponse(String systemPrompt, String userPrompt, int topK) {
        var vectorContext = retrieveVectorStoreContext(userPrompt, topK);
        var knowledgeGraphContext = retrieveKnowledgeGraphContext(userPrompt);
        var augmentedUserPrompt = augmentUserPrompt(userPrompt, vectorContext, knowledgeGraphContext);

        return aiService.generateBasicResponse(systemPrompt, augmentedUserPrompt);
    }

    public Flux<String> streamRAGResponse(String systemPrompt, String userPrompt, int topK) {
        var vectorContext = retrieveVectorStoreContext(userPrompt, topK);
        var knowledgeGraphContext = retrieveKnowledgeGraphContext(userPrompt);
        var augmentedUserPrompt = augmentUserPrompt(userPrompt, vectorContext, knowledgeGraphContext);

        return aiService.streamBasicResponse(systemPrompt, augmentedUserPrompt);
    }

}
