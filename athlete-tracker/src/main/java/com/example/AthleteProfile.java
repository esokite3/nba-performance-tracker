package com.example;

public class AthleteProfile {
    private int athleteId;

    // initialize athlete profile variables
    private String athleteName;
    private String athleteTeam;
    private String athletePosition;
    private int athleteJerseyNum;
    private int athleteHeight;
    private int athleteWeight;

    // constructor
    public AthleteProfile(int athleteId, String athleteName, String athleteTeam, String athletePosition,
                        int athleteJerseyNum, int athleteHeight, int athleteWeight) {
        
        this.athleteId = athleteId;
        this.athleteName = athleteName;
        this.athleteTeam = athleteTeam;
        this.athletePosition = athletePosition;
        this.athleteJerseyNum = athleteJerseyNum;
        this.athleteHeight = athleteHeight;
        this.athleteWeight = athleteWeight;
    }
    
    // athleteId get/set
    public int getAthleteId() { return athleteId; }
    public void setAthleteId(int athleteId) { this.athleteId = athleteId; }

    // athleteName get/set
    public String getAthleteName() { return athleteName; }
    public void setAthleteName(String athleteName) { this.athleteName = athleteName; }

    // athleteTeam get/set
    public String getAthleteTeam() { return athleteTeam; }
    public void setAthleteTeam(String athleteTeam) { this.athleteTeam = athleteTeam; }

    // athletePosition get/set
    public String getAthletePosition() { return athletePosition; }
    public void setAthletePosition(String athletePosition) { this.athletePosition = athletePosition; }

    // athleteJerseyNum get/set
    public int getAthleteJerseyNum() { return athleteJerseyNum; }
    public void setAthleteJerseyNum(int athleteJerseyNum) { this.athleteJerseyNum = athleteJerseyNum; }

    // athleteHeight get/set
    public int getAthleteHeight() { return athleteHeight; }
    public void setAthleteHeight(int athleteHeight) { this.athleteHeight = athleteHeight; }

    // athleteWeight get/set
    public int getAthleteWeight() { return athleteWeight; }
    public void setAthleteWeight(int athleteWeight) { this.athleteWeight = athleteWeight; }
}
