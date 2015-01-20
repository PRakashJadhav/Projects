using UnityEngine;
using System.Collections;

/*	CLASS PlayerHealth
 * 	@Zanice
 * 
 * 	This class tracks the player's health and manages
 * 	both damage and slow effects taken by the player.
 */

public class PlayerHealth : MonoBehaviour {
	//Health variables.
	public int health = 100;
	public int currentHealth;

	//Speed and speed alteration variables.
	float speed;
	float speedchange;
	float speedduration;
	long speedtimer;

	//Damage over time variables.
	int dot;
	float dotspacing;
	int dotiterations;
	long dottimer;
	
	void Start() {
		//Initialize the variables.
		currentHealth = health;
		speed = 1;
		speedchange = 0;
		speedduration = 0f;
		speedtimer = 0;
		dot = 0;
		dotspacing = 0f;
		dotiterations = 0;
		dottimer = 0;
	}
	
	void Update() {
		//If the player's speed is altered, lerp the speed back to normal until the duration is over.
		if (speed != 1f) {
			if (System.DateTime.Now.Ticks >= speedtimer)
				speed = 1;
			else
				speed = 1 + ((speedchange - 1) * ((float) ((speedtimer - System.DateTime.Now.Ticks) / 1000000) / (speedduration * 10)));
		}
		//If a DOT is applied, check the DOT timer.
		if (dot != 0) {
			//If it is time for another DOT tick, apply the damage.
			if (System.DateTime.Now.Ticks >= dottimer) {
				dotiterations--;
				transform.GetComponent<PhotonView>().RPC("takeDamage", PhotonTargets.AllBuffered, dot);
				//If there are no more ticks of the DOT, remove it. Otherwise, reset the timer.
				if (dotiterations == 0) {
					dot = 0;
				}
				else {
					dottimer = System.DateTime.Now.Ticks + (long) (10000000 * dotspacing);
				}
			}
		}
	}

	//RPC call, processes damage-taking for the player.
	[RPC]
	public void takeDamage(int damage) {
		//Subtract the damage from the player's health. If this takes the player's health to 0, the player dies.
		currentHealth -= damage;
		if (currentHealth <= 0) {
			if (currentHealth < 0)
				currentHealth = 0;
			if (transform.GetComponent<PlayerStatus>().playerClass != PlayerStatus.Class.Spectator)
				die();
		}
		//Otherwise, make sure the player does not have more health than the maximum.
		else if (currentHealth > health)
			currentHealth = health;
	}

	//RPC call, affect the speed of the player.	
	[RPC]
	public void applySpeed(float f, float duration) {
		//If this player is the real player (if this method is called on the player object owned by that player's client), adjust the speed.
		if (GameObject.Find("_SCRIPTS").GetComponent<NetworkManager>().myPlayer == transform.gameObject) {
			speedchange = f;
			speedduration = duration;
			speedtimer = System.DateTime.Now.Ticks + (long) (10000000 * duration);
		}
	}

	//RPC call, apply a DOT to the player.
	[RPC]
	public void applyDOT(int damage, float spacing, int iterations) {
		//If this player is the real player (if this method is called on the player object owned by that player's client), apply the DOT.
		if (GameObject.Find("_SCRIPTS").GetComponent<NetworkManager>().myPlayer == transform.gameObject) {
			if (iterations > 0) {
				dot = damage;
				dotspacing = spacing;
				dotiterations = iterations;
				dottimer = System.DateTime.Now.Ticks + (long) (10000000 * spacing);
			}
		}
	}

	//RPC call, reset the health, speed, and DOT's of the player.
	[RPC]
	public void resetHealth() {
		currentHealth = health;
		dot = 0;
		speed = 1;
	}

	//RPC call, kill the player.
	[RPC]
	public void die() {
		//Reset the player's speed and DOT's.
		dot = 0;
		speed = 1;

		//Set the player's class to Spectator.
		transform.GetComponent<PlayerStatus>().setClass(PlayerStatus.Class.Spectator);

		//Relay the player's death.
		GameObject.Find("_SCRIPTS").GetComponent<PhotonView>().RPC("playerDeath", PhotonTargets.MasterClient);
	}
}
