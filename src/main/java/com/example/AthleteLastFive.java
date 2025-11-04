package com.example;

public class AthleteLastFive {
    private int athleteId;

    // initialize athlete last five game variables
    private int[] lastPoints = new int[5];
    private int[] lastRebounds = new int[5];
    private int[] lastAssists = new int[5];
    private int[] lastSteals = new int[5];
    private int[] lastFTP = new int[5]; // FTP: free throw percentage
    private int[] lastTPP = new int[5]; // TPP: three point percentage

    // constructor
    public AthleteLastFive(int athleteId) {
        this.athleteId = athleteId;
    }

    // athleteId get/set
    public int getAthleteId() { return athleteId; }
    public void setAthleteId(int athleteId) { this.athleteId = athleteId; }
    
    // lastPoints get/set
    public int[] getLastPoints() { return lastPoints; }
    public void setLastPoints(int[] lastPoints) { this.lastPoints = lastPoints; }

    // lastRebounds get/set
    public int[] getLastRebounds() { return lastRebounds; }
    public void setLastRebounds(int[] lastRebounds) { this.lastRebounds = lastRebounds; }

    // lastAssists get/set
    public int[] getLastAssists() { return lastAssists; }
    public void setLastAssists(int[] lastAssists) { this.lastAssists = lastAssists; }

    // lastSteals get/set
    public int[] getLastSteals() { return lastSteals; }
    public void setLastSteals(int[] lastSteals) { this.lastSteals = lastSteals; }

    // lastFTP get/set
    public int[] getLastFTP() { return lastFTP; }
    public void setLastFTP(int[] lastFTP) { this.lastFTP = lastFTP; }

    // lastTPP get/set
    public int[] getLastTPP() { return lastTPP; }
    public void setLastTPP(int[] lastTPP) { this.lastTPP = lastTPP; }
}