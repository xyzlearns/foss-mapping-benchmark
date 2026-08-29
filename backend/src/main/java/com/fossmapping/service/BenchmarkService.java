package com.fossmapping.service;

import com.fossmapping.model.*;
import com.fossmapping.repository.BenchmarkCaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BenchmarkService {

    private final BenchmarkCaseRepository caseRepository;
    private final BenchmarkResultService resultService;
    private final OsrmService osrmService;
    private final ValhallaService valhallaService;
    private final GoogleRoutesService googleRoutesService;

    public BenchmarkService(
            BenchmarkCaseRepository caseRepository,
            BenchmarkResultService resultService,
            OsrmService osrmService,
            ValhallaService valhallaService,
            GoogleRoutesService googleRoutesService) {

        this.caseRepository = caseRepository;
        this.resultService = resultService;
        this.osrmService = osrmService;
        this.valhallaService = valhallaService;
        this.googleRoutesService = googleRoutesService;
    }

    public BenchmarkReport run(Long caseId) {

        BenchmarkCase benchmarkCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Benchmark case not found"));

        RouteResult osrm = osrmService.getRoute(
                benchmarkCase.getStartLongitude(),
                benchmarkCase.getStartLatitude(),
                benchmarkCase.getEndLongitude(),
                benchmarkCase.getEndLatitude()
        );

        RouteResult valhalla = valhallaService.getRoute(
                benchmarkCase.getStartLongitude(),
                benchmarkCase.getStartLatitude(),
                benchmarkCase.getEndLongitude(),
                benchmarkCase.getEndLatitude()
        );

        RouteResult google = googleRoutesService.getRoute(
                benchmarkCase.getStartLongitude(),
                benchmarkCase.getStartLatitude(),
                benchmarkCase.getEndLongitude(),
                benchmarkCase.getEndLatitude()
        );

        BenchmarkResult osrmResult = new BenchmarkResult();
        osrmResult.setBenchmarkCaseId(caseId);
        osrmResult.setProvider("OSRM");
        osrmResult.setDistance(osrm.distance());
        osrmResult.setDuration(osrm.duration());

        BenchmarkResult valhallaResult = new BenchmarkResult();
        valhallaResult.setBenchmarkCaseId(caseId);
        valhallaResult.setProvider("VALHALLA");
        valhallaResult.setDistance(valhalla.distance());
        valhallaResult.setDuration(valhalla.duration());

        BenchmarkResult googleResult = new BenchmarkResult();
        googleResult.setBenchmarkCaseId(caseId);
        googleResult.setProvider("GOOGLE");
        googleResult.setDistance(google.distance());
        googleResult.setDuration(google.duration());

        ProviderComparison osrmComparison =
                compare("OSRM", osrm, google);

        ProviderComparison valhallaComparison =
                compare("VALHALLA", valhalla, google);



        BenchmarkResult savedOsrm = resultService.save(osrmResult);
        BenchmarkResult savedValhalla = resultService.save(valhallaResult);
        BenchmarkResult savedGoogle = resultService.save(googleResult);

        return new BenchmarkReport(
                caseId,
                List.of(savedOsrm, savedValhalla, savedGoogle),
                List.of(osrmComparison, valhallaComparison)
        );
    }

    public List<BenchmarkReport> runAll() {
        List<BenchmarkCase> cases = caseRepository.findAll();

        return cases.stream()
                .map(benchmarkCase -> run(benchmarkCase.getId()))
                .toList();
    }

    private double percentageDifference(double foss, double google) {
        return Math.abs(foss - google) / google * 100;
    }

    private ProviderComparison compare(
            String provider,
            RouteResult foss,
            RouteResult google) {

        double distanceDifference =
                Math.abs(foss.distance() - google.distance())
                        / google.distance() * 100;

        double durationDifference =
                Math.abs(foss.duration() - google.duration())
                        / google.duration() * 100;

        return new ProviderComparison(
                provider,
                distanceDifference,
                durationDifference
        );
    }
}