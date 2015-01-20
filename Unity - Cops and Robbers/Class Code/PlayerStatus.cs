using UnityEngine;
using System.Collections;

/*	CLASS PlayerStatus
 * 	@Zanice
 * 
 * 	This class tracks the player's status variables,
 * 	including class, team, score, and karma.
 */

public class PlayerStatus : MonoBehaviour {
	//Team variables.
	public int team = 0;
	public int copTeam;

	//Class variables.	
	public enum Class {Cop, Robber, Spectator};
	public Class playerClass = Class.Spectator;

	//Score variables.
	int score;
	int totalscore;

	//Karma variables.
	int tempkarma;
	int karma;

	GUIText HUDClass;

	void Start() {
		//Initialize the variables.
		copTeam = -1;
		score = 0;
		totalscore = 0;
		tempkarma = 0;
		karma = 0;
	}

	//Get method for 'score'.
	public int getScore() {
		return score;
	}

	//Get method for 'totalscore'.
	public int getTotalScore() {
		return totalscore;
	}

	//Adds the specified amount to the player's score.
	public void addScore(int i) {
		score += i;
	}

	//(Called as new rounds begin) Transfers the player's current score to their total score.
	public void transferScore() {
		totalscore += score;
		score = 0;
	}

	//Get method for 'karma'.
	public int getKarma() {
		return karma;
	}

	//Adds to the temporary karma.
	public void influenceKarma(int i) {
		tempkarma += i;
	}

	//(Called as new rounds begin) Transfers the player's change in karma to their total karma.
	public void transferKarma() {
		karma += tempkarma;
		tempkarma = 0;
		//Keep the karma in bounds.
		if (karma > 1000)
			karma = 1000;
		if (karma < 0)
			karma = 0;
	}

	//Sets the class of the player to the one specified.
	public void setClass(Class c) {
		//Switching out of Spectator: Reactivate the player's model and controller.
		if (playerClass == Class.Spectator) {
			transform.FindChild("Model").gameObject.SetActive(true);
			transform.GetComponent<CharacterController>().enabled = true;
		}
		//Switching into Spectator: Deactivate the player's model and controller.
		if (c == Class.Spectator) {
			transform.FindChild("Model").gameObject.SetActive(false);
			transform.GetComponent<CharacterController>().enabled = false;
		}
		//Set the class.
		playerClass = c;
	}

	//Takes an integer code and uses it to set the class of the player.
	public void setClass(int i) {
		switch (i) {
		case -1:
			setClass(Class.Spectator);
			break;
		case 0:
			setClass(Class.Cop);
			break;
		case 1:
			setClass(Class.Robber);
			break;
		}
	}

	//Activate the character, allowing him to move around and play.
	public void activate() {
		((MonoBehaviour)transform.GetComponent("CharacterMotor")).enabled = true;
		((MonoBehaviour)transform.GetComponent("FPSInputController")).enabled = true;
		((MonoBehaviour)transform.GetComponent("MouseLook")).enabled = true;
		transform.FindChild("PlayerCamera").GetComponent<MouseLook>().enabled = true;
		transform.GetComponent<PlayerAction>().actable = true;
	}

	//Deactivate the play, locking his camera/position and making him unable to act.
	public void deactivate() {
		((MonoBehaviour)transform.GetComponent("CharacterMotor")).enabled = false;
		((MonoBehaviour)transform.GetComponent("FPSInputController")).enabled = false;
		((MonoBehaviour)transform.GetComponent("MouseLook")).enabled = false;
		transform.FindChild("PlayerCamera").GetComponent<MouseLook>().enabled = false;
		transform.GetComponent<PlayerAction>().actable = false;
	}

	//RPC call, sets the player's team ID.
	[RPC]
	public void setTeam(int id) {
		team = id;
	}

	//RPC call, assigns the specified class to the player.
	[RPC]
	public void assignClass(int copteamID) {
		copTeam = copteamID;
		if (copTeam == team)
			setClass(Class.Cop);
		else if (copTeam != -1)
			setClass(Class.Robber);
	}
}
