package vn.com.huudan.ai.api.server;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.com.huudan.ai.temporal.constant.TemporalTaskQueues;
import vn.com.huudan.ai.temporal.workflow.FinancialIndexingWorkflow;
import vn.com.huudan.ai.temporal.workflow.input.IndexingWorkflowInput;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;

@RestController
@RequestMapping("/api/temporal")
public class TemporalWorkflowApi {

    @Autowired
    private WorkflowClient workflowClient;

    @PostMapping(path = "/v1/financial", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> financialSample() {
        var workflow = workflowClient.newWorkflowStub(
                FinancialIndexingWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue(TemporalTaskQueues.FINANCIAL_INDEXING)
                        .setWorkflowId("FinancialSample")
                        .build());

        var input = new IndexingWorkflowInput(
                "src/main/resources/documents/company-financial-statement.html",
                List.of("financial-statement"));

        var documents = workflow.indexFinancialData(input);

        return ResponseEntity.ok().body(input.resourcePath() + " indexed as " + documents.size() + " documents");
    }

}
