package vn.com.huudan.ai.temporal.workflow.alphabuzz;

import java.time.Duration;

import vn.com.huudan.ai.temporal.activity.alphabuzz.AlphabuzzStaticGraphPopulatorActivities;
import vn.com.huudan.ai.temporal.constant.TemporalTaskQueues;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

@WorkflowImpl(taskQueues = TemporalTaskQueues.ALPHABUZZ_STATIC_GRAPH_POPULATOR)
public class AlphabuzzStaticGraphPopulatorWorkflowImpl implements AlphabuzzStaticGraphPopulatorWorkflow {

    private final RetryOptions RETRY_OPTIONS = RetryOptions.newBuilder()
            .setInitialInterval(Duration.ofSeconds(2))
            .setBackoffCoefficient(1.5)
            .setMaximumAttempts(10)
            .build();

    private final ActivityOptions ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(120))
            .setScheduleToCloseTimeout(Duration.ofSeconds(300))
            .setTaskQueue(TemporalTaskQueues.ALPHABUZZ_STATIC_GRAPH_POPULATOR)
            .setRetryOptions(RETRY_OPTIONS)
            .build();

    private final AlphabuzzStaticGraphPopulatorActivities ACTIVITIES = Workflow.newActivityStub(
            AlphabuzzStaticGraphPopulatorActivities.class,
            ACTIVITY_OPTIONS);

    @Override
    public void populateGraph() {
        ACTIVITIES.populateUser();
        ACTIVITIES.populateBuzz();
        ACTIVITIES.populateRelationship_Follow();
        ACTIVITIES.populateRelationship_Publish();
        ACTIVITIES.populateRelationship_Like();
        ACTIVITIES.populateRelationship_Republish();
    }

}
