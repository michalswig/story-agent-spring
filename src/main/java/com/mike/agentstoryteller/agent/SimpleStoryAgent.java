package com.mike.agentstoryteller.agent;

import com.mike.agentstoryteller.dto.StoryRequest;
import com.mike.agentstoryteller.dto.StoryResponse;
import com.mike.agentstoryteller.interfaces.StoryAgent;
import com.mike.agentstoryteller.interfaces.StoryModel;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SimpleStoryAgent implements StoryAgent {
    private final StoryModel model;

    public SimpleStoryAgent(StoryModel model) {
        this.model = model;
    }

    @Override
    public StoryResponse makeStory(StoryRequest req) {
        var words = (req.words() == null) ? java.util.List.<String>of() : req.words();
        var tone = (req.tone() == null || req.tone().isBlank()) ? "gentle and cheerful" : req.tone();
        int max = (req.maxWords() > 0 && req.maxWords() <= 250) ? req.maxWords() : 120;

        // 1) Build a clear prompt (works for stub and real LLM)
        String prompt = """
                Write a SHORT children's story.
                Tone: %s.
                Must include these words naturally: %s.
                Keep it under %d words. End with a one-sentence moral labeled 'Moral:'.
                """.formatted(
                tone,
                String.join(", ", words),
                max
        );

        // 2) “Generate”
        String raw = model.generate(prompt);

// pull the moral out of the generated text (if present)
        MoralSplit ms = splitMoral(raw);
        String story = ms.storyWithoutMoral();
        String moral = (ms.moralLine() != null)
                ? ms.moralLine()
                : "Moral: Be kind, be curious, and help others."; // your default

// keep your existing guards
        if (req.words() == null) throw new AssertionError();
        story = ensureContains(story, req.words());
        story = limitWords(story, max);

        String title = makeTitle(req.words(), tone);

        return new StoryResponse(title, story, moral);
    }

    static MoralSplit splitMoral(String text) {
        Pattern p = Pattern.compile("(?mi)^\\s*Moral:\\s*(.*)$");
        Matcher m = p.matcher(text);
        if (m.find()) {
            String moral = "Moral: " + m.group(1).trim();
            String cleaned = new StringBuilder(text).replace(m.start(), m.end(), "").toString();
            cleaned = cleaned.replaceAll("(?m)^\\s*$\\R?", ""); // remove empty line left behind
            return new MoralSplit(cleaned.trim(), moral);
        }
        return new MoralSplit(text.trim(), null);
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
