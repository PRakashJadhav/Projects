using UnityEngine;
using System.Collections;

/*	CLASS EqMine EXTENDS Equipment
 * 	@Zanice
 * 
 * 	This class determines the behavior of mines. The class is an
 * 	implementation of the Equipment abstract class.
 */

public class EqMine : Equipment {
	//Timer duration variables.
	public float armTimer;
	public float detonationTimer;

	//Rage variable.
	public float triggerRange;

	//Maximum damage variable, which is the damage dealt at the closest range possible.
	public int maxDamage;

	//Explosion state variables.
	bool armed;
	bool triggered;
	bool exploded;
	
	void Start () {
		//Initialize the variables and start the arming timer.
		armed = false;
		triggered = false;
		exploded = false;
		setTimer(armTimer);
	}
	
	void Update () {
		//If the mine is not armed and the arming timer has expired, arm the mine.
		if (!armed) {
			if (timerExpired()) {
				armed = true;
			}
		}
		//Otherwise, if the mine is armed but not triggered, check for AOE targets at the trigger range.
		else if (!triggered) {
			GameObject[] players = getAOETargets("Player", triggerRange);

			//If targets exists within the mine's trigger range, trigger the mine.
			if (players != null) {
				setTimer(detonationTimer);
				triggered = true;
			}
		}
		//Otherwise, if the mine is armed, triggered, and the detonation timer has expired, explode.
		else if ((timerExpired())&&(triggered)) {
			detonate();
		}
	}
	
	//Implemented method, determines how the mine responds to being toggled.
	public override void onToggle() {
		;
	}
	
	//Implemented method, determines how the mine responds to being triggered.
	public override void onTrigger() {
		;
	}
	
	//Implemented method, determines how the mine responds to being damaged.
	public override void onDamaged() {
		//If the mine is ever damaged, explode.
		detonate();
	}

	//Explode the mine.
	public void detonate() {
		if (!exploded) {
			//Determine players hit by the mine's explosion and deal damage to them.
			GameObject[] players = getAOETargets("Player");
			if (players != null) {
				for (int i = 0; i < players.Length; i++) {
					int hit = maxDamage; //TODO: Scale the damage depending on range.
					players[i].GetComponent<PhotonView>().RPC("damageEffect", PhotonTargets.AllBuffered, hit);
				}
			}
			
			//Disable and destroy the mine.
			exploded = true;
			GameObject.Find("_SCRIPTS").GetComponent<NetworkManager>().myC4 = null;
			destroyThis();
		}
	}
}
