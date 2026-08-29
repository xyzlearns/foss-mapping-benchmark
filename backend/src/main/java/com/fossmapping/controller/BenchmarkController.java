package com.fossmapping.controller;

import com.fossmapping.model.BenchmarkReport;
import com.fossmapping.model.BenchmarkResult;
import com.fossmapping.model.RouteResult;
import com.fossmapping.service.BenchmarkService;
import com.fossmapping.service.GoogleRoutesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/benchmarks")
public class BenchmarkController {

    private final BenchmarkService benchmarkService;
    private final GoogleRoutesService googleRoutesService;

    public BenchmarkController(BenchmarkService benchmarkService,GoogleRoutesService googleRoutesService) {
        this.benchmarkService = benchmarkService;
        this.googleRoutesService = googleRoutesService;
    }

    @PostMapping("/{caseId}/run")
    public BenchmarkReport run(@PathVariable Long caseId) {
        return benchmarkService.run(caseId);
    }

    @PostMapping("/run-all")
    public List<BenchmarkReport> runAll() {
        return benchmarkService.runAll();
    }

    @GetMapping("/google")
    public RouteResult google(
            @RequestParam double startLongitude,
            @RequestParam double startLatitude,
            @RequestParam double endLongitude,
            @RequestParam double endLatitude) {

        return googleRoutesService.getRoute(
                startLongitude,
                startLatitude,
                endLongitude,
                endLatitude
        );
    }
}