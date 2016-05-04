package com.ttocsneb.qubed.game.spawn.json;

public class SpawnObject {
	
	/**
	 * Default: ""
	 */
	public String object;
	
	/**
	 * Default: 1
	 */
	public float sizeMin = 1;
	/**
	 * Default: 1
	 */
	public float sizeMax = 1;
	
	/**
	 * whether difficulty affects size
	 * Default: true
	 */
	public boolean sizeDiff = true;
	public float sizeDiffScale = 1;
	
	/**
	 * Default: 0
	 */
	public int offsetMin;
	/**
	 * Default: 0
	 */
	public int offsetMax;
	
	/**
	 * Whether difficulty affects offset
	 * Default: false
	 */
	public boolean offsetDiff = false;
	public float offsetDiffScale = 1;
	
	/**
	 * Default: 1
	 */
	public float speedMin = 1;
	/**
	 * Default: 1
	 */
	public float speedMax = 1;
	
	/**
	 * Whether difficulty affects speed
	 * Default: false;
	 */
	public boolean speedDiff = false;
	public float speedDiffScale = 1;
	
	/**
	 * Default: -10
	 */
	public int angleMin = -10;
	/**
	 * Default: 10
	 */
	public int angleMax = 10;
	
	/**
	 * Whether difficulty affects spawn angle
	 * Default false;
	 */
	public boolean angleDiff = false;
	public float angleDiffScale = 1;
	
	/**
	 * Default: 1
	 */
	public int repeatMin = 1;
	/**
	 * Default: 1
	 */
	public int repeatMax = 1;
	
	/**
	 * Default: 1
	 */
	public float delayMin = 1;
	/**
	 * Default: 1
	 */
	public float delayMax = 1;
	
	/**
	 * Whether difficulty affects delay
	 * Default: ttrue;
	 */
	public boolean delayDiff = true;
	public float delayDiffScale = 1;
}
