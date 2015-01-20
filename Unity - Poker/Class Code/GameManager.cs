using UnityEngine;
using System.Collections;

/*	CLASS GameManager
 * 	@Zanice
 * 
 * 	This class manages the rounds of poker played, and is
 * 	primarily "busy" for the master client. The master
 * 	client's manager determines card dealings, etc. and 
 * 	relays the information to the other clients.
 */

public class GameManager : MonoBehaviour {
	//Manager pointer variables.
	public NetworkManager network;
	public HUDManager hud;

	//Master client variable.
	bool host;

	//Player variables.
	public GameObject player;
	public string playerName;
	public int playerNumber;

	//Other player variables.
	GameObject[] players;
	GameObject[] playersInHand;
	int playerTurn;

	//Game state variables.
	public enum State {Initialization, Setup, Deal, Flop, Turn, River, Winner};
	State state;

	//Deck variable.
	public Deck deck;

	void Start () {
		//Instantiate the variables.
		network = this.gameObject.GetComponent<NetworkManager>();
		hud = this.gameObject.GetComponent<HUDManager>();

		host = false;

		player = null;
		playerName = "";
		playerNumber = -1;

		playerTurn = -1;

		state = State.Initialization;

		deck = new Deck();
	}

	//Updates the list of players in the game.
	public void updatePlayers() {
		//Find all of the players.
		players = GameObject.FindGameObjectsWithTag("Player");

		//Sort the players by their numbers. Done using single-pass bubble sort.
		int index = 0;
		GameObject temp;
		//While the sort is still in progress and has not reached the end of the array...
		while (index < players.Length - 1) {
			//If current index is greater than the next, swap and move an index back (unless current index is zero).
			if (players[index].GetComponent<PlayerManager>().getNumber() > players[index + 1].GetComponent<PlayerManager>().getNumber()) {
				temp = players[index];
				players[index] = players[index + 1];
				players[index + 1] = temp;
				//Subtract two; the addition of one at the end of the loop will resolve to a total of -1.
				if (index > 0)
					index -= 2;
			}
			//Otherwise, the current index is smaller or equal to the next. Move ahead.
			index++;
		}
		hud.updatePlayerList(players);
	}

	//Returns the array of players in the game.
	public GameObject[] getPlayers() {
		return players;
	}

	//Finds the player's "number", done as the player is joining the room.
	public int determinePlayerNumber() {
		//Gather all players present.
		updatePlayers();

		//Set the player's number as the next highest. Display and return it.
		playerNumber = players.Length + 1;
		hud.updatePlayerNumber(playerNumber);
		return playerNumber;
	}

	//Updates the game. The master client is in charge of state changes, RPC calls, etc..
	public void updateGame(bool master) {
		host = master;

		//This methods runs after initialization is complete, so change the state to Setup if need be.
		if (state == State.Initialization) {
			state = State.Setup;
			hud.updateGameState(1);
		}
		//If this is the master client...
		if (host) {
			//Check conditions for moving to the next state and do so if possible.
			//Each state change will cause a call to synchronize the other clients' states and card information.

			//Deal
			if ((state == State.Setup)&&(players.Length >= 2)) {
				deck.reset();
				string hands = deck.deal(players.Length);
				this.GetComponent<PhotonView>().RPC("synchStates", PhotonTargets.All, 2);
				this.GetComponent<PhotonView>().RPC("synchHands", PhotonTargets.All, hands);
				this.GetComponent<PhotonView>().RPC("synchTurn", PhotonTargets.All, 1);
			}
			//Flop
			else if((state == State.Deal)&&(playerTurn == -1)) {
				string ftr = deck.flop();
				this.GetComponent<PhotonView>().RPC("synchStates", PhotonTargets.All, 3);
				this.GetComponent<PhotonView>().RPC("synchFTR", PhotonTargets.All, ftr);
				this.GetComponent<PhotonView>().RPC("synchTurn", PhotonTargets.All, 1);
			}
			//Turn
			else if((state == State.Flop)&&(playerTurn == -1)) {
				string ftr = deck.turn();
				this.GetComponent<PhotonView>().RPC("synchStates", PhotonTargets.All, 4);
				this.GetComponent<PhotonView>().RPC("synchFTR", PhotonTargets.All, ftr);
				this.GetComponent<PhotonView>().RPC("synchTurn", PhotonTargets.All, 1);
			}
			//River
			else if((state == State.Turn)&&(playerTurn == -1)) {
				string ftr = deck.river();
				this.GetComponent<PhotonView>().RPC("synchStates", PhotonTargets.All, 5);
				this.GetComponent<PhotonView>().RPC("synchFTR", PhotonTargets.All, ftr);
				this.GetComponent<PhotonView>().RPC("synchTurn", PhotonTargets.All, 1);
			}
			//Winner
			else if((state == State.River)&&(playerTurn == -1)) {
				this.GetComponent<PhotonView>().RPC("synchStates", PhotonTargets.All, 6);
				this.GetComponent<PhotonView>().RPC("synchTurn", PhotonTargets.All, 1);
			}
			//Setup
			else if ((state == State.Winner)&&(playerTurn == -1)) {
				;
			}
		}
		//Otherwise, these actions are performed for guest clients.
		else {
			//If this player is currently dealt, make sure card information is synchronized.
			if ((state != State.Initialization)&&(state != State.Setup)) {
				//If card information does not exist for the hands, call for a hand synchronization.
				if (deck.getFromHands(playerNumber - 1, 0) == -1)
					this.GetComponent<PhotonView>().RPC("callHandSynch", PhotonTargets.All);

				//Depending on the game state, if card information does not exist for the FTR, 
				//call for a FTR synchronization.
				if ((state == State.Flop)&&(deck.getFromFTR(0) == -1))
					this.GetComponent<PhotonView>().RPC("callFTRSynch", PhotonTargets.All);
				else if ((state == State.Turn)&&(deck.getFromFTR(3) == -1))
					this.GetComponent<PhotonView>().RPC("callFTRSynch", PhotonTargets.All);
				else if ((state == State.River)&&(deck.getFromFTR(4) == -1))
					this.GetComponent<PhotonView>().RPC("callFTRSynch", PhotonTargets.All);
			}
		}
	}

	//Handles a press of the "All In" button.
	public void onAllInButton() {
		;
	}

	//Handles a press of the "Raise" button.
	public void onRaiseButton() {
		;
	}

	//Handles a press of the "Check/Call" button.
	public void onCheckButton() {
		//Relay the move to the master client if it is this player's turn.
		if (playerTurn == playerNumber)
			this.GetComponent<PhotonView>().RPC("relayMove", PhotonTargets.All, 1);
	}

	//Handles a press of the "Fold" button.
	public void onFoldButton() {
		;
	}

	//RPC call, directs master client to send out hand information.
	[RPC]
	public void callHandSynch() {
		if (host)
			this.GetComponent<PhotonView>().RPC("synchHands", PhotonTargets.All, deck.getSerializedHands());
	}

	//RPC call, directs master client to send out FTR information.
	[RPC]
	public void callFTRSynch() {
		if (host)
			this.GetComponent<PhotonView>().RPC("synchFTR", PhotonTargets.All, deck.getSerializedFTR());
	}

	//RPC call, parses through the information provided by the master client to synchronize hand information.
	[RPC]
	public void synchHands(string cards) {
		if (!host) {
			int currentplayer = 0;
			int currentcard;

			//While information exists for more hands...
			while (cards.Length > 0) {
				//Find the delimeter for the first card, using the information to parse the ID of the card.
				if (cards.Substring(1, 1).Equals(".")) {
					int.TryParse(cards.Substring(0, 1), out currentcard);
					deck.setHand(currentplayer, 0, currentcard);
					cards = cards.Substring(2);
				}
				else {
					int.TryParse(cards.Substring(0, 2), out currentcard);
					deck.setHand(currentplayer, 0, currentcard);
					cards = cards.Substring(3);
				}

				//Find the delimeter for the second card, using the information to parse the ID of the card.
				if (cards.Substring(1, 1).Equals(",")) {
					int.TryParse(cards.Substring(0, 1), out currentcard);
					deck.setHand(currentplayer, 1, currentcard);
					cards = cards.Substring(2);
				}
				else {
					int.TryParse(cards.Substring(0, 2), out currentcard);
					deck.setHand(currentplayer, 1, currentcard);
					cards = cards.Substring(3);
				}
				currentplayer++;
			}

			//Update the HUD with this new information.
			hud.updateHUDCards();
		}
	}

	//RPC call, parses through the information provided by the master client to synchronize FTR information.
	[RPC]
	public void synchFTR(string cards) {
		if (!host) {
			int i = 0;
			int currentcard;
			//For all five cards of the FTR...
			while (i < 5) {
				//Find the delimeter for the card, using the information to parse the ID of the card.
				if (cards.Substring(1, 1).Equals(".")) {
					int.TryParse(cards.Substring(0, 1), out currentcard);
					deck.setFTR(i, currentcard);
					cards = cards.Substring(2);
				}
				else {
					int.TryParse(cards.Substring(0, 2), out currentcard);
					deck.setFTR(i, currentcard);
					cards = cards.Substring(3);
				}
				i++;
			}
		}

		//Update the HUD with this new information.
		hud.updateHUDCards();
	}

	//RPC call, synch states with the given ID of the new state.
	[RPC]
	public void synchStates(int stateID) {
		//If the client is not currently initializing...
		if (state != State.Initialization) {
			switch (stateID) {
			//Switch to Setup.
			case 1:
				state = State.Setup;
				break;
			//Switch to Deal.
			case 2:
				state = State.Deal;
				break;
			//If the player is a part of this deal, switch to Flop.
			case 3:
				if (state != State.Setup)
					state = State.Flop;
				break;
			//If the player is a part of this deal, switch to Turn.
			case 4:
				if (state != State.Setup)
					state = State.Turn;
				break;
			//If the player is a part of this deal, switch to River.
			case 5:
				if (state != State.Setup)
					state = State.River;
				break;
			//If the player is a part of this deal, switch to Winner.
			case 6:
				if (state != State.Setup)
					state = State.Winner;
				break;
			}

			//If the player is a part of this deal, update the HUD.
			if ((state != State.Initialization)&&(state != State.Setup)) {
				hud.updateGameState(stateID);
				hud.updateHUDCards();
			}
		}
	}

	//RPC call, synch the turn of the current player.
	[RPC]
	public void synchTurn(int turn) {
		playerTurn = turn;
		if (turn != -1)
			hud.updatePlayerTurn(playerTurn);
	}

	//RPC call, relay the player's move to the master client.
	[RPC]
	public void relayMove(int move) {
		if (host) {
			switch (move) {
			case 1: //Check/Call
				break;
			case 2: //Fold
				break;
			}

			//If all players have gone, signal the next state by setting the turn to -1. Otherwise, increment the turn.
			if (playerTurn == players.Length)
				this.GetComponent<PhotonView>().RPC("synchTurn", PhotonTargets.All, -1);
			else
				this.GetComponent<PhotonView>().RPC("synchTurn", PhotonTargets.All, playerTurn + 1);
		}
	}
}
