package vn.com.huudan.ai.temporal.workflow;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.springframework.ai.document.Document;

import vn.com.huudan.ai.temporal.activity.ProspectusIndexingActivities;
import vn.com.huudan.ai.temporal.constant.TemporalTaskQueues;
import vn.com.huudan.ai.temporal.workflow.input.IndexingWorkflowInput;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

@WorkflowImpl(taskQueues = TemporalTaskQueues.PROSPECTUS_INDEXING)
public class ProspectusIndexingWorkflowImpl implements ProspectusIndexingWorkflow {

        private final RetryOptions RETRY_OPTIONS = RetryOptions.newBuilder()
                        .setInitialInterval(Duration.ofSeconds(2))
                        .setBackoffCoefficient(1.5)
                        .setMaximumAttempts(10)
                        .build();

        private final ActivityOptions ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(120))
                        .setScheduleToCloseTimeout(Duration.ofSeconds(300))
                        .setTaskQueue(TemporalTaskQueues.PROSPECTUS_INDEXING)
                        .setRetryOptions(RETRY_OPTIONS)
                        .build();

        private final ProspectusIndexingActivities ACTIVITIES = Workflow.newActivityStub(
                        ProspectusIndexingActivities.class,
                        ACTIVITY_OPTIONS);

        @Override
        public List<Document> indexProspectusData(IndexingWorkflowInput source) throws IOException {
                return ACTIVITIES.processDocument(source.resourcePath(), source.keywords());
        }

}
