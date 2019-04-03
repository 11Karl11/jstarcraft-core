package com.jstarcraft.core.orm.hibernate;

import java.time.Instant;
import java.util.LinkedList;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.annotations.Type;

import com.jstarcraft.core.cache.CacheObject;

@Entity
@Table(indexes = { @Index(columnList = "name") })
@NamedQueries({

		@NamedQuery(name = MockObject.QUERY_MONEY_2_ID, query = "select clazz.id, clazz.name from MockObject clazz where clazz.money >= :from and clazz.money < :to"),

		@NamedQuery(name = MockObject.QUERY_NAME_2_ID, query = "select clazz.id, clazz.name from MockObject clazz where clazz.name = ?0"),

		@NamedQuery(name = MockObject.UPDATE_MONEY_BY_ID, query = "update MockObject clazz set clazz.money=:money where clazz.id=:id"),

		@NamedQuery(name = MockObject.DELETE_ALL, query = "delete from MockObject clazz"), })
public class MockObject implements CacheObject<Integer> {

	public static final String QUERY_MONEY_2_ID = "MockObject.query_money_2_id";

	public static final String QUERY_NAME_2_ID = "MockObject.query_name_2_id";

	public static final String UPDATE_MONEY_BY_ID = "MockObject.update_money_by_id";

	public static final String DELETE_ALL = "MockObject.delete_all";

	@Id
	private Integer id;

	private String name;

	private int money;

	private Instant instant;

	@Enumerated(EnumType.STRING)
	private MockEnumeration race;

	@Type(type = "com.jstarcraft.core.orm.hibernate.JsonType")
	private LinkedList<NestObject> children;

	@Version
	private long version;

	public MockObject() {
	}

	@Override
	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMoney() {
		return money;
	}

	public Instant getInstant() {
		return instant;
	}

	public MockEnumeration getRace() {
		return race;
	}

	public LinkedList<NestObject> getChildren() {
		return children;
	}

	public int[] toCurrencies() {
		return new int[] { money };
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (!(object instanceof MockObject))
			return false;
		MockObject that = (MockObject) object;
		EqualsBuilder equal = new EqualsBuilder();
		equal.append(this.getId(), that.getId());
		equal.append(this.getName(), that.getName());
		equal.append(this.getMoney(), that.getMoney());
		equal.append(this.getInstant(), that.getInstant());
		equal.append(this.getRace(), that.getRace());
		equal.append(this.getChildren(), that.getChildren());
		return equal.isEquals();
	}

	public static MockObject instanceOf(Integer id, String name, String childrenName, int money, Instant instant, MockEnumeration race) {
		MockObject instance = new MockObject();
		instance.id = id;
		instance.name = name;
		instance.money = money;
		instance.instant = instant;
		instance.race = race;
		instance.children = new LinkedList<>();
		for (int index = 0; index < money; index++) {
			instance.children.add(NestObject.instanceOf(index, childrenName));
		}
		return instance;
	}

}
