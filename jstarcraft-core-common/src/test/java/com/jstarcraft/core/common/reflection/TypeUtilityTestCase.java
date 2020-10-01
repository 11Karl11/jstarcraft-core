package com.jstarcraft.core.common.reflection;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.utility.KeyValue;

import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArraySet;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

public class TypeUtilityTestCase {

    @Test
    public void testRefineType() {
        {
            Type type = TypeUtility.parameterize(HashMap.class, String.class, String.class);
            Assert.assertEquals(TypeUtility.parameterize(Map.class, String.class, String.class), TypeUtility.refineType(type, Map.class));
        }
        {
            Type type = TypeUtility.parameterize(Byte2ObjectOpenHashMap.class, String.class);
            Assert.assertEquals(TypeUtility.parameterize(Map.class, Byte.class, String.class), TypeUtility.refineType(type, Map.class));
        }
        {
            Type type = TypeUtility.parameterize(Object2ByteOpenHashMap.class, String.class);
            Assert.assertEquals(TypeUtility.parameterize(Map.class, String.class, Byte.class), TypeUtility.refineType(type, Map.class));
        }
        {
            Type type = TypeUtility.refineType(Byte2BooleanOpenHashMap.class, Map.class);
            Assert.assertEquals(TypeUtility.parameterize(Map.class, Byte.class, Boolean.class), TypeUtility.refineType(type, Map.class));
        }
        {
            Type type = TypeUtility.refineType(ByteArrayList.class, List.class);
            Assert.assertEquals(TypeUtility.parameterize(List.class, Byte.class), TypeUtility.refineType(type, List.class));
        }
        {
            Type type = TypeUtility.refineType(ByteArraySet.class, Set.class);
            Assert.assertEquals(TypeUtility.parameterize(Set.class, Byte.class), TypeUtility.refineType(type, Set.class));
        }
    }

    @Test
    public void testTypeVariable() {
        TypeVariable<?>[] typeVariables = KeyValue.class.getTypeParameters();
        TypeVariable<?> keyVariable = TypeUtility.typeVariable(KeyValue.class, "K");
        Assert.assertEquals(typeVariables[0].getGenericDeclaration(), keyVariable.getGenericDeclaration());
        Assert.assertEquals(typeVariables[0].getTypeName(), keyVariable.getTypeName());
        TypeVariable<?> valueVariable = TypeUtility.typeVariable(KeyValue.class, "V");
        Assert.assertEquals(typeVariables[1].getGenericDeclaration(), valueVariable.getGenericDeclaration());
        Assert.assertEquals(typeVariables[1].getTypeName(), valueVariable.getTypeName());
    }

}
