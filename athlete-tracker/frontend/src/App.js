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

  const HEROKU_URL ="https://clips-stats-tracker-b9a0ec87d561.herokuapp.com"

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
      <img className="logo-img" src={process.env.PUBLIC_URL + '/clippers-logo.png'} alt='Clippers Logo' />
      <img className="typography" src={process.env.PUBLIC_URL + '/clippers-typo.png'} alt='Clippers Typography' />
      <h1 className="stats-title">STATS TRACKER</h1>

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
          <p><span>Team: {playerStats.profile.Team}</span></p>
          <p><span>Position: {playerStats.profile.Position}</span></p>
          <p><span>Jersey: {playerStats.profile.JerseyNumber > 0 ? playerStats.profile.JerseyNumber : "N/A"}</span></p>
          <p><span>Height: {formatHeight(playerStats.profile.Height)}</span></p>
          <p><span>Weight: {playerStats.profile.Weight > 0 ? `${playerStats.profile.Weight} lbs` : "N/A"}</span></p>

          <h3>Season Averages</h3>
          <p><span>PTS: {playerStats.avg.PPG}</span></p>
          <p><span>RBS: {playerStats.avg.RPG}</span></p>
          <p><span>AST: {playerStats.avg.APG}</span></p>
          <p><span>STL: {playerStats.avg.SPG}</span></p>

          <h3>Last 5 Games (2025–2026 Season)</h3>
          {hasGameData(playerStats.lastFive.LastPoints) ? (
            <>
              <p><span>PTS: {playerStats.lastFive.LastPoints.join(", ")}</span></p>
              <p><span>RBS: {playerStats.lastFive.LastRebounds.join(", ")}</span></p>
              <p><span>AST: {playerStats.lastFive.LastAssists.join(", ")}</span></p>
              <p><span>STL: {playerStats.lastFive.LastSteals.join(", ")}</span></p>
              <p><span>FT%: {playerStats.lastFive.LastFTP.join(", ")}</span></p>
              <p><span>3P%: {playerStats.lastFive.LastTPP.join(", ")}</span></p>
            </>
          ) : (
            <p className="no-data" style={{fontStyle: "italic", color: "#666"}}>
              No games played yet this season
            </p>
          )}
        </div>
      )}
    </div>
  );
}

export default App;