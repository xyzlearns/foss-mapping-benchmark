package com.fossmapping.service;

import com.fossmapping.model.BenchmarkResult;
import com.fossmapping.repository.BenchmarkResultRepository;
import org.springframework.stereotype.Service;

@Service
public class BenchmarkResultService {

    private final BenchmarkResultRepository repository;

    public BenchmarkResultService(BenchmarkResultRepository repository) {
        this.repository = repository;
    }

    public BenchmarkResult save(BenchmarkResult result) {
        return repository.save(result);
    }
}