package com.mike.agentstoryteller.service;

import com.mike.agentstoryteller.interfaces.StoryModel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!llm")
public class StubStoryModel implements StoryModel {
    @Override public String generate(String prompt) {
        // Ultra-simple fake “generation”: tiny template using the prompt hash for variety
        int n = Math.abs(prompt.hashCode() % 3);
        String base = switch (n) {
            case 0 -> "Once upon a time, ";
            case 1 -> "In a sunny little town, ";
            default -> "Long ago but not too long, ";
        };
        return base + "a brave kid learned kindness and curiosity.";
    }
}
