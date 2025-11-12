import time
import math
from datetime import datetime
from nba_api.stats.static import teams
from nba_api.stats.endpoints import commonteamroster, playercareerstats, playergamelog
import boto3
import sys

# -----------------------------
# DynamoDB configuration
# -----------------------------
dynamodb = boto3.resource('dynamodb', region_name='us-east-1')
TABLE_PROFILE = dynamodb.Table('AthleteProfile')
TABLE_AVG = dynamodb.Table('AthleteAvg')
TABLE_LAST5 = dynamodb.Table('AthleteLastFive')

# -----------------------------
# Helper functions
# -----------------------------
def safe_int(val):
    """Safely convert a value to int (returns 0 for None/NaN)."""
    try:
        if val is None or (isinstance(val, float) and math.isnan(val)):
            return 0
        return int(round(val))
    except Exception:
        return 0


def get_current_season():
    """Return current NBA season string, e.g. '2024-25'."""
    current_year = datetime.now().year
    current_month = datetime.now().month
    season_start = current_year - 1 if current_month < 10 else current_year
    return f"{season_start}-{str(season_start + 1)[-2:]}"


def retry_api_call(func, max_retries=3, initial_delay=2):
    """Retry an API call with exponential backoff."""
    for attempt in range(max_retries):
        try:
            return func()
        except Exception as e:
            if attempt == max_retries - 1:
                raise
            delay = initial_delay * (2 ** attempt)
            print(f"  ⚠️  API call failed (attempt {attempt + 1}/{max_retries}), retrying in {delay}s...")
            time.sleep(delay)


# -----------------------------
# Update logic
# -----------------------------
def update_clippers_data():
    season_string = get_current_season()
    print(f"Updating Clippers data for {season_string}")

    try:
        clippers = [t for t in teams.get_teams() if t['abbreviation'] == 'LAC'][0]
    except IndexError:
        print("Could not find Clippers team in nba_api.")
        sys.exit(1)

    # Use retry logic for roster fetch
    def fetch_roster():
        return commonteamroster.CommonTeamRoster(
            team_id=clippers['id'], 
            season=season_string,
            timeout=90  # Increased timeout
        )
    
    roster = retry_api_call(fetch_roster)
    players = roster.get_data_frames()[0].to_dict('records')
    print(f"Found {len(players)} Clippers players.")

    for player in players:
        player_id = player['PLAYER_ID']
        name = player['PLAYER']
        print(f"{name} (ID: {player_id})")

        # --- Update averages ---
        try:
            def fetch_career():
                return playercareerstats.PlayerCareerStats(
                    player_id=player_id,
                    timeout=90
                )
            
            career = retry_api_call(fetch_career)
            df = career.get_data_frames()[0]
            df = df[df['TEAM_ID'] != 0]

            season_stats = {}
            for _, row in df.iterrows():
                s = row['SEASON_ID']
                if s not in season_stats:
                    season_stats[s] = {
                        'PTS': 0, 'REB': 0, 'AST': 0, 'STL': 0,
                        'FT_PCT': [], 'FG3_PCT': [], 'GP': 0
                    }

                season_stats[s]['PTS'] += safe_int(row['PTS'])
                season_stats[s]['REB'] += safe_int(row['REB'])
                season_stats[s]['AST'] += safe_int(row['AST'])
                season_stats[s]['STL'] += safe_int(row['STL'])
                season_stats[s]['GP'] += safe_int(row['GP'])
                if row['FT_PCT'] is not None:
                    season_stats[s]['FT_PCT'].append(row['FT_PCT'])
                if row['FG3_PCT'] is not None:
                    season_stats[s]['FG3_PCT'].append(row['FG3_PCT'])

            for s, stats in season_stats.items():
                TABLE_AVG.put_item(
                    Item={
                        'AthleteID': player_id,
                        'season': s,
                        'athletePPG': safe_int(stats['PTS'] / stats['GP']) if stats['GP'] else 0,
                        'athleteRPG': safe_int(stats['REB'] / stats['GP']) if stats['GP'] else 0,
                        'athleteAPG': safe_int(stats['AST'] / stats['GP']) if stats['GP'] else 0,
                        'athleteSPG': safe_int(stats['STL'] / stats['GP']) if stats['GP'] else 0,
                        'athleteAvgFTP': safe_int(sum(stats['FT_PCT']) / len(stats['FT_PCT']) * 100) if stats['FT_PCT'] else 0,
                        'athleteAvgTPP': safe_int(sum(stats['FG3_PCT']) / len(stats['FG3_PCT']) * 100) if stats['FG3_PCT'] else 0
                    }
                )
            print("  ✓ Updated averages")

        except Exception as e:
            print(f"  ✗ Error updating averages: {e}")

        # --- Update last 5 games ---
        try:
            def fetch_gamelog():
                return playergamelog.PlayerGameLog(
                    player_id=player_id, 
                    season=season_string, 
                    season_type_all_star="Regular Season",
                    timeout=90
                )
            
            gamelog = retry_api_call(fetch_gamelog)
            games = gamelog.get_data_frames()[0].head(5)

            stats = {
                'lastPoints': [safe_int(r['PTS']) for _, r in games.iterrows()],
                'lastRebounds': [safe_int(r['REB']) for _, r in games.iterrows()],
                'lastAssists': [safe_int(r['AST']) for _, r in games.iterrows()],
                'lastSteals': [safe_int(r['STL']) for _, r in games.iterrows()],
                'lastFTP': [safe_int(r['FT_PCT'] * 100) if r['FT_PCT'] is not None else 0 for _, r in games.iterrows()],
                'lastTPP': [safe_int(r['FG3_PCT'] * 100) if r['FG3_PCT'] is not None else 0 for _, r in games.iterrows()],
            }

            # pad to 5 elements
            for k in stats:
                while len(stats[k]) < 5:
                    stats[k].append(0)

            TABLE_LAST5.put_item(
                Item={
                    'AthleteID': player_id,
                    **stats
                }
            )
            print("  ✓ Updated last 5 games")

        except Exception as e:
            print(f"  ✗ Error updating last 5 games: {e}")

        time.sleep(1.0)  # Increased delay between players

    print("\n✅ Daily Clippers update complete!")


if __name__ == "__main__":
    update_clippers_data()