package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import com.example.AthleteService;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.stream.Collectors;

@RestController
public class AthleteController {

    private final AthleteService athleteService;

    public AthleteController(AthleteService athleteService) {
        this.athleteService = athleteService;
    }

    @GetMapping("/api/player")
    public ResponseEntity<?> getPlayer(@RequestParam String name) {
        Map<String, AttributeValue> profileAttr = athleteService.getAthleteProfileByName(name);

        if (profileAttr == null || profileAttr.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No athlete found with name: " + name);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        // Extract athleteId FIRST
        int athleteId = safeInt(profileAttr.get("AthleteID"));
        int jerseyNumber = safeInt(profileAttr.get("athleteJerseyNum"));
        int height = safeHeight(profileAttr.get("athleteHeight"));
        int weight = safeInt(profileAttr.get("athleteWeight"));

        System.out.println("=== DEBUG: Profile Data ===");
        System.out.println("Parsed height: " + height + " inches");
        System.out.println("Parsed weight: " + weight + " lbs");

        Map<String, Object> profile = new HashMap<>();
        profile.put("AthleteID", athleteId);
        profile.put("Name", safeString(profileAttr.get("athleteName")));
        profile.put("Team", safeString(profileAttr.get("athleteTeam")));
        profile.put("Position", safeString(profileAttr.get("athletePosition")));
        profile.put("JerseyNumber", jerseyNumber);
        profile.put("Height", height);
        profile.put("Weight", weight);

        Map<String, AttributeValue> avgAttr = athleteService.getAthleteAvg(athleteId);
        Map<String, Object> averages = new HashMap<>();
        if (avgAttr != null && !avgAttr.isEmpty()) {
            averages.put("PPG", safeInt(avgAttr.get("athletePPG")));
            averages.put("RPG", safeInt(avgAttr.get("athleteRPG")));
            averages.put("APG", safeInt(avgAttr.get("athleteAPG")));
            averages.put("SPG", safeInt(avgAttr.get("athleteSPG")));
            averages.put("AvgFTP", safeInt(avgAttr.get("athleteAvgFTP")));
            averages.put("AvgTPP", safeInt(avgAttr.get("athleteAvgTPP")));
        } else {
            averages.put("error", "No average stats found");
        }

        Map<String, AttributeValue> last5Attr = athleteService.getAthleteLastFive(athleteId);
        
        Map<String, Object> lastFive = new HashMap<>();
        if (last5Attr != null && !last5Attr.isEmpty()) {
            lastFive.put("LastPoints", safeIntList(last5Attr.get("lastPoints")));
            lastFive.put("LastRebounds", safeIntList(last5Attr.get("lastRebounds")));
            lastFive.put("LastAssists", safeIntList(last5Attr.get("lastAssists")));
            lastFive.put("LastSteals", safeIntList(last5Attr.get("lastSteals")));
            lastFive.put("LastFTP", safeIntList(last5Attr.get("lastFTP")));
            lastFive.put("LastTPP", safeIntList(last5Attr.get("lastTPP")));
        } else {
            System.out.println("last5Attr is null or empty!");
            lastFive.put("LastPoints", java.util.Arrays.asList(0, 0, 0, 0, 0));
            lastFive.put("LastRebounds", java.util.Arrays.asList(0, 0, 0, 0, 0));
            lastFive.put("LastAssists", java.util.Arrays.asList(0, 0, 0, 0, 0));
            lastFive.put("LastSteals", java.util.Arrays.asList(0, 0, 0, 0, 0));
            lastFive.put("LastFTP", java.util.Arrays.asList(0, 0, 0, 0, 0));
            lastFive.put("LastTPP", java.util.Arrays.asList(0, 0, 0, 0, 0));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("profile", profile);
        response.put("avg", averages);
        response.put("lastFive", lastFive);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Helper method for height (handles string format like "6-5" or number)
    private int safeHeight(AttributeValue attr) {
        if (attr == null) return 0;
        
        // Try as number first
        if (attr.n() != null) {
            try {
                return Integer.parseInt(attr.n());
            } catch (NumberFormatException e) {
                // Ignore and try string
            }
        }
        
        // Try as string (format: "6-5")
        if (attr.s() != null) {
            try {
                String[] parts = attr.s().split("-");
                int feet = Integer.parseInt(parts[0]);
                int inches = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                return (feet * 12) + inches;
            } catch (Exception e) {
                return 0;
            }
        }
        
        return 0;
    }

    // Helper methods
    private int safeInt(AttributeValue attr) {
        if (attr == null) return 0;
        
        // Try as number first
        if (attr.n() != null) {
            try {
                return Integer.parseInt(attr.n());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        
        // Try as string
        if (attr.s() != null) {
            try {
                return Integer.parseInt(attr.s());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        
        return 0;
    }

    private String safeString(AttributeValue attr) {
        return attr != null && attr.s() != null ? attr.s() : "";
    }

    private List<Integer> safeIntList(AttributeValue attr) {
        // Check if attribute exists and has values - it's a LIST, not a Number Set!
        if (attr == null || attr.l() == null || attr.l().isEmpty()) {
            return java.util.Arrays.asList(0, 0, 0, 0, 0);
        }
        
        List<Integer> result = attr.l().stream()
                .map(item -> {
                    try {
                        // Each item in the list is an AttributeValue with a number
                        return Integer.parseInt(item.n());
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .collect(Collectors.toList());
        
        // If the list is empty after parsing, return default zeros
        if (result.isEmpty()) {
            return java.util.Arrays.asList(0, 0, 0, 0, 0);
        }
        
        return result;
    }
}