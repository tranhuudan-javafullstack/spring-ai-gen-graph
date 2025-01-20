package vn.com.huudan.ai.temporal.activity;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import vn.com.huudan.ai.service.RAGVectorIndexingService;
import vn.com.huudan.ai.temporal.constant.TemporalTaskQueues;

import io.temporal.spring.boot.ActivityImpl;

@Component
@ActivityImpl(taskQueues = TemporalTaskQueues.PROSPECTUS_INDEXING)
public class ProspectusIndexingActivitiesImpl implements ProspectusIndexingActivities {

    @Autowired
    private RAGVectorIndexingService ragVectorIndexingService;

    @Override
    public List<Document> processDocument(String resourcePath, List<String> keywords) {
        var resource = new FileSystemResource(resourcePath);
        return ragVectorIndexingService.processDocument(resource, keywords);
    }

}
