package vn.com.huudan.ai.service;

import java.util.HashMap;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class CypherGeneratorService {

    private static final String KEY_QUESTION = "question";

    private final PromptTemplate cypherGeneratorAlphabuzzTemplate;

    @Autowired
    @Qualifier("openAIService")
    private AIService aiService;

    public CypherGeneratorService() {
        var template = new ClassPathResource("prompts/neo4j-cypher-generator-alphabuzz.st");
        cypherGeneratorAlphabuzzTemplate = new PromptTemplate(template);
    }

    private String createPromptFromTemplate(String originalUserPrompt) {
        var templateMap = new HashMap<String, Object>();

        templateMap.put(KEY_QUESTION, originalUserPrompt);

        return cypherGeneratorAlphabuzzTemplate.render(templateMap);
    }

    public String generateCypherQuery(String userPrompt) {
        var prompt = createPromptFromTemplate(userPrompt);

        return aiService.generateBasicResponse("", prompt);
    }

}
