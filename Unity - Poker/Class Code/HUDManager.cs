using UnityEngine;
using System.Collections;

/*	CLASS HUDManager
 * 	@Zanice
 * 
 * 	This class displays and updates the HUD, including
 * 	a list of player's, the current state in the game,
 * 	whose turn it currently is, and the cards in the
 * 	player's hand and in the FTR.
 */

public class HUDManager : MonoBehaviour {
	//GameManager pointer variable.
	GameManager game;

	//HUD text variables.
	public GUIText networkStatus;
	public GUIText gameState;
	public GUIText playerNumber;
	public GUIText playerTurn;
	public GUIText[] playerList;

	//HUD card display variables.
	public GameObject hand1;
	public GameObject hand2;
	public GameObject flop1;
	public GameObject flop2;
	public GameObject flop3;
	public GameObject turn;
	public GameObject river;

	//Card texture array variable, where the array index matches the ID of the card.
	public Texture[] cards;

	void Start () {
		//Initialize the variables.
		game = this.gameObject.GetComponent<GameManager>();

		networkStatus.text = "";
		gameState.text = "";
		playerNumber.text = "";
		playerTurn.text = "";
		playerList[0].text = "";
		playerList[1].text = "";
		playerList[2].text = "";
		playerList[3].text = "";
		playerList[4].text = "";
		playerList[5].text = "";
		playerList[6].text = "";
		playerList[7].text = "";

		hand1.GetComponent<GUITexture>().texture = null;
		hand2.GetComponent<GUITexture>().texture = null;
		flop1.GetComponent<GUITexture>().texture = null;
		flop2.GetComponent<GUITexture>().texture = null;
		flop3.GetComponent<GUITexture>().texture = null;
		turn.GetComponent<GUITexture>().texture = null;
		river.GetComponent<GUITexture>().texture = null;
	}

	//Updates the text of the network status text element.
	public void updateNetworkStatus(string status) {
		networkStatus.text = status;
	}

	//Updates the text of the game state text element, based on the integer ID given.
	public void updateGameState(int state) {
		switch (state) {
		case 1:
			gameState.text = "Waiting for other players...";
			break;
		case 2:
			gameState.text = "Cards have been dealt.";
			break;
		case 3:
			gameState.text = "Flop has been laid.";
			break;
		case 4:
			gameState.text = "Turn has been laid.";
			break;
		case 5:
			gameState.text = "River has been laid.";
			break;
		case 6:
			gameState.text = "Deciding winner.";
			break;
		}
	}

	//Updates the text of the player number text element.
	public void updatePlayerNumber(int number) {
		playerNumber.text = "Player Number: " + number;
	}

	//Updates the text of the player turn text element.
	public void updatePlayerTurn(int player) {
		playerTurn.text = "It is " + game.getPlayers()[player - 1].GetComponent<PlayerManager>().getName() + "'s turn.";
	}

	//Updates the text of the player list.
	public void updatePlayerList(GameObject[] array) {
		if (array != null) {
			//Check players' existence for all eight spots.
			int i = 0;
			while (i < 8) {
				if (i < array.Length) {
					//Player i exists, change text to list player.
					playerList[i].text = array[i].GetComponent<PlayerManager>().getNumber() + ": " + array[i].GetComponent<PlayerManager>().getName();
				}
				else {
					//Player i doesn't exist, erase text.
					playerList[i].text = "";
				}
				i++;
			}
		}
	}

	//Updates the card displays.
	public void updateHUDCards() {
		//Copy the cards in this player's hand and in the FTR to a temporary array for easy access.S
		int[] display = new int[7];
		display[0] = game.deck.getFromHands(game.playerNumber - 1, 0);
		display[1] = game.deck.getFromHands(game.playerNumber - 1, 1);
		for (int i = 1; i < 6; i++) {
			display[i + 1] = game.deck.getFromFTR(i - 1);
		}

		//If the card exists, paint the first card of the player's hand. Otherwise, paint the back of a card.
		if (display[0] != -1)
			hand1.guiTexture.texture = cards[display[0]];
		else
			hand1.guiTexture.texture = cards[52];

		//If the card exists, paint the second card of the player's hand. Otherwise, paint the back of a card.
		if (display[1] != -1)
			hand2.guiTexture.texture = cards[display[1]];
		else
			hand2.guiTexture.texture = cards[52];

		//If the card exists, paint the first card of the FTR. Otherwise, paint the back of a card.
		if (display[2] != -1)
			flop1.guiTexture.texture = cards[display[2]];
		else
			flop1.guiTexture.texture = cards[52];

		//If the card exists, paint the second card of the FTR. Otherwise, paint the back of a card.
		if (display[3] != -1)
			flop2.guiTexture.texture = cards[display[3]];
		else
			flop2.guiTexture.texture = cards[52];

		//If the card exists, paint the third card of the FTR. Otherwise, paint the back of a card.
		if (display[4] != -1)
			flop3.guiTexture.texture = cards[display[4]];
		else
			flop3.guiTexture.texture = cards[52];

		//If the card exists, paint the fourth card of the FTR. Otherwise, paint the back of a card.
		if (display[5] != -1)
			turn.guiTexture.texture = cards[display[5]];
		else
			turn.guiTexture.texture = cards[52];

		//If the card exists, paint the fifth card of the FTR. Otherwise, paint the back of a card.
		if (display[6] != -1)
			river.guiTexture.texture = cards[display[6]];
		else
			river.guiTexture.texture = cards[52];
	}
}
