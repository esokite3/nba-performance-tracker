package com.example;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DynamoDbClient client = DynamoDBClientProvider.getClient();
        AthleteService service = new AthleteService(client);
        AthleteTracker tracker = new AthleteTracker(service);
    
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter player ID: ");
        int playerId = scanner.nextInt();
        scanner.close();

        tracker.trackPlayer(playerId);
        System.out.println("Tracking complete");
    }
}
