package com.jstarcraft.core.distribution.lock;

import java.time.Instant;

/**
 * 分布式定义
 * 
 * @author Birdy
 *
 */
public class DistributionDefinition {

	/** 锁名称 */
	private String name;

	/** 最多锁定到指定的时间(必选) */
	private Instant most;

	/** 至少锁定到指定的时间(可选) */
	private Instant least;

	DistributionDefinition() {
	}

	public DistributionDefinition(String name, Instant most) {
		this(name, most, Instant.now());
	}

	public DistributionDefinition(String name, Instant most, Instant least) {
		this.name = name;
		this.most = most;
		this.least = least;
	}

	public String getName() {
		return name;
	}

	public Instant getMost() {
		return most;
	}

	public Instant getLeast() {
		return least;
	}

}
