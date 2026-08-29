package com.fossmapping.model;

public record RouteRequest(
        String startAddress,
        String endAddress
) {
}