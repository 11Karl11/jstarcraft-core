package com.jstarcraft.core.orm.berkeley.migration.older;

import com.jstarcraft.core.cache.CacheObject;
import com.jstarcraft.core.orm.berkeley.annotation.BerkeleyConfiguration;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
@BerkeleyConfiguration(store = "migration")
public class Player implements CacheObject<Long> {

	@PrimaryKey(sequence = "Player_ID")
	private long id;

	/**
	 * 旧库Player.name没有次级索引,新库Player.name有次级索引
	 */
	private String name;

	/**
	 * 旧库Player.sex类型为Boolean,新库Player.sex类型为String
	 */
	private boolean sex;

	/**
	 * 旧库Player.age不存在,新库Player.age存在
	 */

	public Player() {
	}

	public Player(String name) {
		this.name = name;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Object getSex() {
		return sex;
	}

}
