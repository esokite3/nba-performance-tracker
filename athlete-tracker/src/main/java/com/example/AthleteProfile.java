package com.example;

public class AthleteProfile {
    // initialize variables
    public String athleteId;
    public String athleteName;
    public String athleteTeam;
    public String athletePosition;
    public int athleteJerseyNum;
    public int athleteHeight;
    public int athleteWeight;

    // constructor
    public AthleteProfile(String athleteId, String athleteName, String athleteTeam, String athletePosition,
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
    public String getAthleteId(String athleteId) {
        return athleteId;
    }
    public void setAthleteId(String athleteId) {
        this.athleteId = athleteId;
    }

    // athleteName get/set
    public String getAthleteName(String athleteName) {
        return athleteName;
    }
    public void setAthleteName(String athleteName) {
        this.athleteName = athleteName;
    }

    // athleteTeam get/set
    public String getAthleteTeam(String athleteTeam) {
        return athleteTeam;
    }
    public void setAthleteTeam(String athleteTeam) {
        this.athleteTeam = athleteTeam;
    }

    // athletePosition get/set
    public String getAthletePosition(String athletePosition) {
        return athletePosition;
    }
    public void setAthletePosition(String athletePosition) {
        this.athletePosition = athletePosition;
    }

    // athleteJerseyNum get/set
    public int getAthleteJerseyNum(int athleteJerseyNum) {
        return athleteJerseyNum;
    }
    public void setAthleteJerseyNum(int athleteJerseyNum) {
        this.athleteJerseyNum = athleteJerseyNum;
    }

    // athleteHeight get/set
    public int getAthleteHeight(int athleteHeight) {
        return athleteHeight;
    }
    public void setAthleteHeight(int athleteHeight) {
        this.athleteHeight = athleteHeight;
    }

    // athleteWeight get/set
    public int getAthleteWeight(int athleteWeight) {
        return athleteWeight;
    }
    public void setAthleteWeight(int athleteWeight) {
        this.athleteWeight = athleteWeight;
    }
}
