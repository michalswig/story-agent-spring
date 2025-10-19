package com.mike.agentstoryteller.agent;

import com.mike.agentstoryteller.dto.StoryRequest;
import com.mike.agentstoryteller.dto.StoryResponse;
import com.mike.agentstoryteller.interfaces.StoryAgent;
import com.mike.agentstoryteller.interfaces.StoryModel;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class SimpleStoryAgent implements StoryAgent {
    private final StoryModel model;
    public SimpleStoryAgent(StoryModel model) { this.model = model; }

    @Override
    public StoryResponse makeStory(StoryRequest req) {
        var words = (req.words() == null) ? java.util.List.<String>of() : req.words();
        var tone  = (req.tone() == null || req.tone().isBlank()) ? "gentle and cheerful" : req.tone();
        int max   = (req.maxWords() > 0 && req.maxWords() <= 250) ? req.maxWords() : 120;

        // 1) Build a clear prompt (works for stub and real LLM)
        String prompt = """
      Write a SHORT children's story.
      Tone: %s.
      Must include these words naturally: %s.
      Keep it under %d words. End with a one-sentence moral labeled 'Moral:'.
      """.formatted(
                tone,
                words.stream().collect(Collectors.joining(", ")),
                max
        );

        // 2) “Generate”
        String raw = model.generate(prompt);

        // 3) Simple polish pass (ensure words appear; trim length)
        String story = ensureContains(raw, words);
        story = limitWords(story, max);

        String title = makeTitle(words, tone);
        String moral = "Moral: Be kind, be curious, and help others.";
        return new StoryResponse(title, story, moral);
    }

    private static String ensureContains(String text, java.util.List<String> words) {
        String finalText = text;
        var missing = words.stream().filter(w -> !finalText.toLowerCase().contains(w.toLowerCase())).toList();
        if (!missing.isEmpty()) {
            text += " " + String.join(", ", missing) + " appeared, too.";
        }
        return text;
    }
    private static String limitWords(String text, int max) {
        var tokens = text.split("\\s+");
        if (tokens.length <= max) return text;
        return String.join(" ", java.util.Arrays.copyOf(tokens, max)) + "...";
    }
    private static String makeTitle(java.util.List<String> words, String tone) {
        String base = words.isEmpty() ? "A Little Adventure" : "The " + String.join(" & ", words);
        return base + " (" + tone + ")";
    }
}
