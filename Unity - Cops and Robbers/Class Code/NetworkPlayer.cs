using UnityEngine;
using System.Collections;

/*	CLASS NetworkPlayer
 * 	@Zanice
 * 
 * 	This class controls the sending and receiving of player
 * 	data concerning player position and rotation.
 * 
 * 	TODO: Synchronize player camera position (to see where other players are aiming).
 */

public class NetworkPlayer : MonoBehaviour {
	//Player orientation variables.
    private Vector3 realPosition;
    private Quaternion realRotation;
	//private Quaternion realAimRotation;

	//Elapsed time variable.
	//private long lastUpdate;

    void Update() {
		//Lerp position and rotation changes for a player.
        if (!transform.GetComponent<PhotonView>().isMine) {
            transform.position = Vector3.Lerp(transform.position, realPosition, .25f);
            transform.rotation = Quaternion.Lerp(transform.rotation, realRotation, .25f);
			//transform.FindChild("Model").FindChild("Aim").rotation = Quaternion.Lerp(transform.FindChild("Model").FindChild("Aim").rotation, realAimRotation, .25f);
        }
    }

    public void OnPhotonSerializeView(PhotonStream stream, PhotonMessageInfo info) {
		//If the stream is sending information, send the position and rotation of the player.
        if (stream.isWriting) {
            stream.SendNext(transform.position);
            stream.SendNext(transform.rotation);
			//stream.SendNext(transform.FindChild("PlayerCamera").rotation);
        }
		//Otherwise, we are receiving from the string. Store the given position and rotation.
        else {
            realPosition = (Vector3) stream.ReceiveNext();
            realRotation = (Quaternion) stream.ReceiveNext();
			//realAimRotation = (Quaternion) stream.ReceiveNext();
        }
    }
}