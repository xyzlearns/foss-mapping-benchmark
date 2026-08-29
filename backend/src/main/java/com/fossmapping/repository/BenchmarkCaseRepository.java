package com.fossmapping.repository;

import com.fossmapping.model.BenchmarkCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BenchmarkCaseRepository
        extends JpaRepository<BenchmarkCase,Long>{
}