using UnityEngine;
using System.Collections;

/*	CLASS CameraModelAction
 * 	@Zanice
 * 
 * 	This class relays method call flags from the camera's 
 * 	models' animations to the PlayerAction class.
 */

public class CameraModelAction : MonoBehaviour {
	//PlayerAction pointer for this player.
	PlayerAction action;
	
	void Start() {
		action = transform.parent.parent.GetComponent<PlayerAction>();
	}

	//Relay the event of firing the shotgun.
	public void shotgunFire() {
		action.onShotgunFire(false);
	}

	//Relay the event of firing the shotgun while aiming down its sight.
	public void shotgunAimedFire() {
		action.onShotgunFire(true);
	}

	//Relay the event of reloading the shotgun.
	public void shotgunReload() {
		action.onShotgunReload();
	}

	//Relay the event of placing a mine.
	public void minePlace() {
		action.onMinePlace();
	}

	//Relay the event of swinging the knife.
	public void knifeSwing() {
		action.onKnifeSwing();
	}

	//Relay the event of throwing the knife.
	public void knifeThrow() {
		action.onKnifeThrow();
	}

	//Relay the event of throwing C4.
	public void c4Throw() {
		action.onC4Throw();
	}

	//Relay the event of firing the BB gun.
	public void bbgunFire() {
		action.onBBGunFire();
	}

	//Relay the event of firing a poison bolt.
	public void poisonboltFire() {
		action.onPoisonBoltFire();
	}
}
