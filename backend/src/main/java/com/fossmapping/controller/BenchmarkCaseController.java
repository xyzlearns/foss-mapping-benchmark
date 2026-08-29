package com.fossmapping.controller;

import com.fossmapping.model.BenchmarkCase;
import com.fossmapping.service.BenchmarkCaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/benchmark-cases")
public class BenchmarkCaseController {

    private final BenchmarkCaseService service;

    public BenchmarkCaseController(BenchmarkCaseService service) {
        this.service = service;
    }

    @PostMapping
    public BenchmarkCase create(@RequestBody BenchmarkCase benchmarkCase) {
        return service.save(benchmarkCase);
    }

    @GetMapping
    public List<BenchmarkCase> getAll() {
        return service.findAll();
    }
}