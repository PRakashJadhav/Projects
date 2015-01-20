using UnityEngine;
using System.Collections;

public class TowerScript : MonoBehaviour {
	TowerManager manager;
	Transform turret;
	Transform laser;
	Transform targetSingle;
	Transform[] targetsAOE;
	Vector3 targetArea;

	long time;
	long cooldownTimer;
	long chargeTimer;
	int currentCooldown;

	public int cost;

	public enum Targeting {
		SingleTarget, SingleTargetByProxy, SingleTargetByHighestHealth, SingleTargetByLowestHealth, AOE, AOEFromTower
	};
	public Targeting targeting;

	public Color color;
	public int behaviourSet;
	public int damage;
	public int cooldown;
	public int cooldownRamp;
	public int minCooldown;
	public float rangeMin;
	public float rangeMax;
	public float rangeAOE;
	public int collection;
	public float reduxDamage;
	public int reduxDamageDuration;
	public float reduxSpeed;
	public int reduxSpeedDuration;
	public AudioSource shotSound;

	// Use this for initialization
	void Start () {
		transform.parent = GameObject.Find("Towers").transform;
		manager = transform.parent.GetComponent<TowerManager>();
		turret = transform.FindChild("Turret").FindChild("Turret1").transform;
		laser = transform.transform.FindChild("Turret").FindChild("Turret1").FindChild("Turret1").FindChild("Laser");
		laser.gameObject.SetActive(false);
		transform.FindChild("Model1").FindChild("Base").renderer.material.color = color;
		transform.FindChild("Turret").FindChild("Turret1").FindChild("Turret1").renderer.material.color = color;
		targetSingle = null;
		targetsAOE = null;
		targetArea = Vector3.zero;
		if (behaviourSet == 3)
			turret.LookAt(turret.position + new Vector3(0, 1, 0));
	}
	
	// Update is called once per frame
	void Update () {
		time = System.DateTime.Now.Ticks;
		switch (targeting) {
			case Targeting.SingleTarget:
				targetSingle = manager.findTarget(transform, rangeMin, rangeMax);
				break;
			case Targeting.SingleTargetByProxy:
				targetSingle = manager.findTargetByProxy(transform, rangeMin, rangeMax);
				break;
			case Targeting.SingleTargetByHighestHealth:
				targetSingle = manager.findTargetByHealth(transform, rangeMin, rangeMax, true);
				break;
			case Targeting.SingleTargetByLowestHealth:
				targetSingle = manager.findTargetByHealth(transform, rangeMin, rangeMax, false);
				break;
			case Targeting.AOE:
				targetArea = manager.findGroupTarget(transform, rangeMin, rangeMax);
				targetsAOE = manager.findAOETargets(targetArea, rangeAOE);
				break;
			case Targeting.AOEFromTower:
				targetsAOE = manager.findAOETargets(transform.position, rangeAOE);
				break;
		}
		switch (behaviourSet) {
			case 1: //Single Target, Single Shot
				if (targetSingle != null) {
					turret.LookAt(targetSingle.position);
					if (time >= cooldownTimer) {
					shotSound.Play ();
						attack(targetSingle);
						cooldownTimer = time + (10000 * cooldown);
						laser.gameObject.SetActive(true);
					}
					else if (time >= cooldownTimer - (10000 * cooldown) + 500000)
						laser.gameObject.SetActive(false);
				}
				else if (time >= cooldownTimer - (10000 * cooldown) + 500000) {
					turret.LookAt(turret.position + new Vector3(0, 0, -1));
					laser.gameObject.SetActive(false);
				}
				break;
			case 2: //AOE
				if (targetsAOE.Length > 0) {
					turret.LookAt(targetArea);
					if (time >= cooldownTimer) {
						for (int i = 0; i < targetsAOE.Length; i++) {
							if (targetsAOE[i] != null) {
							shotSound.Play ();
								attack(targetsAOE[i]);
							}
						}
						cooldownTimer = time + (10000 * cooldown);
						laser.gameObject.SetActive(true);
					}
					else if (time >= cooldownTimer - (10000 * cooldown) + 500000)
						laser.gameObject.SetActive(false);
				}
				else if (time >= cooldownTimer - (10000 * cooldown) + 500000) {
					turret.LookAt(turret.position + new Vector3(0, 0, -1));
					laser.gameObject.SetActive(false);
				}
				break;
			case 3: //AOEFromTower
				if (System.DateTime.Now.Ticks >= cooldownTimer) {
					for (int i = 0; i < targetsAOE.Length; i++) {
						if (targetsAOE[i] != null) {
						shotSound.Play ();
							attack(targetsAOE[i]);
						}
					}
					cooldownTimer = time + (10000 * cooldown);
					laser.gameObject.SetActive(true);
				}
				else if (time >= cooldownTimer - (10000 * cooldown) + 500000) {
					laser.gameObject.SetActive(false);
				}
				break;
			case 4: //Single Target, Single Shot, Ramped Speed
				if (targetSingle != null) {
					turret.LookAt(targetSingle.position);
					if (time >= cooldownTimer) {
					shotSound.Play ();
						attack(targetSingle);
						currentCooldown -= cooldownRamp;
						if (currentCooldown < minCooldown)
							currentCooldown = minCooldown;
						cooldownTimer = time + (10000 * currentCooldown);
						laser.gameObject.SetActive(true);
					}
					else if (time >= cooldownTimer - (10000 * currentCooldown) + 500000)
						laser.gameObject.SetActive(false);
				}
				else {
					currentCooldown = cooldown;
					if (time >= cooldownTimer - (10000 * currentCooldown) + 500000) {
						turret.LookAt(turret.position + new Vector3(0, 0, -1));
						laser.gameObject.SetActive(false);
					}
				}
				break;
			case 5: //Charge Shot
				if (targetsAOE.Length > 0) {
					if (time >= cooldownTimer) {
						turret.LookAt(targetArea);
						//Start charge-up if not started
						if (chargeTimer == 0) {
							chargeTimer = time + (10000 * 2000);
						}
						if (time >= chargeTimer) {
							int count = 0;
							for (int i = 0; i < targetsAOE.Length; i++) {
								if (targetsAOE[i] != null) {
									count++;
								}
							}
							int indivdamage = damage / count;
							for (int i = 0; i < targetsAOE.Length; i++) {
								if (targetsAOE[i] != null) {
								shotSound.Play ();
									attackForDamage(targetsAOE[i], indivdamage);
								}
							}
							cooldownTimer = time + (10000 * cooldown);
							chargeTimer = 0;
							laser.gameObject.SetActive(true);
						}
					}
					else if (time >= cooldownTimer - (10000 * cooldown) + 500000)
						laser.gameObject.SetActive(false);
				}
				else if (time >= cooldownTimer - (10000 * cooldown) + 500000) {
					turret.LookAt(turret.position + new Vector3(0, 0, -1));
					laser.gameObject.SetActive(false);
					chargeTimer = 0;
				}
				break;
		}
	}
	
	public void attack(Transform target) {
		//Apply damage
		target.GetComponent<EnemyScript>().takeDamage(damage, collection);
		//Apply damage reduction if applicable
		if (reduxDamage != 0) {
			target.GetComponent<EnemyScript>().reduceDamage(reduxDamage, reduxDamageDuration);
		}
		//Apply movement speed reduction if applicable
		if (reduxSpeed != 0) {
			target.GetComponent<EnemyScript>().reduceSpeed(reduxSpeed, reduxSpeedDuration);
		}
	}

	public void attackForDamage(Transform target, int d) {
		target.GetComponent<EnemyScript>().takeDamage(d, collection);
	}
}
