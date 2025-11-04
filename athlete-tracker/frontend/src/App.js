import React, { useState, useRef } from "react";
import "./App.css";

function App() {
  const [playerName, setPlayerName] = useState("");
  const [playerStats, setPlayerStats] = useState(null);
  const [isClicked, setIsClicked] = useState(false);
  const buttonRef = useRef(null);

  const formatName = (name) => {
    return name
      .trim()
      .split(" ")
      .map(w => w.charAt(0).toUpperCase() + w.slice(1).toLowerCase())
      .join(" ");
  }

  const formatHeight = (inches) => {
    if (!inches || inches === 0) return "N/A";
    const feet = Math.floor(inches / 12);
    const remainingInches = inches % 12;
    return `${feet}'${remainingInches}"`;
  }

  const HEROKU_URL ="https://clips-stats-tracker.herokuapp.com"

  const handleSearch = async () => {
    if (!playerName.trim()) {
      alert("Please enter a player name.");
      return;
    }

    const response = await fetch(
      `${HEROKU_URL}/api/player?name=${formatName(playerName)}`
    );

    if (!response.ok) {
      alert("Player not found.");
      setPlayerStats(null);
      return;
    }

    const json = await response.json();
    console.log("Received data:", json);
    setPlayerStats(json);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    // Add visual feedback using state
    setIsClicked(true);
    setTimeout(() => {
      setIsClicked(false);
    }, 150);
    
    handleSearch();
  };

  // Helper to check if all values in array are 0
  const hasGameData = (arr) => {
    return arr && arr.some(val => val > 0);
  };

  return (
    <div className="App">
      <img src={process.env.PUBLIC_URL + '/clippers-logo.png'} alt='Clippers Logo' />
      <h1>Stats Tracker</h1>

      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={playerName}
          placeholder="Enter Clipper player name"
          onChange={(e) => setPlayerName(e.target.value)}
        />
        <button 
          ref={buttonRef} 
          type="submit"
          className={isClicked ? 'active-click' : ''}
        >
          Search
        </button>
      </form>

      {playerStats && (
        <div className="player-info">
          <h2>{playerStats.profile.Name}</h2>

          <h3>Player Profile</h3>
          <p>Team: {playerStats.profile.Team}</p>
          <p>Position: {playerStats.profile.Position}</p>
          <p>Jersey: {playerStats.profile.JerseyNumber > 0 ? playerStats.profile.JerseyNumber : "N/A"}</p>
          <p>Height: {formatHeight(playerStats.profile.Height)}</p>
          <p>Weight: {playerStats.profile.Weight > 0 ? `${playerStats.profile.Weight} lbs` : "N/A"}</p>

          <h3>Career Averages</h3>
          <p>PTS: {playerStats.avg.PPG}</p>
          <p>RBS: {playerStats.avg.RPG}</p>
          <p>AST: {playerStats.avg.APG}</p>
          <p>STL: {playerStats.avg.SPG}</p>

          <h3>Last 5 Games (2025–2026 Season)</h3>
          {hasGameData(playerStats.lastFive.LastPoints) ? (
            <>
              <p>PTS: {playerStats.lastFive.LastPoints.join(", ")}</p>
              <p>RBS: {playerStats.lastFive.LastRebounds.join(", ")}</p>
              <p>AST: {playerStats.lastFive.LastAssists.join(", ")}</p>
              <p>STL: {playerStats.lastFive.LastSteals.join(", ")}</p>
              <p>FT%: {playerStats.lastFive.LastFTP.join(", ")}</p>
              <p>3P%: {playerStats.lastFive.LastTPP.join(", ")}</p>
            </>
          ) : (
            <p style={{fontStyle: "italic", color: "#666"}}>
              No games played yet this season
            </p>
          )}
        </div>
      )}
    </div>
  );
}

export default App;