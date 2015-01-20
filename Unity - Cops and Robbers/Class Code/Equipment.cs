using UnityEngine;
using System.Collections;

/*	ABSTRACT CLASS Equipment
 * 	@Zanice
 * 
 * 	This class serves as the basis for a stand-alone
 * 	equipment entity in the game. Most equipment will
 * 	be some form of explosive, so related methods are
 * 	defined here.
 */

public abstract class Equipment : MonoBehaviour {
	//Range radius variable.
	public float range;

	//Timer variable.
	long timer;

	//Get method for 'timer'.
	public long getTimer() {
		return timer;
	}

	//Set method for 'timer'; sets the timer to the current time plus the number of seconds given.
	public void setTimer(float seconds) {
		timer = System.DateTime.Now.Ticks + (long) (10000000 * seconds);
	}

	//Returns true if the current timer has expired.
	public bool timerExpired() {
		return System.DateTime.Now.Ticks >= timer;
	}

	//Overloaded version of getAOETargets() that finds targets by the equipment's range.
	public GameObject[] getAOETargets(string tag) {
		return getAOETargets(tag, range);
	}

	//Finds AOE Targets that have the given tag, are within line of sight to the equipment, and are
	//within the given distance to the equipment.
	public GameObject[] getAOETargets(string tag, float distance) {
		//Find all objects with the given tag.
		GameObject[] objects = GameObject.FindGameObjectsWithTag(tag);

		//Declare variables for raycast attempts.
		RaycastHit hit;
		int num = 0;

		//For each object found with the tag, cast a ray from the equipment to the object.
		for (int i = 0; i < objects.Length; i++) {
			if (objects[i] != null) {
				//If the raycast encountered some object, determine what has been hit.
				if (Physics.Raycast(transform.position, objects[i].transform.position - transform.position, out hit, distance)) {
					//If the raycast did not collide with the equipment's model...
					if (hit.transform != transform) {
						//...and if the current object was hit, increment the number of targets and keep the object in the array.
						if (hit.transform == objects[i].transform)
							num++;
						//Otherwise, the object is not within the equipment's line of sight and is therefore not a target.
						else
							objects[i] = null;
					}
					//Otherwise, if the equipment was hit with its own raycast, attempt another raycast at the point of collision.
					else {
						//If the raycast encountered some object...
						if (Physics.Raycast(hit.point, objects[i].transform.position - hit.point, out hit, distance)) {
							//...and if the current object was hit, increment the number of targets and keep the object in the array.
							if (hit.transform == objects[i].transform)
								num++;
							//Otherwise, the object is not within the equipment's line of sight and is therefore not a target.
							else
								objects[i] = null;
						}
						//Otherwise, if no object was hit, the current object is not a target.
						else
							objects[i] = null;
					}
				}
				//Otherwise, if no object was hit, the current object is not a target.
				else
					objects[i] = null;
			}
		}
		//If there is at least one target for the equipment, return an array of the targets.
		if (num != 0) {
			GameObject[] hits = new GameObject[num];
			num = 0;
			for (int i = 0; i < objects.Length; i++) {
				if (objects[i] != null) {
					hits[num] = objects[i];
					num++;
				}
			}
			return hits;
		}
		//If there are no targets, return null.
		return null;
	}

	//Abstract method, handles the event that a player toggles the equipment.
	public abstract void onToggle();

	//Abstract method, handles the event that a player triggers the equipment.
	public abstract void onTrigger();

	//Abstract method, handles the event that a player damages the equipment by some means.
	public abstract void onDamaged();

	//Destroys this equipment, removing it from play.
	public void destroyThis() {
		PhotonNetwork.Destroy(transform.GetComponent<PhotonView>());
	}
}
