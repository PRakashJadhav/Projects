using UnityEngine;
using System.Collections;

/*	CLASS Spawn
 * 	@Zanice
 * 
 * 	This class gives behavious to spawn object
 * 	in the map. The spawn will be for a specific
 * 	team and will signal if it is occupied by a
 * 	player.
 */

public class Spawn : MonoBehaviour {
	//Team ID variable.
    public int team = -1;

	//Occupancy variable.
    bool occupied;

	//If a physical object begins overlapping with the spawn, the spawn is occupied.
    void OnTriggerEnter(Collider other) {
        occupied = true;
    }

	//If a physical object overlaps with the spawn, the spawn is occupied.
	void OnTriggerStay(Collider other) {
		occupied = true;
	}

	//If a physical object exits the spawn and no longer overlaps with it, the spawn is not occupied.
    void onTriggerExit(Collider other) {
        occupied = false;
    }

	//Get method for 'occupied'.
    public bool isOccupied() {
        return occupied;
    }
}