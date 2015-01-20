using UnityEngine;
using System.Collections;

/*	CLASS EqC4 EXTENDS Equipment
 * 	@Zanice
 * 
 * 	This class determines the behavior of C4. The class is an
 * 	implementation of the Equipment abstract class.
 */

public class EqC4 : Equipment {
	//Maximum damage variable, which is the damage dealt at the closest range possible.
	public int maxDamage;

	//Explosion state variables.
	bool triggered;
	bool exploded;

	//Delay variable, which determines the smallest amount of time between the C4 becoming
	//a world object and the player triggering its explosion.
	public float delay;
	
	void Start() {
		//Initialize the variables and start the timer.
		triggered = false;
		exploded = false;
		setTimer(delay);
	}
	
	void Update() {
		//If the player triggered an explosion, and if the delay timer is complete, explode.
		if (triggered) {
			if (timerExpired())
				detonate();
		}
	}

	//Implemented method, determines how the C4 responds to being toggled.
	public override void onToggle() {
		;
	}

	//Implemented method, determines how the C4 responds to being triggered.
	public override void onTrigger() {
		//Start the explosion process by setting 'trigger' to true.
		triggered = true;
	}

	//Implemented method, determines how the C4 responds to being damaged.
	public override void onDamaged() {
		//If the C4 is ever damaged, explode.
		detonate();
	}

	//Explode the C4.
	public void detonate() {
		if (!exploded) {
			//Determine players hit by the C4's explosion and deal damage to them.
			GameObject[] players = getAOETargets("Player");
			if (players != null) {
				for (int i = 0; i < players.Length; i++) {
					int hit = maxDamage; //TODO: Scale the damage depending on range.
					players[i].GetComponent<PhotonView>().RPC("damageEffect", PhotonTargets.AllBuffered, hit);
				}
			}

			//Disable and destroy the C4.
			exploded = true;
			GameObject.Find("_SCRIPTS").GetComponent<NetworkManager>().myC4 = null;
			destroyThis();
		}
	}
}
