package com.mike.agentstoryteller.controller;

import com.mike.agentstoryteller.dto.StoryRequest;
import com.mike.agentstoryteller.dto.StoryResponse;
import com.mike.agentstoryteller.interfaces.StoryAgent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/story")
public class StoryController {
    private final StoryAgent agent;
    public StoryController(StoryAgent agent) { this.agent = agent; }

    @PostMapping
    public StoryResponse create(@RequestBody StoryRequest req) {
        return agent.makeStory(req);
    }
}
