package com.example;

public class AthleteAvg {
    private int athleteId;
    // initialize average stats variables

    private int athletePPG; // PPG: points per game
    private int athleteRPG; // RPG: rebounds per game
    private int athleteAPG; // APG: assists per game
    private int athleteSPG; // SPG: steals per game
    private int athleteAvgFTP; // FTP: free throw percentage
    private int athleteAvgTPP; // TPP: three-point percentage

    // constructor
    public AthleteAvg(int athleteId, int athletePPG, int athleteRPG, int athleteAPG, 
                    int athleteSPG, int athleteAvgFTP, int athleteAvgTPP) {

        this.athleteId = athleteId;
        this.athletePPG = athletePPG;
        this.athleteRPG = athleteRPG;
        this.athleteAPG = athleteAPG;
        this.athleteSPG = athleteSPG;
        this.athleteAvgFTP = athleteAvgFTP;
        this.athleteAvgTPP = athleteAvgTPP;
    }

    // athleteId get/ste
    public int getAthleteId() { return athleteId; }
    public void setAthleteId(int athleteId) { this.athleteId = athleteId; }

    // athletePPG get/set
    public int getAthletePPG() { return athletePPG; }
    public void setAthletePPG(int athletePPG) { this.athletePPG = athletePPG; }

    // athleteRPG get/set
    public int getAthleteRPG() { return athleteRPG;}
    public void setAthleteRPG(int athleteRPG) { this.athleteRPG = athleteRPG; }

    // athleteAPG get/set
    public int getAthleteAP() { return athleteAPG;}
    public void getAthleteAPG(int athleteAPG) { this.athleteAPG = athleteAPG; }

    // athleteSPG get/set
    public int getAthleteSPG() { return athleteSPG; }
    public void setAthleteSPG(int athleteSPG) { this.athleteSPG = athleteSPG; }

    // athleteAvgFTP get/set
    public int getAthleteAvgFTP() { return athleteAvgFTP; }
    public void setAthleteAvgFTP(int athleteAvgFTP) { this.athleteAvgFTP = athleteAvgFTP; }

    // athleteAvgTPP get/set
    public int getAthleteAvgTPP() { return athleteAvgTPP; }
    public void setAthleteAvgTPP(int athleteAvgTPP) { this.athleteAvgTPP = athleteAvgTPP; }
}
