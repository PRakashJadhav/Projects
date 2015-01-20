using UnityEngine;
using System.Collections;

/*	CLASS NetworkManager
 * 	@Zanice
 * 
 * 	This class manages connection to the server
 * 	and distributes player information to the
 * 	clones of the player across clients.
 */

public class NetworkManager : MonoBehaviour {
	//GameManager pointer variable.
	GameManager game;

	//Network connection variables.
	bool roomed;
	bool setup;

	void Start () {
		//Initialize the variables.
		game = this.gameObject.GetComponent<GameManager>();

		roomed = false;
		setup = false;

		//Connect to the server.
		connect();
	}

	void Update () {
		if (setup) {
			if (game.player.GetComponent<PlayerManager>().getNumber() == -1)
				this.GetComponent<PhotonView>().RPC("distributeNumber", PhotonTargets.All);
			if (game.player.GetComponent<PlayerManager>().getName().Equals(""))
				this.GetComponent<PhotonView>().RPC("distributeName", PhotonTargets.All);
			game.updateGame(PhotonNetwork.isMasterClient);
		}
		else {
			if (roomed) {
				int num = game.determinePlayerNumber();
				if (num == 1)
					game.playerName = "Zach";
				else
					game.playerName = "<COMPUTER>";
				Transform spawn = GameObject.Find("Spawn" + num).transform;
				game.player = PhotonNetwork.Instantiate("Player", spawn.position, spawn.rotation, 0) as GameObject;
				GameObject.Find("Main Camera").gameObject.SetActive(false);
				game.updatePlayers();
				game.player.transform.FindChild("Player Camera").gameObject.SetActive(true);

				GameObject.Find("HUD").GetComponent<GUIButton>().setAllInActive(true);
				GameObject.Find("HUD").GetComponent<GUIButton>().setRaiseActive(true);
				GameObject.Find("HUD").GetComponent<GUIButton>().setCheckActive(true);
				GameObject.Find("HUD").GetComponent<GUIButton>().setFoldActive(true);

				setup = true;
			}
		}
	}

	//Begins sequence of connecting to the Photon Unity network.
	void connect() {
		PhotonNetwork.ConnectUsingSettings("0.0.1");
	}

	//Overridden method, attempt to join a room (the one existing room) if any rooms exist.
	void OnJoinedLobby() {
		PhotonNetwork.JoinRandomRoom();
	}

	//Overridden Method, called when a room is successfully joined.
	void OnJoinedRoom() {
		//The player is now roomed.
		roomed = true;
	}

	//Overridden Method, creates a room to join if no room exists in the lobby.
	void OnPhotonRandomJoinFailed() {
		PhotonNetwork.CreateRoom(null);
	}

	//Overridden Method, called when a room is successfully created.
	void OnCreatedRoom() {
		//The player is now roomed.
		roomed = true;
	}

	//Overridden Method, updates the connection status display.
	void OnGUI() {
		//Relay the message to the HUD unless the connection is complete.
		string message = PhotonNetwork.connectionStateDetailed.ToString();
		if (message.Equals("Joined"))
			message = "";
		game.hud.updateNetworkStatus(message);

	}

	//RPC call, distribues the player name to other clients.
	[RPC]
	public void distributeName() {
		game.player.GetComponent<PhotonView>().RPC("setName", PhotonTargets.All, game.playerName);
	}

	//RPC call, distributes the player number to other clients.
	[RPC]
	public void distributeNumber() {
		game.player.GetComponent<PhotonView>().RPC("setNumber", PhotonTargets.All, game.playerNumber);
	}
}
