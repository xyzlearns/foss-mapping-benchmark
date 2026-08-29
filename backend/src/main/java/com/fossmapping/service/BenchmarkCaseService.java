package com.fossmapping.service;

import com.fossmapping.model.BenchmarkCase;
import com.fossmapping.repository.BenchmarkCaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BenchmarkCaseService {

    private final BenchmarkCaseRepository repository;

    public BenchmarkCaseService(BenchmarkCaseRepository repository) {
        this.repository = repository;
    }

    public BenchmarkCase save(BenchmarkCase benchmarkCase) {
        return repository.save(benchmarkCase);
    }

    public List<BenchmarkCase> findAll() {
        return repository.findAll();
    }
}