using UnityEngine;
using System.Collections;

/*	CLASS Deck
 * 	@Zanice
 * 
 * 	This class is used to manage the deck of cards in each
 * 	of its allocations: The deck of undrawn cards, each 
 * 	player's hand, the flop-turn-river, and the cards that
 * 	have been buried. The master client holds the master
 * 	instantiation of the deck, while the other clients are
 * 	sent information to synchronize cards on their end.
 * 
 * 	Cards are represented as integers from 0-52. Card
 * 	assignments are described in methods below and can
 * 	be retrieved via defined methods.
 */

public class Deck {
	//Undrawn deck array variables.
	int[] undrawn;
	int undrawnsize;

	//Hand matrix variable.
	int[,] hands;

	//Flop-turn-river array variable.
	int[] ftr;

	//Buried array variable.
	int[] buried;

	//Serialized card information variables, for relay between clients.
	string serialhands;
	string serialftr;

	public Deck() {
		//Initialize the variables.
		undrawnsize = 52;
		undrawn = new int[52];
		hands = new int[8,2];
		ftr = new int[5];
		buried = new int[3];
		serialhands = "";
		serialftr = "";

		//Use reset() to set up the deck.
		reset();
	}

	//Resets the deck, leaving 'undrawn' with all cards in randomized order 
	//and all other arrays with no cards.
	public void reset() {
		//Refill undrawn as a deck of perfect order.
		for (int i = 0; i < 52; i++)
			undrawn[i] = i;
		undrawnsize = 52;

		//Empty the hand matrix.
		for (int j = 0; j < 8; j++) {
			hands[j, 0] = -1;
			hands[j, 1] = -1;
		}

		//Empty the flop-turn-river.
		for (int k = 0; k < 5; k++)
			ftr[k] = -1;

		//Empty the buried pile.
		buried[0] = -1;
		buried[1] = -1;
		buried[2] = -1;

		//Shuffle the deck by randomly moving cards from 'undrawn' to a temporary array.
		int size = undrawnsize;
		int index;
		int[] shuffled = new int[52];
		while (size > 0) {
			//Until 'undrawn' is empty, randomly move cards from 'undrawn' to 'shuffled'.
			index = Random.Range(0, size);
			shuffled[52 - size] = undrawn[index];

			//If this randomly chosen card is not at the end, it must be replaced to avoid holes
			//in the array. Fill the hole with the end card.
			if (index != size - 1) {
				undrawn[index] = undrawn[size - 1];
			}

			//Decrement the size.
			size--;
		}
		undrawn = shuffled;
	}

	//Get the integer form of the card at the specified index of 'undrawn'.
	public int getFromUndrawn(int i) {
		return undrawn[i];
	}

	//Get the integer form of the card at the specified index of the specified player's hand.
	public int getFromHands(int player, int i) {
		return hands[player, i];
	}

	//Get the integer form of the card at the specified index of 'ftr'.
	public int getFromFTR(int i) {
		return ftr[i];
	}

	//Get the integer form of the card at the specified index of 'buried'.
	public int getFromBuried(int i) {
		return buried[i];
	}

	//Get the string representation of 'hands'.
	public string getSerializedHands() {
		return serialhands;
	}

	//Get the string representation of 'ftr'.
	public string getSerializedFTR() {
		return serialftr;
	}

	//Deal each player, dealing for the specified number of players.
	public string deal(int players) {
		string dealings = "";
		for (int i = 0; i < players; i++) {
			//For both cards added to the hand, concatenate the information to 'dealings' with flag-like delimeters.
			//For simplicity, the cards are drawn from the back of the deck array.
			hands[i, 0] = undrawn[undrawnsize - 1];
			dealings += hands[i, 0] + ".";
			hands[i, 1] = undrawn[undrawnsize - 2];
			dealings += hands[i, 1] + ",";

			//Remove these cards from the deck and adjust its size.
			undrawn[undrawnsize - 1] = -1;
			undrawn[undrawnsize - 2] = -1;
			undrawnsize -= 2;
		}

		//Update the string representation of the hands and return it.
		serialhands = dealings;
		return serialhands;
	}

	//Deal the flop, calling updateFTR() to update 'serialftr'.
	public string flop() {
		ftr[0] = undrawn[undrawnsize - 1];
		ftr[1] = undrawn[undrawnsize - 2];
		ftr[2] = undrawn[undrawnsize - 3];
		undrawnsize -= 3;
		return updateFTR();
	}

	//Deal the turn, calling updateFTR() to update 'serialftr'.
	public string turn() {
		ftr[3] = undrawn[undrawnsize - 1];
		undrawnsize -= 1;
		return updateFTR();
	}

	//Deal the river, calling updateFTR() to update 'serialftr'.
	public string river() {
		ftr[4] = undrawn[undrawnsize - 1];
		undrawnsize -= 1;
		return updateFTR();
	}

	//Update the string representation of the FTR.
	string updateFTR() {
		string cards = "";
		for (int i = 0; i < 5; i++)
			cards += ftr[i] + ".";
		serialftr = cards;
		return cards;
	}

	//Set the specified card of the specified player's hand with the specified card value.
	public void setHand(int player, int i, int val) {
		hands[player, i] = val;
	}

	//Set the specified card of the FTR with the specified card value.
	public void setFTR(int i, int val) {
		ftr[i] = val;
	}

	//Returns the value of the specified card ID, resulting in a value from 2 to 14.
	public static int valueOf(int i) {
		return (i % 13) + 2;
	}

	//Returns the suit of the specified card ID, resulting in a value from 0 to 3.
	public static int suitOf(int i) {
		return i / 13;
	}

	//Translates the specified card ID to the word representation of its value.
	public static string valueNameOf(int i) {
		switch (valueOf(i)) {
		case 2:
			return "Two";
		case 3:
			return "Three";
		case 4:
			return "Four";
		case 5:
			return "Five";
		case 6:
			return "Six";
		case 7:
			return "Seven";
		case 8:
			return "Eight";
		case 9:
			return "Nine";
		case 10:
			return "Ten";
		case 11:
			return "Jack";
		case 12:
			return "Queen";
		case 13:
			return "King";
		case 14:
			return "Ace";
		}
		return "";
	}

	//Translates the specified card ID to the word representation of its suit.
	public static string suitNameOf(int i) {
		switch (suitOf(i)) {
		case 1:
			return "Diamonds";
		case 2:
			return "Spades";
		case 3:
			return "Hearts";
		case 4:
			return "Clubs";
		}
		return "";
	}

	//Translates the specified card ID to its word representation.
	public static string nameOf(int i) {
		return valueNameOf(i) + " of " + suitNameOf(i);
	}
}
