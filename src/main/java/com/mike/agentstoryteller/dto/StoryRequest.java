package com.mike.agentstoryteller.dto;

public record StoryRequest(java.util.List<String> words, String tone, int maxWords) {}
