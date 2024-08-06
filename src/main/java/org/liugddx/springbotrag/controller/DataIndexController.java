package org.liugddx.springbotrag.controller;

import org.liugddx.springbotrag.service.DataIndexer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DataIndexController {

    private final DataIndexer dataIndexer;

    public DataIndexController(DataIndexer dataIndexer) {
        this.dataIndexer = dataIndexer;
    }

    @PostMapping("/data/load")
    public ResponseEntity<String> load() {
        try {
            this.dataIndexer.loadData();
            return ResponseEntity.ok("Data indexed successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while indexing data: " + e.getMessage());
        }
    }

    @GetMapping("/data/count")
    public long count() {
        return dataIndexer.count();
    }
}

