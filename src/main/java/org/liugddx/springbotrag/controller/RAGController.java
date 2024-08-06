package org.liugddx.springbotrag.controller;

import org.liugddx.springbotrag.service.RAGService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class RAGController {
    private final RAGService ragService;

    public RAGController(RAGService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/ask")
    public Map findAnswer(@RequestParam(value = "question", defaultValue = "give me the general summary on the trend report") String question) {
        String answer = this.ragService.findAnswer(question);
        Map map = new LinkedHashMap();
        map.put("question", question);
        map.put("answer", answer);
        return map;
    }
}
