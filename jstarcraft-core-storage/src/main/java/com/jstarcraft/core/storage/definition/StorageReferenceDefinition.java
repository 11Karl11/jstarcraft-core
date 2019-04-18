package com.jstarcraft.core.storage.definition;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Observable;

import com.jstarcraft.core.common.conversion.csv.ConversionUtility;
import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;
import com.jstarcraft.core.script.ScriptScope;
import com.jstarcraft.core.storage.Storage;
import com.jstarcraft.core.storage.StorageManager;
import com.jstarcraft.core.storage.annotation.StorageId;
import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.utility.ReflectionUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 仓储引用定义
 * 
 * @author Birdy
 */
public class StorageReferenceDefinition extends ReferenceDefinition {

	private final Field attribute;

	private final StorageManager manager;

	public StorageReferenceDefinition(Field field, StorageManager manager) {
		super(field);
		Class<?> clazz;
		// TODO 不能有擦拭和通配类型
		if (Storage.class.isAssignableFrom(field.getType())) {
			Type type = field.getGenericType();
			if (!(type instanceof ParameterizedType)) {
				String message = StringUtility.format("字段[{}]的类型非法,无法装配", field);
				throw new StorageException(message);
			}

			Type[] types = ((ParameterizedType) type).getActualTypeArguments();
			if (types[1] instanceof Class) {
				clazz = (Class<?>) types[1];
			} else if (types[1] instanceof ParameterizedType) {
				clazz = (Class<?>) ((ParameterizedType) types[1]).getRawType();
			} else {
				String message = StringUtility.format("字段[{}]的类型非法,无法装配", field);
				throw new StorageException(message);
			}
		} else {
			clazz = field.getType();
		}

		this.attribute = ReflectionUtility.uniqueField(clazz, StorageId.class);
		this.manager = manager;
	}

	@Override
	public void setReference(Object instance) {
		Object value;
		if (StringUtility.isBlank(reference.expression())) {
			value = manager.getStorage(attribute.getDeclaringClass());
		} else {
			Storage storage = manager.getStorage(attribute.getDeclaringClass());
			ScriptContext context = new ScriptContext();
			context.useClasses();
			ScriptScope scope = new ScriptScope();
			scope.createAttribute("instance", instance);
			ScriptExpression expression = ReflectionUtility.getInstance(reference.type(), context, scope, reference.expression());
			value = storage.getInstance(ConversionUtility.convert(expression.doWith(String.class), attribute.getGenericType()), false);
		}
		if (value == null && reference.necessary()) {
			throw new StorageException("引用定义对象不能为null");
		}
		try {
			field.set(instance, value);
		} catch (Exception exception) {
			throw new StorageException("引用定义设置对象异常", exception);
		}
	}

	@Override
	public Class getMonitorStorage() {
		return attribute.getDeclaringClass();
	}

	/** 更新通知 */
	@Override
	public void update(Observable object, Object argument) {
		Storage storage = manager.getStorage(attribute.getDeclaringClass());
		for (Object instance : storage.getAll()) {
			setReference(instance);
		}
	}

}
