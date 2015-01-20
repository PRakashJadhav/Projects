using UnityEngine;
using System.Collections;

/*	CLASS PlayerNetworkData
 * 	@Zanice
 * 
 * 	This class handles the sending and receiving
 * 	of player data across the network. For now,
 * 	the position and rotations of players are
 * 	passed, but this class can be used instead
 * 	of RPC calls to perfectly synch the name
 * 	and number of the player as well.
 */

public class PlayerNetworkData : MonoBehaviour {
	//Player orientation variables.
	private Vector3 realPosition;
	private Quaternion realRotation;
	
	void Update() {
		//Update the position and rotation for the players not owned by the client.
		if (!transform.GetComponent<PhotonView>().isMine) {
			transform.position = realPosition;
			transform.rotation = Quaternion.Lerp(transform.rotation, realRotation, .25f);
		}
	}

	//Determines how data is sent/received across the network.
	public void OnPhotonSerializeView(PhotonStream stream, PhotonMessageInfo info) {
		//If information is being sent, send this player object's position and rotation.
		if (stream.isWriting) {
			stream.SendNext(transform.position);
			stream.SendNext(transform.rotation);
		}
		//Otherwise, if information is being received, store the position and rotation received.
		else {
			realPosition = (Vector3) stream.ReceiveNext();
			realRotation = (Quaternion) stream.ReceiveNext();
		}
	}
}
