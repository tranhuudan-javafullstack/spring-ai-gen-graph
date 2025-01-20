package vn.com.huudan.ai.temporal.activity;

import java.io.IOException;
import java.util.List;

import org.springframework.ai.document.Document;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface UrlOneIndexingActivities {

    @ActivityMethod
    List<Document> processDocument(String resourcePath, List<String> keywords) throws IOException;

}
