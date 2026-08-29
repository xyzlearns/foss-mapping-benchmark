package com.fossmapping.model;

import jakarta.persistence.*;

@Entity
public class BenchmarkResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long benchmarkCaseId;

    private String provider;

    private double distance;
    private double duration;

    public BenchmarkResult() {
    }

    // getters and setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBenchmarkCaseId() {
        return benchmarkCaseId;
    }

    public void setBenchmarkCaseId(Long benchmarkCaseId) {
        this.benchmarkCaseId = benchmarkCaseId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}


