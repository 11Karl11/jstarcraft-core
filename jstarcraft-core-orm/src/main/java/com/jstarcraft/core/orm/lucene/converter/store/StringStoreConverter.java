package com.jstarcraft.core.orm.lucene.converter.store;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.orm.lucene.annotation.SearchStore;
import com.jstarcraft.core.orm.lucene.converter.LuceneContext;
import com.jstarcraft.core.orm.lucene.converter.StoreConverter;

/**
 * 字符串存储转换器
 * 
 * @author Birdy
 *
 */
public class StringStoreConverter implements StoreConverter {

    @Override
    public Object decode(LuceneContext context, String path, Field field, SearchStore annotation, Type type, NavigableMap<String, IndexableField> indexables) {
        String from = path;
        char character = path.charAt(path.length() - 1);
        character++;
        String to = path.substring(0, path.length() - 1) + character;
        indexables = indexables.subMap(from, true, to, false);
        IndexableField indexable = indexables.firstEntry().getValue();
        return indexable.stringValue();
    }

    @Override
    public NavigableMap<String, IndexableField> encode(LuceneContext context, String path, Field field, SearchStore annotation, Type type, Object instance) {
        NavigableMap<String, IndexableField> indexables = new TreeMap<>();
        indexables.put(path, new StoredField(path, instance.toString()));
        return indexables;
    }

}
