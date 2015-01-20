using UnityEngine;
using System.Collections;

/*	CLASS GameManager1
 * 	@Zanice
 * 
 * 	This is the game mode for "classic" Cops and Robbers. The
 * 	game goes on until at least one team has no players alive,
 * 	but the game follows the standard rules of Cops and Robbers.
 */

public class GameManagerGame1 : GameManager {
	//Rotation variable, to determine how many rounds are played before sides are switched.
	public int roundRotation = 3;

	//Round number variable.
	int round;

	//Cop team variable, to keep track of which team is what side.
	int copTeam;

    public override void Start() {
		//Run GameManager's startup and set the ID of this game.
        base.Start();
        base.gametypeID = 1;

		//Initialize the variables.		
		round = 0;
		copTeam = 1;
    }

	//Implemented method, dictates how a team is assigned to the player.
	public override int assignTeam() {
		//Count the number of players that are currently on each team.
		int team1 = 0;
		int team2 = 0;
		PlayerStatus current;
		for (int i = 0; i < getPlayers().Length; i++) {
			current = getPlayers()[i].GetComponent<PlayerStatus>();
			if (current.team == 1)
				team1++;
			else if (current.team == 2)
				team2++;
		}

		//Based on the count, return the team assignment. If both teams are equal, assign to the cop team.
		if (team2 > team1)
			return 1;
		else if (team1 > team2)
			return 2;
		else
			return copTeam;
	}

	//Implemented method, dictates how a spawn is chosen for the player based on the team number.
	public override Transform findSpawnPoint(int team) {
		//If spawns exist for the player, find a spawn. Otherwise, return null.
		if (getSpawns().Length > 0) {
			//Create a new array of potential spawns and add valid spawns to the array.
			//A spawn is valid if the spawn's team ID matches the team passed into this method.
			GameObject[] potentials = new GameObject[getSpawns().Length];
			int size = 0;
			for (int i = 0; i < getSpawns().Length; i++) {
				if (getSpawns()[i].GetComponent<Spawn>().team == team) {
					potentials[size] = getSpawns()[i];
					size++;
				}
			}

			//If there are no valid spawns, consider all spawns.
			if (size == 0) {
				potentials = getSpawns();
				size = getSpawns().Length;
			}

			//Randomly select a spawn for the player to spawn at, and return the first success.
			//TODO: Write a better way to randomly choose a spawn (one that has no chance of failing).
			int iterations = 0;
			Transform spawn = null;
			while (iterations < 20) {
				spawn = potentials[Random.Range(0, size)].transform;
				if (!spawn.GetComponent<Spawn>().isOccupied())
					break;
				iterations++;
			}
			return spawn;
		}
		else return null;
	}

	//Implemented method, runs instrustions at the beginning of the Preparation state.
	public override void preparationRoutine() {
		//Increment the round number. If it is time for a rotation, rotate the teams.
		round++;
		if (((round != 1)&&(round % roundRotation == 1))||((round != 1)&&(roundRotation == 1))) {
			if (copTeam == 1)
				copTeam = 2;
			else
				copTeam = 1;
		}

		//Based on which team is the cop team, assign the players' classes.
		network.photonView.RPC("assignClasses", PhotonTargets.AllBuffered, copTeam);
	}

	//Implemented method, dictates under what conditions the game starts.
	public override bool gameStartParametersMet() {
		//Declare a boolean variable for each team, and a variable to hold the current player.
		bool team1 = false;
		bool team2 = false;
		PlayerStatus current;

		//Return true if there is at least one player on both teams.
		for (int i = 0; i < getPlayers().Length; i++) {
			current = getPlayers()[i].GetComponent<PlayerStatus>();
			if (current.team == 1)
				team1 = true;
			else if (current.team == 2)
				team2 = true;
		}
		return (team1)&&(team2);
	}

	//Implemented method, handles the event of the given player's death.
	public override void onPlayerDeath(GameObject player) {
		//A game event has occurred.
		onGameEvent();
	}

	//Implemented method, dictates under what conditions the game ends.
	public override bool roundEndParametersMet() {
		//Return true if at least one of the teams has no players alive.
		bool team1 = false;
		bool team2 = false;
		PlayerStatus current;
		for (int i = 0; i < getPlayers().Length; i++) {
			current = getPlayers()[i].GetComponent<PlayerStatus>();
			if ((current.team == 1)&&(current.playerClass != PlayerStatus.Class.Spectator))
				team1 = true;
			else if ((current.team == 2)&&(current.playerClass != PlayerStatus.Class.Spectator))
				team2 = true;
		}
		return (!team1)||(!team2);
	}

	//Implemented method, dictates under what conditions the game must go back to setup.
	public override bool reversionParametersMet() {
		//If the conditions for starting the game are not met, then revert the game to setup.
		return !gameStartParametersMet();
	}
}