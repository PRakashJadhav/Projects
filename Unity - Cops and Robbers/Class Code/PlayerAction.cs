using UnityEngine;
using System.Collections;

/*	CLASS PlayerAction
 * 	@Zanice
 * 
 * 	This class manages the player's current status,
 * 	inventory, camera animation, and actions.
 */

public class PlayerAction : MonoBehaviour {
	//NetworkManager pointer variable.
	NetworkManager network;

	//Item proprety variables.
	public float ShotgunRange;
	public int ShotgunDamage;
	public float KnifeRange;
	public int KnifeDamage;
	public float BBGunRange;
	public int BBGunDamage;
	public float PoisonBoltRange;
	public int PoisonBoltDamage;
	public int PoisonBoltDOT;
	public Transform aimPoint;

	//Crosshair texture variable.
	GUITexture crosshair;
	
	//Inventory/Ammo variables.
	public int shotgun;
	public int shotgunclip;
	public int armguard;
	public bool armor;
	public int mine;
	public int knife;
	public int c4;
	public int bbgun;
	public int poisonbolt;
	
	//Weapon Selection variables.
	public bool isCop;
	public int selected;
	public enum Equipment {None, Shotgun, Armguard, Armor, Mine, Knife, C4, BBGun, PoisonBolt};
	public Equipment current;
	public Equipment[] slotsCop;
	public Equipment[] slotsRobber;
	
	//Action variables.
	public bool actable;
	long cooldown;
	long togglecooldown;
	
	//Animation variables.
	Animator anim;
	GameObject camshotgun;
	GameObject cammine;
	GameObject camknife;
	GameObject camc4;
	bool switched;
	
	void Start() {
		//Initialize variables.
		network = GameObject.Find("_SCRIPTS").GetComponent<NetworkManager>();

		crosshair = GameObject.Find("Crosshair").GetComponent<GUITexture>();
		crosshair.enabled = true;
		
		actable = false;
		
		shotgun = 0;
		shotgunclip = 0;
		armguard = 0;
		armor = false;
		mine = 0;
		
		knife = 0;
		c4 = 0;
		bbgun = 0;
		poisonbolt = 0;
		
		slotsCop = new Equipment[5];
		slotsRobber = new Equipment[5];
		slotsCop[0] = Equipment.None;
		slotsCop[1] = Equipment.Shotgun;
		slotsRobber[0] = Equipment.None;
		slotsRobber[1] = Equipment.Knife;
		
		selected = 0;
		current = slotsCop[0];
		
		cooldown = 0;
		togglecooldown = 0;
		
		isCop = true;
		
		anim = null;
		
		camshotgun = transform.FindChild("PlayerCamera").FindChild("CameraShotgun").gameObject;
		cammine = transform.FindChild("PlayerCamera").FindChild("CameraMine").gameObject;
		camknife = transform.FindChild("PlayerCamera").FindChild("CameraKnife").gameObject;
		camc4 = transform.FindChild("PlayerCamera").FindChild("CameraC4").gameObject;

		switched = false;
	}
	
	void Update() {
		//Reset 'switched'.
		switched = false;

		//If the player is currently a spectator, set their current equipment to "None".
		if (transform.GetComponent<PlayerStatus>().playerClass == PlayerStatus.Class.Spectator) {
			selected = 0;
			switchEquipment(Equipment.None);
			return;
		}
		//Otherwise, the player is a part of the game and must be updated accordingly. 
		else {
			//Determine if the player is a cop or robber.
			if (transform.GetComponent<PlayerStatus>().playerClass == PlayerStatus.Class.Cop)
				isCop = true;
			else
				isCop = false;

			//If the player can act, update the character based on player input.
			if (actable) {
				//If the player is off cooldown, handle inputs by the player to change weapons.
				if (offCooldown(1)) {
					//If the selected weapon is no longer equippable (out of ammo, etc.), change to the default weapon.
					if (!equippable(selected)) {
						selected = 1;
						if (isCop)
							switchEquipment(slotsCop[1]);
						else
							switchEquipment(slotsRobber[1]);
					}


					//Process mousewheel inputs, if they exist, for changing weapons.
					float scroll = Input.GetAxis("Scroll");
					if (scroll != 0) {
						bool valid = false;
						while (!valid) {
							if (Input.GetAxis("Scroll") > 0)
								selected = (selected + 1) % 5;
							else
								selected = (selected + 4) % 5;
							if (equippable(selected)) {
								valid = true;
							}
						}
					}

					//Process key inputs, if they exist, for changing weapons.
					int tempswitch = 0;
					bool pressed = false;
					if (Input.GetKeyDown(KeyCode.Alpha1)) {
						tempswitch = 0;
						pressed = true;
					}
					else if (Input.GetKeyDown(KeyCode.Alpha2)) {
						tempswitch = 1;
						pressed = true;
					}
					else if (Input.GetKeyDown(KeyCode.Alpha3)) {
						tempswitch = 2;
						pressed = true;
					}
					else if (Input.GetKeyDown(KeyCode.Alpha4)) {
						tempswitch = 3;
						pressed = true;
					}
					else if (Input.GetKeyDown(KeyCode.Alpha5)) {
						tempswitch = 4;
						pressed = true;
					}
					if ((pressed)&&(equippable(tempswitch)))
						selected = tempswitch;


					//If a weapon change has been called, change the weapon and put the player on cooldown.
					if (isCop) {
						if (current != slotsCop[selected]) {
							switchEquipment(slotsCop[selected]);
							setCooldown(1, .5f);
						}
					}
					else {
						if (current != slotsRobber[selected]) {
							switchEquipment(slotsRobber[selected]);
							setCooldown(1, .5f);
						}
					}
				}

				//If the player has not switched equipment and is off cooldown, process other possible inputs.
				if (!switched) {
					if (offCooldown(1)) {
						//Check input for primary action.
						if (Input.GetButtonDown("Fire1")) {
							switch (current) {
							case Equipment.Shotgun:
								//Shoot the shotgun.
								if (shotgunclip > 0) {
									if (anim != null)
										anim.Play("ShootUnaimed");
								}
								break;
							case Equipment.Mine:
								//Place a mine.
								if (mine > 0) {
									;
								}
								break;
							case Equipment.Knife:
								//Swing with the knife.
								if (knife > 0) {
									if (anim != null)
										anim.Play("Stab");
								}
								break;
							case Equipment.C4:
								//Throw the C4.
								if (c4 > 0) {
									;
								}
								break;
							case Equipment.PoisonBolt:
								//Shoot a poison bolt.
								if (poisonbolt > 0) {
									;
								}
								break;
							case Equipment.BBGun:
								//Shoot the BB gun.
								if (bbgun > 0)
									;
								break;
							}
						}
						//Handle reload inputs.
						if (Input.GetKeyDown(KeyCode.R)) {
							//Reload the shotgun.
							if (current == Equipment.Shotgun)
								;//reloaddown = true;
						}
						if (Input.GetKey(KeyCode.R)) {
							//Craft a new knife.
							if (current == Equipment.Knife)
								;
						}
					}
					//Check input for secondary action.
					if (Input.GetButtonDown("Fire2"))
						//Throw the knife.
						if (current == Equipment.Knife)
							;
					if (Input.GetButton("Fire2")) {
						//Aim down the sight of the shotgun.
						if (current == Equipment.Shotgun)
							;
					}
				}
				
				//Check input for equipment trigger.
				if (Input.GetKeyDown(KeyCode.Q)) {
					//Detonate the C4.
					if (network.myC4 != null)
						network.myC4.GetComponent<EqC4>().onTrigger();
				}
				
				
				/*
				//Main Action
				if (Input.GetButtonDown("Fire1")) {
					if (offCooldown(1)) {
						RaycastHit hit;
						switch (current) {
						case Equipment.Shotgun:
                            if (shotgunclip > 0) {
							    Physics.Raycast(aimPoint.position, aimPoint.forward, out hit, ShotgunRange);
							    if (hit.transform != null) {
								    if (hit.transform.GetComponent<PlayerHealth>() != null) {
									    hit.transform.GetComponent<PhotonView>().RPC("takeDamage", PhotonTargets.AllBuffered, ShotgunDamage);
								    }
							    }
							    shotgunclip--;
							    setCooldown(1, 0f);
                            }
							break;
						case Equipment.Mine:
                            if (mine > 0) {
							    Physics.Raycast(transform.position, -transform.up, out hit, 200);
							    if (hit.transform != null) {
								    network.spawnMine(hit.point);
							    }
							    mine--;
							    setCooldown(1, 0f);
                            }
							break;
						case Equipment.Knife:
                            if (knife > 0) {
							    Physics.Raycast(aimPoint.position, aimPoint.forward, out hit, KnifeRange);
							    if (hit.transform != null) {
								    if (hit.transform.GetComponent<PlayerHealth>() != null) {
									    if (Vector3.Angle(transform.position - hit.transform.position, hit.transform.forward) > 30f)
										    hit.transform.GetComponent<PhotonView>().RPC("takeDamage", PhotonTargets.AllBuffered, KnifeDamage);
									    else
										    hit.transform.GetComponent<PhotonView>().RPC("armguardEffect", PhotonTargets.AllBuffered, KnifeDamage);
								    }
							    }
                            }
							break;
						case Equipment.C4:
                            if (c4 > 0) {
							    network.spawnC4(transform.position);
							    c4--;
							    setCooldown(1, 0f);
                            }
							break;
						case Equipment.BBGun:
                            if (bbgun > 0) {
							    Physics.Raycast(aimPoint.position, aimPoint.forward, out hit, BBGunRange);
							    if (hit.transform != null) {
								    if (hit.transform.GetComponent<PlayerHealth>() != null) {
									    hit.transform.GetComponent<PhotonView>().RPC("takeDamage", PhotonTargets.AllBuffered, BBGunDamage);
								    }
							    }
							    bbgun--;
							    setCooldown(1, 0f);
                            }
							break;
						case Equipment.PoisonBolt:
                            if (poisonbolt > 0) {
							    Physics.Raycast(aimPoint.position, aimPoint.forward, out hit, PoisonBoltRange);
							    if (hit.transform != null) {
								    if (hit.transform.GetComponent<PlayerHealth>() != null) {
									    hit.transform.GetComponent<PhotonView>().RPC("takeDamage", PhotonTargets.AllBuffered, PoisonBoltDamage);
									    hit.transform.GetComponent<PhotonView>().RPC("applySpeed", PhotonTargets.AllBuffered, .5f, 10f);
									    hit.transform.GetComponent<PhotonView>().RPC("applyDOT", PhotonTargets.AllBuffered, PoisonBoltDOT, 2.5f, 4);
								    }
							    }
							    poisonbolt--;
							    setCooldown(1, 0f);
                            }
							break;
						}
					}
				}
				//Side Action
				if (Input.GetButtonDown("Fire2")) {
					if (offCooldown(1)) {
						;
					}
				}
				//Equipment Toggle
				if (Input.GetKeyDown(KeyCode.Z)) {
					if (offCooldown(2)) {
						;
					}
				}
                */
			}
		}
	}

	//Returns true if the equipment in the specified slot is equippable.
	bool equippable(int i) {
		Equipment eq;

		//Check if the equipment can be equipped, depending on what class the player currently is.
		if (isCop) {
			eq = slotsCop[i];
			//"None" and "Shotgun" are always equippable
			if ((eq == Equipment.Armguard)&&(armguard == 0))
				return false;
			if (eq == Equipment.Armor)
				return false;
			if ((eq == Equipment.Mine)&&(mine == 0))
				return false;
		}
		else {
			eq = slotsRobber[i];
			//"None" and "Knife" are always equippable
			if ((eq == Equipment.C4)&&(c4 == 0))
				return false;
			if ((eq == Equipment.BBGun)&&(bbgun == 0))
				return false;
			if ((eq == Equipment.PoisonBolt)&&(poisonbolt == 0))
				return false;
		}
		return true;
	}

	//Switches the current equipment of the player.
	public void switchEquipment(Equipment neweq) {
		//Deactivate the current weapon.
		anim = null;
		switch (current) {
		case Equipment.None:
			break;
		case Equipment.Shotgun:
			camshotgun.SetActive(false);
			break;
		case Equipment.Armguard:
			break;
		case Equipment.Armor:
			break;
		case Equipment.Mine:
			cammine.SetActive(false);
			break;
		case Equipment.Knife:
			camknife.SetActive(false);
			break;
		case Equipment.C4:
			camc4.SetActive(false);
			break;
		case Equipment.BBGun:
			break;
		case Equipment.PoisonBolt:
			break;
		}

		//Activate the new weapon.
		current = neweq;
		switch (current) {
		case Equipment.None:
			break;
		case Equipment.Shotgun:
			camshotgun.SetActive(true);
			anim = camshotgun.GetComponent<Animator>();
			break;
		case Equipment.Armguard:
			break;
		case Equipment.Armor:
			break;
		case Equipment.Mine:
			cammine.SetActive(true);
			anim = cammine.GetComponent<Animator>();
			break;
		case Equipment.Knife:
			camknife.SetActive(true);
			anim = camknife.GetComponent<Animator>();
			break;
		case Equipment.C4:
			camc4.SetActive(true);
			anim = camc4.GetComponent<Animator>();
			break;
		case Equipment.BBGun:
			break;
		case Equipment.PoisonBolt:
			break;
		}

		//Set 'switched' to true since equipment was switched.
		switched = true;
	}

	//Applies a cooldown timer for the player.
	public void setCooldown(int id, float f) {
		//If the ID specified is equal to 1, set the action cooldown.
		if (id == 1) {
			if (System.DateTime.Now.Ticks + (long) (10000000 * f) > cooldown)
				cooldown = System.DateTime.Now.Ticks + (long) (10000000 * f);
		}
		//Otherwise, if the ID specified is equal to 2, set the toggle cooldown.
		else if (id == 2) {
			if (System.DateTime.Now.Ticks + (long) (10000000 * f) > togglecooldown)
				togglecooldown = System.DateTime.Now.Ticks + (long) (10000000 * f);
		}
	}

	//Returns true if the specified cooldown is completed.
	public bool offCooldown(int id) {
		if (id == 1)
			return System.DateTime.Now.Ticks >= cooldown;
		else if (id == 2)
			return System.DateTime.Now.Ticks >= togglecooldown;
		return false;
	}

	//Sets the equipment slots as a cop and robber.
	public void setSets(Equipment cop1, Equipment cop2, Equipment cop3, Equipment rob1, Equipment rob2, Equipment rob3) {
		slotsCop[2] = cop1;
		slotsCop[3] = cop2;
		slotsCop[4] = cop3;
		slotsRobber[2] = rob1;
		slotsRobber[3] = rob2;
		slotsRobber[4] = rob3;
	}

	//Applies variable changes for the start of a new round.
	public void newRound() {
		//Empty the player's inventory.
		shotgun = 0;
		shotgunclip = 0;
		armguard = 0;
		armor = false;
		mine = 0;
		knife = 0;
		c4 = 0;
		bbgun = 0;
		poisonbolt = 0;

		//If the player is a spectator, we're done.
		if (transform.GetComponent<PlayerStatus>().playerClass == PlayerStatus.Class.Spectator) {
			return;
		}
		else {
			//Otherwise, determine what class the player is.
			if (transform.GetComponent<PlayerStatus>().playerClass == PlayerStatus.Class.Cop)
				isCop = true;
			else
				isCop = false;
			for (int i = 0; i < 5; i++) {
				//If the player is a cop, reset the inventory of cop equipment.
				if (isCop) {
					switch (slotsCop[i]) {
					case Equipment.Shotgun:
						shotgun = 30;
						shotgunclip = 6;
						break;
					case Equipment.Armguard:
						armguard = 1;
						break;
					case Equipment.Armor:
						armor = true;
						break;
					case Equipment.Mine:
						mine = 1;
						break;
					}
				}
				//Otherwise, the player is a robber. Reset the inventory of robber equipment.
				else {
					switch (slotsRobber[i]) {
					case Equipment.Knife:
						knife = 1;
						break;
					case Equipment.C4:
						c4 = 1;
						break;
					case Equipment.BBGun:
						bbgun = 20;
						break;
					case Equipment.PoisonBolt:
						poisonbolt = 1;
						break;
					}
				}
			}
		}

		//Change the equipment of the player to the default.
		//selected = 1;
		selected = 0;
		if (isCop)
			switchEquipment(slotsCop[selected]);
		else
			switchEquipment(slotsRobber[selected]);
	}

	//Processes the event the player fires the shotgun.
	public void onShotgunFire(bool aimed) {
		//Cast a ray to see if anyone is hit, and apply damage if so.
		RaycastHit hit;
		Physics.Raycast(aimPoint.position, aimPoint.forward, out hit, ShotgunRange);
		if (hit.transform != null) {
			if (hit.transform.GetComponent<PlayerHealth>() != null) {
				hit.transform.GetComponent<PhotonView>().RPC("takeDamage", PhotonTargets.AllBuffered, ShotgunDamage);
			}
		}

		//Reduce the clip and put the player on cooldown.
		shotgunclip--;
		setCooldown(1, 0f);
	}

	//Processes the event the player reloads the shotgun.
	public void onShotgunReload() {
		
	}

	//Processes the event the player places a mine.
	public void onMinePlace() {
		//Direct the network to instantiate a mine on the ground below the player.
		RaycastHit hit;
		Physics.Raycast(transform.position, -transform.up, out hit, 200);
		if (hit.transform != null) {
			network.spawnMine(hit.point);
		}

		//Reduce the mine inventory and put the player on cooldown.
		mine--;
		setCooldown(1, 0f);
	}

	//Processes the event the player swings the knife.
	public void onKnifeSwing() {
		//Cast a ray to see if anyone is hit, and if so attempt to apply damage.
		RaycastHit hit;
		Physics.Raycast(aimPoint.position, aimPoint.forward, out hit, KnifeRange);

		//If an object is hit...
		if (hit.transform != null) {
			//...and the object hit is a player...
			if (hit.transform.GetComponent<PlayerHealth>() != null) {
				//...and if the player hit is not directly facing the player, apply damage.
				if (Vector3.Angle(transform.position - hit.transform.position, hit.transform.forward) > 30f)
					hit.transform.GetComponent<PhotonView>().RPC("takeDamage", PhotonTargets.AllBuffered, KnifeDamage);
				//Otherwise, if the other player is directly facing the player, apply damage via the event the other player has an armguard out.
				else
					hit.transform.GetComponent<PhotonView>().RPC("armguardEffect", PhotonTargets.AllBuffered, KnifeDamage);
			}
		}
	}

	//Processes the event the player throws the knife.
	public void onKnifeThrow() {
		
	}

	//Processes the event the player throws the C4.
	public void onC4Throw() {
		//Direct the network to instantiate a thrown C4 at the player's location, reduce the C4 inventory and put the player on cooldown.
		network.spawnC4(transform.position);
		c4--;
		setCooldown(1, 0f);
	}

	//Processes the event the player fires the BB gun.
	public void onBBGunFire() {
		//Cast a ray to see if anyone is hit, and apply damage if so.
		RaycastHit hit;
		Physics.Raycast(aimPoint.position, aimPoint.forward, out hit, BBGunRange);
		if (hit.transform != null) {
			if (hit.transform.GetComponent<PlayerHealth>() != null) {
				hit.transform.GetComponent<PhotonView>().RPC("takeDamage", PhotonTargets.AllBuffered, BBGunDamage);
			}
		}

		//Reduce the ammo of the BB gun and put the player on cooldown.
		bbgun--;
		setCooldown(1, 0f);
	}
	
	public void onPoisonBoltFire() {
		//Cast a ray to see if anyone is hit, and apply damage, damage over time, and a slow effect if so.
		RaycastHit hit;
		Physics.Raycast(aimPoint.position, aimPoint.forward, out hit, PoisonBoltRange);
		if (hit.transform != null) {
			if (hit.transform.GetComponent<PlayerHealth>() != null) {
				hit.transform.GetComponent<PhotonView>().RPC("takeDamage", PhotonTargets.AllBuffered, PoisonBoltDamage);
				hit.transform.GetComponent<PhotonView>().RPC("applySpeed", PhotonTargets.AllBuffered, .5f, 10f);
				hit.transform.GetComponent<PhotonView>().RPC("applyDOT", PhotonTargets.AllBuffered, PoisonBoltDOT, 2.5f, 4);
			}
		}

		//Reduce the inventory of poison bolts and put the player on cooldown.
		poisonbolt--;
		setCooldown(1, 0f);
	}


	//RPC call, take damage according to if the player has an armguard equipped currently.
	[RPC]
	public void armguardEffect(int damage) {
		if (GameObject.Find("_SCRIPTS").GetComponent<NetworkManager>().myPlayer == transform.gameObject) {
			//If an armguard is equipped, the armguard is destroyed but the player avoids damage.
			if (armguard == 1)
				armguard = 0;
			//Otherwise, the player takes damage.
			else
				transform.GetComponent<PhotonView>().RPC("takeDamage", PhotonTargets.AllBuffered, damage);
		}
	}

	//RPC call, take damage.
	[RPC]
	public void damageEffect(int damage) {
		if (GameObject.Find("_SCRIPTS").GetComponent<NetworkManager>().myPlayer == transform.gameObject) {
			//If the player has armor on, damage taken is reduced.
			if (armor)
				damage = (int) ((float) damage / .75f);
			transform.GetComponent<PhotonView>().RPC("takeDamage", PhotonTargets.AllBuffered, damage);
		}
	}
}