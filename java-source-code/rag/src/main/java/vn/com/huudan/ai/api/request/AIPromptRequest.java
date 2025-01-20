package vn.com.huudan.ai.api.request;

import jakarta.validation.constraints.NotBlank;

public record AIPromptRequest(
        String systemPrompt,
        @NotBlank String userPrompt
) {

}
