using UnityEngine;
using System.Collections;

/*	CLASS NetworkManager
 * 	@Zanice
 * 
 * 	This class controls the main network interactions with
 * 	the lobby, the room, and the other players. The class
 * 	manages the creation and upkeep of the player object,
 * 	the equipment of the player, and RPC commands sent
 * 	out to other players.
 */

public class NetworkManager : Photon.MonoBehaviour {
	//Variable holding the value of a second, in terms of ticks.
	long SECOND = 10000000;
	
	//Player and team variables.
	public GameObject myPlayer;
	int team;

	//Equipment variables.
	public GameObject myMine;
	public GameObject myC4;

	//Network connection variables.
	bool connecting;
	bool connected;
	bool teamed;
	bool setup;
	long timer;
	
	void Start() {
		//Initialize the variables.
		team = 0;
		connected = false;
		teamed = false;
		setup = false;
		timer = 0;

		//Connect to the server.
		connect();
	}
	
	void Update() {
		//If the client is connected, the player is not teamed, the timer set is complete, and spawns exist for the player, begin creating the player.
		if ((connected)&&(!teamed)&&(System.DateTime.Now.Ticks > timer)&&(GameObject.Find("_SCRIPTS").GetComponent<GameManager>().getSpawns() != null)) {
			//Set the player's class and team, and relay the player's team to the other clients.
			myPlayer.GetComponent<PlayerStatus>().setClass(PlayerStatus.Class.Spectator);
			team = GameObject.Find("_SCRIPTS").GetComponent<GameManager>().findTeam();
			myPlayer.GetComponent<PhotonView>().RPC("setTeam", PhotonTargets.AllBuffered, team);

			//Spawn the player.
			Transform spawn = GameObject.Find("_SCRIPTS").GetComponent<GameManager>().findSpawnPoint(team);
			if (spawn != null) {
				myPlayer.transform.position = spawn.position;
				myPlayer.transform.rotation = spawn.rotation;
			}
			else {
				myPlayer.transform.position = Vector3.zero;
				myPlayer.transform.rotation = Quaternion.identity;
			}

			//Activate and configure the player.
			myPlayer.GetComponent<PlayerStatus>().activate();
			myPlayer.GetComponent<PlayerAction>().setSets(PlayerAction.Equipment.Armguard, PlayerAction.Equipment.Armor, PlayerAction.Equipment.Mine, PlayerAction.Equipment.C4, PlayerAction.Equipment.BBGun, PlayerAction.Equipment.PoisonBolt);

			//Update connection variables.
			teamed = true;
			setup = true;
		}

		//If the setup is complete, update the game for the client via GameManager.
		if (setup) {
			GameObject.Find("_SCRIPTS").GetComponent<GameManager>().updateGame(PhotonNetwork.isMasterClient);
		}
	}

	//Starts the conenction process.
	void connect() {
		PhotonNetwork.ConnectUsingSettings("0.0.1");
	}

	//Called during the connection process. Display connection information.
	void OnGUI() {
		GUILayout.Label(PhotonNetwork.connectionStateDetailed.ToString());
	}

	//Called during the connection process, when the client joins the lobby.
	void OnJoinedLobby() {
		//Join a room.
		PhotonNetwork.JoinRandomRoom();
	}

	//Called during the connection process, when an attempt to join a random room fails.
	void OnPhotonRandomJoinFailed() {
		//If a room does not exist to join, create one.
		PhotonNetwork.CreateRoom(null);
	}

	//Called during the connection process, when a room is joined.
	void OnJoinedRoom() {
		//Create the player object and disable the scene's main camera.
		GameObject.Find("StartCamera").SetActive(false);
		myPlayer = PhotonNetwork.Instantiate("Player", new Vector3(0, 1.05f, 0), Quaternion.identity, 0) as GameObject;
		myPlayer.transform.FindChild("PlayerCamera").GetComponent<Camera>().enabled = true;
		myPlayer.transform.FindChild("PlayerCamera").GetComponent<AudioListener>().enabled = true;
		myPlayer.GetComponent<PlayerAction>().enabled = true;

		//Set a timer to delay the final seetup (prevents null exceptions).
		timer = System.DateTime.Now.Ticks + (SECOND * 1);
		connected = true;
	}

	//Spawn a stand-alone mine in the game.
	public void spawnMine(Vector3 pos) {
		myMine = PhotonNetwork.Instantiate("Mine", pos, Quaternion.identity, 0) as GameObject;
		myMine.GetComponent<EqMine>().enabled = true;
	}

	//Spawn a stand-alone C4 in the game.
	public void spawnC4(Vector3 pos) {
		myC4 = PhotonNetwork.Instantiate("C4", pos, Quaternion.identity, 0) as GameObject;
		myC4.GetComponent<EqC4>().enabled = true;
	}

	//RPC call to change the game state.
	[RPC]
	public void syncGameState(int mode) {
		//Change the state depending on the passed variable, and update the client's timer accordingly.
		switch (mode) {
		case 0:
			GameObject.Find("_SCRIPTS").GetComponent<GameManager>().state = GameManager.State.Setup;
			timer = 0;
			break;
		case 1:
			GameObject.Find("_SCRIPTS").GetComponent<GameManager>().state = GameManager.State.Preparation;
			GameObject.Find("_SCRIPTS").GetComponent<GameManager>().timer = System.DateTime.Now.Ticks + (SECOND * GameObject.Find("_SCRIPTS").GetComponent<GameManager>().prepTime);
			break;
		case 2:
			GameObject.Find("_SCRIPTS").GetComponent<GameManager>().state = GameManager.State.InGame;
			GameObject.Find("_SCRIPTS").GetComponent<GameManager>().timer = System.DateTime.Now.Ticks + (SECOND * GameObject.Find("_SCRIPTS").GetComponent<GameManager>().gameTime);
			break;
		case 3:
			GameObject.Find("_SCRIPTS").GetComponent<GameManager>().state = GameManager.State.End;
			GameObject.Find("_SCRIPTS").GetComponent<GameManager>().timer = System.DateTime.Now.Ticks + (SECOND * GameObject.Find("_SCRIPTS").GetComponent<GameManager>().endTime);
			break;
		}
	}

	//RPC call to activate or deactivate the player.
	[RPC]
	public void setActivePlayers(bool b) {
		if (b)
			myPlayer.GetComponent<PlayerStatus>().activate();
		else
			myPlayer.GetComponent<PlayerStatus>().deactivate();
	}

	//RPC call to move players out of the way of spawns.
	[RPC]
	public void prepareMassSpawn() {
		myPlayer.transform.position = new Vector3(0, -100, 0);
	}

	//RPC call to spawn the player.
	[RPC]
	public void massSpawn() {
		//Find a spawn for the player. Position and align the character at the spawn.
		Transform spawn = GameObject.Find("_SCRIPTS").GetComponent<GameManager>().findSpawnPoint(team);
		if (spawn != null) {
			myPlayer.transform.position = spawn.position;
			myPlayer.transform.rotation = spawn.rotation;
			myPlayer.transform.FindChild("PlayerCamera").rotation = spawn.rotation;
			myPlayer.transform.FindChild("PlayerCamera").GetComponent<MouseLook>().rotationY = -spawn.rotation.x;
		}
		else {
			myPlayer.transform.position = Vector3.zero;
			myPlayer.transform.rotation = Quaternion.identity;
			myPlayer.transform.FindChild("PlayerCamera").rotation = Quaternion.identity;
			myPlayer.transform.FindChild("PlayerCamera").GetComponent<MouseLook>().rotationY = 0;
		}

		//For all clones of this player across the clients, reset the healths back to full.
		myPlayer.GetComponent<PhotonView>().RPC("resetHealth", PhotonTargets.AllBuffered);
	}

	//RPC call to assign the class of the player.
	[RPC]
	public void assignClasses(int i) {
		myPlayer.GetComponent<PhotonView>().RPC("assignClass", PhotonTargets.AllBuffered, i);
	}

	//RPC call to relay the player's death.
	[RPC]
	public void playerDeath() {
		GameObject.Find("_SCRIPTS").GetComponent<GameManager>().onPlayerDeath(null);
	}

	//RPC call to reset equipment and status variables on a new round.
	[RPC]
	public void newRound() {
		//Destroy any equipment that belongs to the player.
		if (myMine != null) {
			PhotonNetwork.Destroy(myMine.GetComponent<PhotonView>());
			myMine = null;
		}
		if (myC4 != null) {
			PhotonNetwork.Destroy(myC4.GetComponent<PhotonView>());
			myC4 = null;
		}

		//Relay the start of a new round to PlayerAction.
		myPlayer.GetComponent<PlayerAction>().newRound();
	}
}
