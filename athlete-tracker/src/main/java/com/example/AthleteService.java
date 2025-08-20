package com.example;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.util.*;

public class AthleteService {
    private final DynamoDbClient ddb;

    public AthleteService(DynamoDbClient client) {
        this.ddb = client;
    }
}