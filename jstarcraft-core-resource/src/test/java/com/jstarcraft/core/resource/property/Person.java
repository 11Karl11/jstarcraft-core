package com.jstarcraft.core.resource.property;

import java.util.ArrayList;
import java.util.HashMap;

import com.jstarcraft.core.resource.Storage;
import com.jstarcraft.core.resource.annotation.StorageConfiguration;
import com.jstarcraft.core.resource.annotation.StorageId;
import com.jstarcraft.core.resource.annotation.StorageIndex;
import com.jstarcraft.core.resource.annotation.StorageReference;
import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.core.utility.StringUtility;

@StorageConfiguration
public class Person {

	public static final String INDEX_NAME = "name";
	public static final String INDEX_AGE = "age";

	@StorageId
	private Integer id;

	@StorageIndex(name = INDEX_NAME, unique = true)
	private String name;

	@StorageIndex(name = INDEX_AGE, unique = false)
	private int age;

	private boolean sex;

	private KeyValue<String, String> object;

	private KeyValue<Integer, String>[] array;

	private HashMap<String, KeyValue<Integer, String>> map;

	private ArrayList<KeyValue<Integer, String>> list;

	private Integer childId;

	@StorageReference(expression = "instance.getChildId()")
	private transient Person child;

	@StorageReference
	private transient MockSpringObject reference;

	@StorageReference
	private transient Storage<Integer, Person> storage;

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public boolean isSex() {
		return sex;
	}

	public KeyValue<String, String> getObject() {
		return object;
	}

	public KeyValue<Integer, String>[] getArray() {
		return array;
	}

	public HashMap<String, KeyValue<Integer, String>> getMap() {
		return map;
	}

	public ArrayList<KeyValue<Integer, String>> getList() {
		return list;
	}

	public Integer getChildId() {
		return childId;
	}

	public Person getChild() {
		return child;
	}

	public MockSpringObject getReference() {
		return reference;
	}

	public Storage<Integer, Person> getStorage() {
		return storage;
	}

	public String getDescription() {
		return StringUtility.reflect(this);
	}

}
