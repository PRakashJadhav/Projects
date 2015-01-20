using UnityEngine;
using System.Collections;

/*	CLASS PlayerManager
 * 	@Zanice
 * 
 * 	This class holds the informater of players
 * 	for each client.
 */

public class PlayerManager : MonoBehaviour {
	//Player information variables.
	string pname;
	int pnumber;

	void Start () {
		//Initialize the variables.
		pname = "";
		pnumber = -1;
	}

	//Get method for 'pname'.
	public string getName() {
		return pname;
	}

	//Get method for 'pnumber'.
	public int getNumber() {
		return pnumber;
	}

	//RPC call, sets the player's name.
	[RPC]
	public void setName(string s) {
		//Set the name and call the GameManager to update the list of players.
		pname = s;
		GameObject.Find("_SCRIPTS").GetComponent<GameManager>().updatePlayers();
	}

	//RPC call, sets the player's number.	
	[RPC]
	public void setNumber(int n) {
		pnumber = n;
	}
}
