package com.fossmapping.repository;

import com.fossmapping.model.BenchmarkResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BenchmarkResultRepository
        extends JpaRepository<BenchmarkResult, Long> {
}