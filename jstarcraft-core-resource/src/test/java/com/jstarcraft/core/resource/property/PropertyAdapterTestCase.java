package com.jstarcraft.core.resource.property;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.resource.Storage;
import com.jstarcraft.core.resource.StorageManager;
import com.jstarcraft.core.resource.annotation.StorageAccessor;
import com.jstarcraft.core.utility.KeyValue;

/**
 * 仓储注解测试
 * 
 * @author Birdy
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Component
public class PropertyAdapterTestCase {

	@Autowired
	private MockSpringObject springObject;
	@Autowired
	private StorageManager storageManager;
	@StorageAccessor
	private Storage<Integer, Person> storage;
	@StorageAccessor("2")
	private Person person;
	@StorageAccessor(value = "2", clazz = Person.class, property = "sex")
	private boolean sex;
	@StorageAccessor(value = "2", clazz = Person.class, property = "description")
	private String description;

	/**
	 * 测试仓储访问器
	 */
	@Test
	public void testAssemblage() {
		// 保证@StorageAccessor注解的接口与类型能被自动装配
		Assert.assertThat(springObject, CoreMatchers.notNullValue());
		Assert.assertThat(storage, CoreMatchers.notNullValue());
		Assert.assertThat(person, CoreMatchers.notNullValue());

		// 检查仓储访问
		Assert.assertThat(storage.getAll().size(), CoreMatchers.equalTo(3));
		Assert.assertThat(storage.getInstance(2, false), CoreMatchers.sameInstance(person));

		// 检查实例访问
		Assert.assertThat(person.isSex(), CoreMatchers.equalTo(sex));
		KeyValue<?, ?> keyValue = new KeyValue<>("key", "value");
		Assert.assertThat(person.getObject(), CoreMatchers.equalTo(keyValue));
		keyValue = new KeyValue<>(1, "1");
		Assert.assertThat(person.getArray()[1], CoreMatchers.equalTo(keyValue));
		Assert.assertThat(person.getMap().get("1"), CoreMatchers.equalTo(keyValue));
		Assert.assertThat(person.getList().get(1), CoreMatchers.equalTo(keyValue));

		// 检查引用访问
		Assert.assertThat(person.getChild(), CoreMatchers.sameInstance(storage.getInstance(2, false)));
		Assert.assertThat(person.getReference(), CoreMatchers.sameInstance(springObject));
		Assert.assertThat(person.getStorage(), CoreMatchers.sameInstance(storage));

		// 检查属性访问
		Assert.assertTrue(sex);
		Assert.assertThat(description, CoreMatchers.notNullValue());
	}

	/**
	 * 测试仓储索引
	 */
	@Test
	public void testIndex() {
		List<Person> ageIndex = storage.getMultiple(Person.INDEX_AGE, 32);
		Assert.assertThat(ageIndex.size(), CoreMatchers.equalTo(2));

		Person birdy = storage.getSingle(Person.INDEX_NAME, "Birdy");
		Assert.assertThat(birdy, CoreMatchers.equalTo(storage.getInstance(1, false)));
	}

}
