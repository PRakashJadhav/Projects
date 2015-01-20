using UnityEngine;
using System.Collections;

/*	ABSTRACT CLASS GameManager
 * 	@Zanice
 * 
 * 	This class holds the frame for different game types. Children
 * 	of this class can determine how teams and spawns are set,
 * 	how long the game lasts, and what kind of round-ending
 * 	parameters there are among other implementation abilities.
 */

public abstract class GameManager : MonoBehaviour {
	//NetworkManager pointer variable.
	public NetworkManager network;

	//Time variables.
	long SECOND = 10000000;
	public long timer;

	//Game ID variable.
    public int gametypeID;

	//Player and spawn array variables.
	GameObject[] players;
	GameObject[] spawns;

	//Transition timer variables.
	public int prepTime = 5;
	public int gameTime = 5;
	public int endTime = 5;

	//Game state variables.
	public enum State {Setup, Preparation, InGame, End};
	public State state;

    public virtual void Start() {
		//Initialize the variables.
		network = GameObject.Find("_SCRIPTS").GetComponent<NetworkManager>();

		timer = 0;

		players = GameObject.FindGameObjectsWithTag("Player");
		spawns = GameObject.FindGameObjectsWithTag("Respawn");

		state = State.Setup;
    }

	//Get method for the player array.
	public GameObject[] getPlayers() {
		return players;
	}

	//Get method for the spawn array.
	public GameObject[] getSpawns() {
		return spawns;
	}

	//Determines the next state for the game.
	public State nextState() {
		if ((state == State.Setup)||(state == State.End))
			return State.Preparation;
		if (state == State.Preparation)
			return State.InGame;
		return State.End;
	}

	//Changes the state of the game to the new state.
	public void switchState(State newstate) {
		//Change the state.
		state = newstate;

		//Set up and perform actions based on the new state.
		int mode = 0;
		switch(state) {
		case State.Setup:
			timer = 0;
			break;
		case State.Preparation:
			//Set the preparation time, run the implemented preparation routine.
			timer = System.DateTime.Now.Ticks + (SECOND * prepTime);
			preparationRoutine();

			//Deactivate all players, spawn the players, and signal the new round.
			network.GetComponent<PhotonView>().RPC("setActivePlayers", PhotonTargets.AllBuffered, false);
			network.GetComponent<PhotonView>().RPC("prepareMassSpawn", PhotonTargets.AllBuffered);
			network.GetComponent<PhotonView>().RPC("massSpawn", PhotonTargets.AllBuffered);
			network.GetComponent<PhotonView>().RPC("newRound", PhotonTargets.AllBuffered);

			//Set mode to the Preparation state's ID.
			mode = 1;
			break;
		case State.InGame:
			//Set the game timer and activate all players.
			timer = System.DateTime.Now.Ticks + (SECOND * gameTime);
			network.GetComponent<PhotonView>().RPC("setActivePlayers", PhotonTargets.AllBuffered, true);

			//Set mode to the InGame state's ID.
			mode = 2;
			break;
		case State.End:
			//Set the end timer.
			timer = System.DateTime.Now.Ticks + (SECOND * endTime);

			//Set mode to the End state's ID.
			mode = 3;
			break;
		}

		//Relay the new state to the other clients.
		network.GetComponent<PhotonView>().RPC("syncGameState", PhotonTargets.AllBuffered, mode);
	}

	//Assign the player to a team by first updating the present players.
	public int findTeam() {
		players = GameObject.FindGameObjectsWithTag("Player");
		return assignTeam();
	}

	//Runs an update depending on if the client is the master client.
	public void updateGame(bool master) {
		//Regather the players in the game.
		players = GameObject.FindGameObjectsWithTag("Player");

		//If this is the master client, update the current state depending on timers and parameters.
		if (master) {
			if ((state != State.Setup)&&(reversionParametersMet())) {
				switchState(State.Setup);
				return;
			}
			if (state == State.Setup) {
				if (gameStartParametersMet())
					switchState(nextState());
			}
			else if (System.DateTime.Now.Ticks >= timer)
				switchState(nextState());
		}
	}

	//Handles a game event and starts a check on if the round should end.
	public void onGameEvent() {
		if (state == State.InGame) {
			if (roundEndParametersMet())
				switchState(State.End);
		}
	}

	//Abstract method, dictates how a spawn is chosen for the player based on the team number.
    public abstract Transform findSpawnPoint(int team);

	//Abstract method, dictates how a team is assigned to the player.
	public abstract int assignTeam();

	//Abstract method, handles the event of the given player's death.
	public abstract void onPlayerDeath(GameObject player);

	//Abstract method, dictates under what conditions the game starts.
	public abstract bool gameStartParametersMet();

	//Abstract method, dictates under what conditions the game must go back to setup.
	public abstract bool reversionParametersMet();

	//Abstract method, dictates under what conditions the game ends.
	public abstract bool roundEndParametersMet();

	//Abstract method, runs instrustions at the beginning of the Preparation state.
	public abstract void preparationRoutine();
}