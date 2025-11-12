from nba_api.stats.static import teams
from nba_api.stats.endpoints import commonteamroster, playercareerstats, playergamelog
import boto3
import time
import math
from datetime import datetime

# Configure DynamoDB
dynamodb = boto3.resource('dynamodb', region_name='us-east-1')

# Safe conversion helper
def safe_int(val):
    """Safely convert a value to int. Returns 0 if the value is None or NaN."""
    try:
        if val is None or (isinstance(val, float) and math.isnan(val)):
            return 0
        return int(round(val))
    except:
        return 0

def safe_float(val, decimals=1):
    """Safely convert a value to float with specified decimal places."""
    try:
        if val is None or (isinstance(val, float) and math.isnan(val)):
            return 0.0
        return round(float(val), decimals)
    except Exception:
        return 0.0
    
# DynamoDB tables
TABLE_PROFILE = dynamodb.Table('AthleteProfile')
TABLE_AVG = dynamodb.Table('AthleteAvg')
TABLE_LAST5 = dynamodb.Table('AthleteLastFive')

# Determine current NBA season dynamically
current_year = datetime.now().year
current_month = datetime.now().month

# NBA season starts in October, so if we're before October, use previous year
if current_month < 10:
    season_start = current_year - 1
else:
    season_start = current_year

season_string = f"{season_start}-{str(season_start + 1)[-2:]}"
print(f"Using season: {season_string}")

# Get Clippers team ID and fetch roster
clippers = [team for team in teams.get_teams() if team['abbreviation'] == 'LAC'][0]
roster = commonteamroster.CommonTeamRoster(team_id=clippers['id'], season=season_string)
clippers_players = roster.get_data_frames()[0].to_dict('records')
print(f"Total Clippers players to populate: {len(clippers_players)}")

for player in clippers_players:
    player_id = player['PLAYER_ID']
    full_name = player['PLAYER']
    print(f"\nProcessing: {full_name} (ID: {player_id})")

    # Athlete Profile
    profile_item = {
        'AthleteID': player_id,
        'athleteName': full_name,
        'athleteTeam': 'LAC',
        'athletePosition': player.get('POSITION', None),
        'athleteJerseyNum': player.get('NUM', None),
        'athleteHeight': player.get('HEIGHT', None),
        'athleteWeight': player.get('WEIGHT', None)
    }
    try:
        TABLE_PROFILE.put_item(Item=profile_item)
        print(f"  ✓ Profile saved")
    except Exception as e:
        print(f"  ✗ Error inserting profile: {e}")

    # Athlete Average (career stats)
    try:
        career = playercareerstats.PlayerCareerStats(player_id=player_id)
        df = career.get_data_frames()[0]
        
        # Filter out aggregate rows (Career total) - only keep individual season rows
        df = df[df['TEAM_ID'] != 0]
        
        # Aggregate stats by season to handle multiple team entries
        season_stats = {}
        for _, row in df.iterrows():
            season = row['SEASON_ID']
            if season not in season_stats:
                season_stats[season] = {
                    'PTS': 0, 'REB': 0, 'AST': 0, 'STL': 0,
                    'FT_PCT': [], 'FG3_PCT': [], 'GP': 0
                }
            
            season_stats[season]['PTS'] += safe_int(row['PTS'])
            season_stats[season]['REB'] += safe_int(row['REB'])
            season_stats[season]['AST'] += safe_int(row['AST'])
            season_stats[season]['STL'] += safe_int(row['STL'])
            season_stats[season]['GP'] += safe_int(row['GP'])
            
            if row['FT_PCT'] is not None:
                season_stats[season]['FT_PCT'].append(row['FT_PCT'])
            if row['FG3_PCT'] is not None:
                season_stats[season]['FG3_PCT'].append(row['FG3_PCT'])
        
        # Write aggregated stats
        seasons_written = 0
        for season, stats in season_stats.items():
            avg_item = {
                'AthleteID': player_id,
                'season': season,
                'athletePPG': safe_float(stats['PTS'] / stats['GP']) if stats['GP'] > 0 else 0.0,
                'athleteRPG': safe_float(stats['REB'] / stats['GP']) if stats['GP'] > 0 else 0.0,
                'athleteAPG': safe_float(stats['AST'] / stats['GP']) if stats['GP'] > 0 else 0.0,
                'athleteSPG': safe_float(stats['STL'] / stats['GP']) if stats['GP'] > 0 else 0.0,
                'athleteAvgFTP': safe_float(sum(stats['FT_PCT']) / len(stats['FT_PCT']) * 100) if stats['FT_PCT'] else 0.0,
                'athleteAvgTPP': safe_float(sum(stats['FG3_PCT']) / len(stats['FG3_PCT']) * 100) if stats['FG3_PCT'] else 0.0
            }
            TABLE_AVG.put_item(Item=avg_item)
            seasons_written += 1
        print(f"  ✓ Saved {seasons_written} season(s) of stats")
    except Exception as e:
        print(f"  ✗ Error fetching season stats: {e}")

    # Athlete Last Five Games (current season)
    last_points = [0] * 5
    last_rebounds = [0] * 5
    last_assists = [0] * 5
    last_steals = [0] * 5
    last_ftp = [0] * 5
    last_tpp = [0] * 5

    try:
        gamelog = playergamelog.PlayerGameLog(
            player_id=player_id,
            season=season_string,  # Use dynamic season
            season_type_all_star='Regular Season'
        )
        df_games = gamelog.get_data_frames()[0].head(5)

        games_found = len(df_games)
        for i, (_, row) in enumerate(df_games.iterrows()):
            last_points[i] = safe_int(row['PTS'])
            last_rebounds[i] = safe_int(row['REB'])
            last_assists[i] = safe_int(row['AST'])
            last_steals[i] = safe_int(row['STL'])
            last_ftp[i] = safe_int(row['FT_PCT'] * 100) if row['FT_PCT'] is not None else 0
            last_tpp[i] = safe_int(row['FG3_PCT'] * 100) if row['FG3_PCT'] is not None else 0
        
        print(f"  ✓ Found {games_found} recent game(s)")
    except Exception as e:
        print(f"  ⚠ No recent games found in {season_string}: {e}")

    last5_item = {
        'AthleteID': player_id,
        'lastPoints': last_points,
        'lastRebounds': last_rebounds,
        'lastAssists': last_assists,
        'lastSteals': last_steals,
        'lastFTP': last_ftp,
        'lastTPP': last_tpp
    }
    try:
        TABLE_LAST5.put_item(Item=last5_item)
        print(f"  ✓ Last 5 games saved")
    except Exception as e:
        print(f"  ✗ Error inserting last 5 games: {e}")

    time.sleep(0.6)  # Increased delay to avoid rate limits

print("\n" + "="*50)
print("All Clippers player tables populated successfully.")
print("="*50)