package com.example;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        DynamoDbClient client = DynamoDBClientProvider.getClient();

        AthleteService service = new AthleteService(client);

        // sample athlete
        service.createAthleteProfile(1, "Victor Wembanyama", "San Antonio Spurs", "Center", 1, 74, 200);
        
        Map<String, AttributeValue> item = service.getAthleteProfile(1);

        if (item != null && !item.isEmpty()) {
            System.out.println("Name: " + item.get("Name").s());
            System.out.println("Age: " + item.get("Age").n());
            System.out.println("Team: " + item.get("Team").s());
        } else {
            System.out.println("Player not found.");
        }
    }
}
