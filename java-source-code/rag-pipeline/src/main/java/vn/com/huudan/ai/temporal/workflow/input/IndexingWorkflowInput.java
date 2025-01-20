package vn.com.huudan.ai.temporal.workflow.input;

import java.util.List;

public record IndexingWorkflowInput(
        String resourcePath,
        List<String> keywords) {

}
