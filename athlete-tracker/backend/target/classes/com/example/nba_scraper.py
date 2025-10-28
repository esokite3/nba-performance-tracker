from nba_api.stats.endpoints import commonplayerinfo, playercareerstats, playergamelog
from datetime import datetime
import boto3

dynamodb = boto3.resource (
    "dynamodb", region_name="us-east-1", endpoint_url="http://localhost:8000"
)

profile_table = dynamodb.Table("AthleteProfile")
avg_table = dynamodb.Table("AthleteAvg")
lastfive_table = dynamodb.Table("AthleteLastFive")

def get_current_season():
    now = datetime.now()
    year, month = now.year, now.month

    # season starts in Oct
    if month >= 10:
        return f"{year}-{str(year+1)[-2:]}"
    else:
        return f"{year-1}-{str(year)[-2:]}"

# athlete profile
def get_player_profile(player_id: int):
    info = commonplayerinfo.CommonPlayerInfo(player_id=player_id)
    infodf = info.get_data_frames()[0]

    # combine first and last names
    infodf["NAME"] = infodf["FIRST_NAME"] + " " + infodf["LAST_NAME"]

    # build player profile
    return {
        "ID": player_id,
        "Name": infodf["NAME"].values[0],
        "Team": infodf["TEAM_NAME"].values[0],
        "Position": infodf["POSITION"].values[0],
        "Jersey": infodf["JERSEY"].values[0],
        "Height": infodf["HEIGHT"].values[0],
        "Weight": infodf["WEIGHT"].values[0]
    }


# athlete average stats
def get_player_avg(player_id: int):
    career = playercareerstats.PlayerCareerStats(player_id=player_id)
    careerdf = career.get_data_frames()[0]
    latest_season = careerdf.iloc[-1]

    # build player avg
    return {
        "ID": player_id,
        "PPG": float(latest_season["PTS"]),
        "RPG": float(latest_season["REB"]),
        "APG": float(latest_season["AST"]),
        "SPG": float(latest_season["STL"]),
        "FTP": float(latest_season["FT_PCT"]),
        "TPP": float(latest_season["FG3_PCT"])
    }

# athlete last five
def get_last_five(player_id: int):
    season = get_current_season()
    gamelog = playergamelog.PlayerGameLog(player_id=player_id)
    gamesdf = gamelog.get_data_frames()[0]

    gamesdf = gamesdf.sort_values("GAME_DATE", ascending=False).head(5)

    return {
        "ID": player_id,
        "LastPoints": gamesdf["PTS"].astype(int).tolist(),
        "LastRebounds": gamesdf["REB"].astype(int).tolist(),
        "LastAssists": gamesdf["AST"].astype(int).tolist(),
        "LastSteals": gamesdf["STL"].astype(int).tolist(),
        "LastFTP": gamesdf["FT_PCT"].astype(int).tolist(),
        "LastTPP": gamesdf["FG3_PCT"].astype(int).tolist()
    }

def load_player_data(player_id):
    profile = get_player_profile(player_id)
    avg_stats = get_player_avg(player_id)
    last_five = get_last_five(player_id)

    profile_table.put_item(Item=profile)
    avg_table.put_item(Item=avg_stats)
    lastfive_table.put_item(Item=last_five)