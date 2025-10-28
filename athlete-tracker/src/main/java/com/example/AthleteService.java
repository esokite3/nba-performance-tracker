package com.example;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class AthleteService {
    private final DynamoDbClient ddb;

    public AthleteService(DynamoDbClient client) {
        this.ddb = client;
    }

    // Create profile
    public void createAthleteProfile(int athleteId, String athleteName, String athleteTeam, 
                                    String athletePosition, int athleteJerseyNum, int athleteHeight, int athleteWeight) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("AthleteID", AttributeValue.builder().n(Integer.toString(athleteId)).build());
        item.put("Name", AttributeValue.builder().s(athleteName).build());
        item.put("Team", AttributeValue.builder().s(athleteTeam).build());
        item.put("Position", AttributeValue.builder().s(athletePosition).build());
        item.put("JerseyNumber", AttributeValue.builder().n(Integer.toString(athleteJerseyNum)).build());
        item.put("Height", AttributeValue.builder().n(Integer.toString(athleteHeight)).build());
        item.put("Weight", AttributeValue.builder().n(Integer.toString(athleteWeight)).build());

        ddb.putItem(PutItemRequest.builder()
                    .tableName("AthleteProfile")
                    .item(item)
                    .build());
    }

    // Create athlete avg stats
    public void createAthleteAvg(int athleteId, int athletePPG, int athleteRPG, int athleteAPG, 
                    int athleteSPG, int athleteAvgFTP, int athleteAvgTPP) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("AthleteID", AttributeValue.builder().n(Integer.toString(athleteId)).build());
        item.put("PPG", AttributeValue.builder().n(Integer.toString(athletePPG)).build());
        item.put("RPG", AttributeValue.builder().n(Integer.toString(athleteRPG)).build());
        item.put("APG", AttributeValue.builder().n(Integer.toString(athleteAPG)).build());
        item.put("SPG", AttributeValue.builder().n(Integer.toString(athleteSPG)).build());
        item.put("AvgFTP", AttributeValue.builder().n(Integer.toString(athleteAvgFTP)).build());
        item.put("AvgTPP", AttributeValue.builder().n(Integer.toString(athleteAvgTPP)).build());

        ddb.putItem(PutItemRequest.builder()
                    .tableName("AthleteAvg")
                    .item(item)
                    .build());
    }

    // create athlete last five stats
    public void createAthleteLastFive(int athleteId, int[] lastPoints, int[] lastRebounds, int[] lastAssists,
                                      int[] lastSteals, int[] lastFTP, int[] lastTPP) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("AthleteID", AttributeValue.builder().n(Integer.toString(athleteId)).build());
        item.put("LastPoints", AttributeValue.builder().ns(Arrays.stream(lastPoints).mapToObj(String::valueOf).collect(Collectors.toList())).build());
        item.put("LastRebounds", AttributeValue.builder().ns(Arrays.stream(lastRebounds).mapToObj(String::valueOf).collect(Collectors.toList())).build());
        item.put("LastAssists", AttributeValue.builder().ns(Arrays.stream(lastAssists).mapToObj(String::valueOf).collect(Collectors.toList())).build());
        item.put("LastSteals", AttributeValue.builder().ns(Arrays.stream(lastSteals).mapToObj(String::valueOf).collect(Collectors.toList())).build());
        item.put("LastFTP", AttributeValue.builder().ns(Arrays.stream(lastFTP).mapToObj(String::valueOf).collect(Collectors.toList())).build());
        item.put("LastTPP", AttributeValue.builder().ns(Arrays.stream(lastTPP).mapToObj(String::valueOf).collect(Collectors.toList())).build());

        ddb.putItem(PutItemRequest.builder()
                    .tableName("AthleteLastFive")
                    .item(item)
                    .build());
    }

    // Get athlete profile by ID
    public Map<String, AttributeValue> getAthleteProfile(int athleteId) {
    GetItemRequest request = GetItemRequest.builder()
        .tableName("AthleteProfile")
        .key(Map.of("AthleteID", AttributeValue.builder().n(Integer.toString(athleteId)).build()))
        .build();
    GetItemResponse response = ddb.getItem(request);
    return response.item();
    }

    // get athlete average stats by ID
    public Map<String, AttributeValue> getAthleteAvg(int athleteId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName("AthleteAvg")
            .key(Map.of("AthleteID", AttributeValue.builder().n(Integer.toString(athleteId)).build()))
            .build();
        GetItemResponse response = ddb.getItem(request);
        return response.item();
    }

    // Get athlete last five stats by ID
    public Map<String, AttributeValue> getAthleteLastFive(int athleteId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName("AthleteLastFive")
            .key(Map.of("AthleteID", AttributeValue.builder().n(Integer.toString(athleteId)).build()))
            .build();
        GetItemResponse response = ddb.getItem(request);
        return response.item();
    }
}