package com.fossmapping.model;

public record ProviderComparison(
        String provider,
        double distanceDifferencePercent,
        double durationDifferencePercent
) {
}