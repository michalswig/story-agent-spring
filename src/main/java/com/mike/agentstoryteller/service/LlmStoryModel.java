package com.mike.agentstoryteller.service;

import com.mike.agentstoryteller.interfaces.StoryModel;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("llm")
public class LlmStoryModel implements StoryModel {
    private final OpenAIClient client = OpenAIOkHttpClient.fromEnv();

    @Override
    public String generate(String prompt) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model("gpt-4o-mini")
                .addUserMessage(prompt)
                .maxCompletionTokens(200)
                .build();

        ChatCompletion chat = client.chat().completions().create(params);

        String text = chat.choices().get(0).message().content().orElse("");
        return text.trim().isEmpty()
                ? "A tiny backup tale: patience and curiosity save the day."
                : text.trim();
    }
}
