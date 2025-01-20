package vn.com.huudan.ai.temporal.workflow;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.springframework.ai.document.Document;

import vn.com.huudan.ai.temporal.activity.UrlOneIndexingActivities;
import vn.com.huudan.ai.temporal.constant.TemporalTaskQueues;
import vn.com.huudan.ai.temporal.workflow.input.IndexingWorkflowInput;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

@WorkflowImpl(taskQueues = TemporalTaskQueues.URL_ONE_INDEXING)
public class UrlOneIndexingWorkflowImpl implements UrlOneIndexingWorkflow {

        private final RetryOptions RETRY_OPTIONS = RetryOptions.newBuilder()
                        .setInitialInterval(Duration.ofSeconds(2))
                        .setBackoffCoefficient(1.5)
                        .setMaximumAttempts(10)
                        .build();

        private final ActivityOptions ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(120))
                        .setScheduleToCloseTimeout(Duration.ofSeconds(300))
                        .setTaskQueue(TemporalTaskQueues.URL_ONE_INDEXING)
                        .setRetryOptions(RETRY_OPTIONS)
                        .build();

        private final UrlOneIndexingActivities ACTIVITIES = Workflow.newActivityStub(
                UrlOneIndexingActivities.class,
                        ACTIVITY_OPTIONS);

        @Override
        public List<Document> indexUrlOneData(IndexingWorkflowInput source) throws IOException {
                return ACTIVITIES.processDocument(source.resourcePath(), source.keywords());
        }

}
