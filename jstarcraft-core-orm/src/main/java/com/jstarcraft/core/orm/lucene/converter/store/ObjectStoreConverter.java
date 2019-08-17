package com.jstarcraft.core.orm.lucene.converter.store;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.orm.exception.OrmException;
import com.jstarcraft.core.orm.lucene.annotation.LuceneStore;
import com.jstarcraft.core.orm.lucene.converter.LuceneContext;
import com.jstarcraft.core.orm.lucene.converter.StoreConverter;
import com.jstarcraft.core.utility.KeyValue;

/**
 * 对象存储转换器
 * 
 * @author Birdy
 *
 */
public class ObjectStoreConverter implements StoreConverter {

    @Override
    public Object decode(LuceneContext context, String path, Field field, LuceneStore annotation, Type type, NavigableMap<String, IndexableField> indexables) {
        String from = path;
        char character = path.charAt(path.length() - 1);
        character++;
        String to = path.substring(0, path.length() - 1) + character;
        indexables = indexables.subMap(from, true, to, false);
        Class<?> clazz = TypeUtility.getRawType(type, null);

        try {
            // TODO 此处需要代码重构
            Object instance = context.getInstance(clazz);
            for (KeyValue<Field, StoreConverter> keyValue : context.getStoreKeyValues(clazz)) {
                // TODO 此处代码可以优反射次数.
                field = keyValue.getKey();
                StoreConverter converter = keyValue.getValue();
                annotation = field.getAnnotation(LuceneStore.class);
                String name = field.getName();
                type = field.getGenericType();
                Object data = converter.decode(context, path + "." + name, field, annotation, type, indexables);
                field.set(instance, data);
            }
            return instance;
        } catch (Exception exception) {
            // TODO
            throw new OrmException(exception);
        }
    }

    @Override
    public NavigableMap<String, IndexableField> encode(LuceneContext context, String path, Field field, LuceneStore annotation, Type type, Object instance) {
        NavigableMap<String, IndexableField> indexables = new TreeMap<>();
        Class<?> clazz = TypeUtility.getRawType(type, null);

        try {
            // TODO 此处需要代码重构
            for (KeyValue<Field, StoreConverter> keyValue : context.getStoreKeyValues(clazz)) {
                // TODO 此处代码可以优反射次数.
                field = keyValue.getKey();
                StoreConverter converter = keyValue.getValue();
                annotation = field.getAnnotation(LuceneStore.class);
                String name = field.getName();
                type = field.getGenericType();
                Object data = field.get(instance);
                for (IndexableField indexable : converter.encode(context, path + "." + name, field, annotation, type, data).values()) {
                    indexables.put(path + "." + name, indexable);
                }
            }
            return indexables;
        } catch (Exception exception) {
            // TODO
            throw new OrmException(exception);
        }
    }

}
