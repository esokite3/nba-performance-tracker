package com.example;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import java.util.Scanner;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Main {
    public static void main(String[] args) {
        DynamoDbClient client = DynamoDBClientProvider.getClient();

        AthleteService service = new AthleteService(client);
        AthleteTracker tracker = new AthleteTracker(service);
    
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter player name: ");
        String athleteName = scanner.nextLine();
        scanner.close();
        
        Map<String, AttributeValue> profile = service.getAthleteProfileByName(athleteName);
        
        if (profile == null) {
            System.out.println("No athlete found with name: " + athleteName);
            return;
        }

        int athleteId = Integer.parseInt(profile.get("AthleteID").n());
        tracker.trackPlayer(athleteId);
    }
}
