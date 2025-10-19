package com.mike.agentstoryteller.interfaces;

import com.mike.agentstoryteller.dto.StoryRequest;
import com.mike.agentstoryteller.dto.StoryResponse;

public interface StoryAgent { StoryResponse makeStory(StoryRequest req); }
