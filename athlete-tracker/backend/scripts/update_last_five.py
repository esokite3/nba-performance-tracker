"""
Daily updater for Clippers Last 5 Games
Run this script with Heroku Scheduler daily
"""
from nba_api.stats.static import teams
from nba_api.stats.endpoints import commonteamroster, playergamelog
import boto3
import time
import math
import os
from datetime import datetime

# Configure DynamoDB with environment variables
dynamodb = boto3.resource(
    'dynamodb',
    region_name=os.environ.get('AWS_REGION', 'us-east-1'),
    aws_access_key_id=os.environ.get('AWS_ACCESS_KEY_ID'),
    aws_secret_access_key=os.environ.get('AWS_SECRET_ACCESS_KEY')
)

TABLE_LAST5 = dynamodb.Table('AthleteLastFive')

def safe_int(val):
    """Safely convert a value to int. Returns 0 if the value is None or NaN."""
    try:
        if val is None or (isinstance(val, float) and math.isnan(val)):
            return 0
        return int(round(val))
    except:
        return 0

def update_last_five_games():
    """Update last 5 games for all Clippers players."""
    print(f"=== Starting daily update at {datetime.now()} ===")
    
    # Determine current NBA season
    current_year = datetime.now().year
    current_month = datetime.now().month
    
    if current_month < 10:
        season_start = current_year - 1
    else:
        season_start = current_year
    
    season_string = f"{season_start}-{str(season_start + 1)[-2:]}"
    print(f"Season: {season_string}")
    
    # Get Clippers roster
    try:
        clippers = [team for team in teams.get_teams() if team['abbreviation'] == 'LAC'][0]
        roster = commonteamroster.CommonTeamRoster(team_id=clippers['id'], season=season_string)
        clippers_players = roster.get_data_frames()[0].to_dict('records')
        print(f"Found {len(clippers_players)} Clippers players\n")
    except Exception as e:
        print(f"Error fetching roster: {e}")
        return
    
    updated_count = 0
    failed_count = 0
    
    for player in clippers_players:
        player_id = player['PLAYER_ID']
        full_name = player['PLAYER']
        
        try:
            # Initialize arrays for last 5 games
            last_points = [0] * 5
            last_rebounds = [0] * 5
            last_assists = [0] * 5
            last_steals = [0] * 5
            last_ftp = [0] * 5
            last_tpp = [0] * 5
            
            # Fetch game log
            gamelog = playergamelog.PlayerGameLog(
                player_id=player_id,
                season=season_string,
                season_type_all_star='Regular Season'
            )
            df_games = gamelog.get_data_frames()[0].head(5)
            
            games_found = len(df_games)
            
            # Populate stats from games
            for i, (_, row) in enumerate(df_games.iterrows()):
                last_points[i] = safe_int(row['PTS'])
                last_rebounds[i] = safe_int(row['REB'])
                last_assists[i] = safe_int(row['AST'])
                last_steals[i] = safe_int(row['STL'])
                last_ftp[i] = safe_int(row['FT_PCT'] * 100) if row['FT_PCT'] is not None else 0
                last_tpp[i] = safe_int(row['FG3_PCT'] * 100) if row['FG3_PCT'] is not None else 0
            
            # Update DynamoDB
            last5_item = {
                'AthleteID': player_id,
                'lastPoints': last_points,
                'lastRebounds': last_rebounds,
                'lastAssists': last_assists,
                'lastSteals': last_steals,
                'lastFTP': last_ftp,
                'lastTPP': last_tpp,
                'lastUpdated': datetime.now().isoformat()
            }
            
            TABLE_LAST5.put_item(Item=last5_item)
            print(f"✓ {full_name}: {games_found} games updated")
            updated_count += 1
            
            # Rate limit protection
            time.sleep(0.6)
            
        except Exception as e:
            print(f"✗ {full_name}: Failed - {e}")
            failed_count += 1
    
    print(f"\n=== Update Complete ===")
    print(f"Successfully updated: {updated_count}")
    print(f"Failed: {failed_count}")
    print(f"Timestamp: {datetime.now()}")
    print("=" * 50)

if __name__ == "__main__":
    update_last_five_games()