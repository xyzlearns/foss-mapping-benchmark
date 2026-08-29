package com.fossmapping.model;

import java.util.List;

public record BenchmarkReport(
        Long benchmarkCaseId,
        List<BenchmarkResult> results,
        List<ProviderComparison> comparisons
) {
}