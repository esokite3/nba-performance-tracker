package com.example;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.List;
import java.util.Map;

public class AthleteTracker {
    private AthleteService service;

    // Constructor to accept AthleteService
    public AthleteTracker(AthleteService service) {
        this.service = service;
    }

    // Track a player by ID
    public void trackPlayer(int athleteId) {
        // Get profile
        Map<String, AttributeValue> profile = service.getAthleteProfile(athleteId);
        if (profile == null || profile.isEmpty()) {
            System.out.println("No profile found for athlete ID: " + athleteId);
            return;
        }

        System.out.println("=== Athlete Profile ===");
        System.out.println("ID: " + profile.get("AthleteID").n());
        System.out.println("Name: " + profile.get("Name").s());
        System.out.println("Team: " + profile.get("Team").s());
        System.out.println("Position: " + profile.get("Position").s());
        System.out.println("Jersey: " + profile.get("JerseyNumber").n());
        System.out.println("Height: " + profile.get("Height").n());
        System.out.println("Weight: " + profile.get("Weight").n());

        // Get average stats
        Map<String, AttributeValue> avg = service.getAthleteAvg(athleteId);
        if (avg != null && !avg.isEmpty()) {
            System.out.println("\n=== Average Stats ===");
            System.out.println("PPG: " + avg.get("PPG").n());
            System.out.println("RPG: " + avg.get("RPG").n());
            System.out.println("APG: " + avg.get("APG").n());
            System.out.println("SPG: " + avg.get("SPG").n());
            System.out.println("FT%: " + avg.get("AvgFTP").n());
            System.out.println("3P%: " + avg.get("AvgTPP").n());
        }

        // Get last five games
        Map<String, AttributeValue> lastFive = service.getAthleteLastFive(athleteId);
        if (lastFive != null && !lastFive.isEmpty()) {
            System.out.println("\n=== Last 5 Games ===");
            printIntList("Points: ", lastFive.get("LastPoints").ns());
            printIntList("Rebounds: ", lastFive.get("LastRebounds").ns());
            printIntList("Assists: ", lastFive.get("LastAssists").ns());
            printIntList("Steals: ", lastFive.get("LastSteals").ns());
            printIntList("FT%: ", lastFive.get("LastFTP").ns());
            printIntList("3P%: ", lastFive.get("LastTPP").ns());
        }
    }

    // Helper to print list of numbers
    private void printIntList(String label, List<String> numbers) {
        System.out.print(label);
        for (int i = 0; i < numbers.size(); i++) {
            System.out.print(numbers.get(i));
            if (i != numbers.size() - 1) System.out.print(", ");
        }
        System.out.println();
    }
}