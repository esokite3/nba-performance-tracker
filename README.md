# Clippers Stats Tracker
A full-stack web application for tracking Clippers player statistics. Search for players and view their profile information, career averages, and recent game performance.<br>
Deployed at: https://clips-stats-tracker.vercel.app

<p align="center">
  <img src="athlete-tracker/frontend/public/clippers-logo.png" width="150" alt="Clippers Logo">
</p>

## Features
- Real-time player statistics lookup
- Season averages (Points, Rebounds, Assists, Steals)
- Last 5 games performance tracking
- Player profile information (height, weight, position, jersey number)

## Tech Stack

### Frontend
- **React**: UI Framework
- **CSS**: Styling
- **Vercel**: Deployment platform

### Backend
- **Spring Boot**: (Java 21) REST API framework
- **AWS DynamoDB**: NoSQL database for player statistics
- **Maven**: Dependency management

## Database Schema (DynamoDB)

### AthleteProfile Table
- `AthleteID` (Number): Primary Key
- `athleteName` (String)
- `athleteTeam` (String)
- `athletePosition` (String)
- `athleteJerseyNum` (Number)
- `athleteHeight` (String)
- `athleteWeight` (Number)

### AthleteAvg Table
- `AthleteID` (Number): Primary Key
- `athletePPG` (Number)
- `athleteRPG` (Number)
- `athleteAPG` (Number)
- `athleteSPG` (Number)

### AthleteLastFive Table
- `AthleteID` (Number): Primary Key
- `lastPoints` (List of Numbers)
- `lastRebounds` (List of Numbers)
- `lastAssists` (List of Numbers)
- `lastSteals` (List of Numbers)
- `lastFTP` (List of Numbers)
- `lastTPP` (List of Numbers)

## Future Enhancements
- [ ] Add support for all NBA teams
- [ ] Player comparison feature
- [ ] Data visualization with charts
- [ ] Predictive modeling (e.g., predicting retirement age)

## Author

Caitlyn Lee - https://www.github.com/esokite3


