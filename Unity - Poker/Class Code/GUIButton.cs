using UnityEngine;
using System.Collections;

/*	CLASS GUIButton
 * 	@Zanice
 * 
 * 	This class holds the implementation of button objects
 * 	used in the interface. Button events are relayed to the 
 * 	GameManager.These buttons can be hidden if their 
 * 	"active" variable is false.
 */

public class GUIButton : MonoBehaviour {
	//Button texture variables.
	public Texture2D allIn;
	public Texture2D raise;
	public Texture2D check;
	public Texture2D fold;

	//Active button variables.
	bool activeAllIn = false;
	bool activeRaise = false;
	bool activeCheck = false;
	bool activeFold = false;

	//Sets if the "All In" button is active.
	public void setAllInActive(bool b) {
		activeAllIn = b;
	}

	//Sets if the "Raise" button is active.
	public void setRaiseActive(bool b) {
		activeRaise = b;
	}

	//Sets if the "Check/Call" button is active.
	public void setCheckActive(bool b) {
		activeCheck = b;
	}

	//Sets if the "Fold" button is active.
	public void setFoldActive(bool b) {
		activeFold = b;
	}

	//Called privately, sets up buttons for use.
	private void OnGUI() {
		//If the "All In" button is active...
		if (activeAllIn) {
			//Display the button, and relay the event it's pressed.
			if (GUI.Button(new Rect(Screen.width / 2 - 256, Screen.height - 74, allIn.width, allIn.height), allIn))
				GameObject.Find("_SCRIPTS").GetComponent<GameManager>().onAllInButton();
		}

		//If the "Raise" button is active...
		if (activeRaise) {
			//Display the button, and relay the event it's pressed.
			if (GUI.Button(new Rect(Screen.width / 2 - 128, Screen.height - 74, raise.width, raise.height), raise))
				GameObject.Find("_SCRIPTS").GetComponent<GameManager>().onRaiseButton();
		}

		//If the "Check/Call" button is active...
		if (activeCheck) {
			//Display the button, and relay the event it's pressed.
			if (GUI.Button(new Rect(Screen.width / 2, Screen.height - 74, check.width, check.height), check))
				GameObject.Find("_SCRIPTS").GetComponent<GameManager>().onCheckButton();
		}

		//If the "Fold" button is active...
		if (activeFold) {
			//Display the button, and relay the event it's pressed.
			if (GUI.Button(new Rect(Screen.width / 2 + 128, Screen.height - 74, fold.width, fold.height), fold))
				GameObject.Find("_SCRIPTS").GetComponent<GameManager>().onFoldButton();
		}
	}
}
