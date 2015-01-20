using UnityEngine;
using System.Collections;

/*	CLASS HUD
 * 	@Zanice
 * 
 * 	This class manages the HUD elements for the player. As it stands,
 * 	all elements are text-based.
 */

public class HUD : MonoBehaviour {
	//GameManager pointer variable.
	GameManager game;

	//Player variables.
	GameObject player;
	PlayerStatus playerstatus;

	//HUD text element variables.
	GUIText gstate;
	GUIText gclass;
	GUIText gteam;
	GUIText gcopteam;
	GUIText gtimer;
	GUIText ghealth;
	GUIText gequipment;

	void Start () {
		//Initialize the variables.
		game = transform.GetComponent<GameManager>();

		player = null;
		playerstatus = null;

		gclass = GameObject.Find("HUDClass").guiText;
		gstate = GameObject.Find("HUDState").guiText;
		gteam = GameObject.Find("HUDTeam").guiText;
		gcopteam = GameObject.Find("HUDCopTeam").guiText;
		gtimer = GameObject.Find("HUDTimer").guiText;
		ghealth = GameObject.Find("HUDHealth").guiText;
		gequipment = GameObject.Find("HUDEquipment").guiText;
	}

	void Update () {
		//Update the current state of the game.
		gstate.text = "GameState: " + game.state.ToString();

		//If a timer is active, update the value of the timer. Otherwise, erase the timer.
		if (game.timer != 0)
			gtimer.text = (((game.timer - System.DateTime.Now.Ticks) / 10000000) + 1) + "";
		else
			gtimer.text = "";

		//If the player object exists and can have information pulled, update the player information on the HUD.
		if (player == null)
			player = transform.GetComponent<NetworkManager>().myPlayer;
		if ((player != null)&&(playerstatus == null)) {
			if (player.GetComponent<PlayerStatus>() != null)
				playerstatus = player.GetComponent<PlayerStatus>();
		}
		if (playerstatus != null) {
			gclass.text = "Class: " + playerstatus.playerClass.ToString();
			gteam.text = "Team: " + playerstatus.team;
			gcopteam.text = "CopTeam: " + playerstatus.copTeam;
			ghealth.text = "Health: " + player.GetComponent<PlayerHealth>().currentHealth;
			gequipment.text = "Current Equipment: " + player.GetComponent<PlayerAction>().current.ToString();
		}
	}
}
