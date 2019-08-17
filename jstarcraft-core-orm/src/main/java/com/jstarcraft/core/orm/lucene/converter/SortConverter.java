package com.jstarcraft.core.orm.lucene.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.apache.lucene.index.IndexableField;

import com.jstarcraft.core.orm.lucene.annotation.LuceneSort;

/**
 * 排序转换器
 * 
 * @author Birdy
 *
 */
public interface SortConverter {

    /**
     * 转换排序
     * 
     * @param context
     * @param path
     * @param annotation
     * @param field
     * @param data
     * @return
     */
    Iterable<IndexableField> convert(LuceneContext context, String path, Field field, LuceneSort annotation, Type type, Object data);

}
