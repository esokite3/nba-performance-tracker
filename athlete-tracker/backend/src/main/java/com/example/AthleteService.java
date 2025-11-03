package com.example;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.util.*;
import org.springframework.stereotype.Service;
import java.text.Normalizer;

@Service
public class AthleteService {
    private final DynamoDbClient ddb;

    public AthleteService(DynamoDbClient client) {
        this.ddb = client;
    }

    // Get athlete profile by Id
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

    // Get athlete profile by name
    public Map<String, AttributeValue> getAthleteProfileByName(String name) {
        if (name == null || name.isEmpty()) return null;

        String normalizedInput = Normalizer.normalize(name, Normalizer.Form.NFD)
                                        .replaceAll("\\p{M}", "")
                                        .toLowerCase();

        ScanRequest scan = ScanRequest.builder()
                                    .tableName("AthleteProfile")
                                    .build();
        ScanResponse response = ddb.scan(scan);

        for (Map<String, AttributeValue> item : response.items()) {
            AttributeValue nameAttr = item.get("athleteName");
            if (nameAttr != null && nameAttr.s() != null) {
                String normalizedItem = Normalizer.normalize(nameAttr.s(), Normalizer.Form.NFD)
                                                .replaceAll("\\p{M}", "")
                                                .toLowerCase();
                if (normalizedItem.equals(normalizedInput)) {
                    return item;
                }
            }
        }
        return null;
    }
}