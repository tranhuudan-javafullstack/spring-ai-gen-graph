package vn.com.huudan.ai.temporal.workflow;

import java.util.List;

import org.springframework.ai.document.Document;

import vn.com.huudan.ai.temporal.workflow.input.IndexingWorkflowInput;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface FinancialIndexingWorkflow {

    @WorkflowMethod
    List<Document> indexFinancialData(IndexingWorkflowInput source);

}
