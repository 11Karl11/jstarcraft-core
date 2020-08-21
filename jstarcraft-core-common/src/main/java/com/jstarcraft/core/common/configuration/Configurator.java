package com.jstarcraft.core.common.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Iterator;

/**
 * 配置器
 * 
 * @author Birdy
 *
 */
// TODO 考虑改名Option
public interface Configurator {

    BigDecimal getBigDecimal(String key, BigDecimal instead);

    BigDecimal getBigDecimal(String key);

    BigInteger getBigInteger(String key, BigInteger instead);

    BigInteger getBigInteger(String key);

    Boolean getBoolean(String key, Boolean instead);

    Boolean getBoolean(String key);

    Byte getByte(String key, Byte instead);

    Byte getByte(String key);

    Character getCharacter(String key, Character instead);

    Character getCharacter(String key);

    Class getClass(String key, Class instead);

    Class getClass(String key);

    Double getDouble(String key, Double instead);

    Double getDouble(String key);

    <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key, T instead);

    <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key);

    Float getFloat(String key, Float instead);

    Float getFloat(String key);

    Integer getInteger(String key, Integer instead);

    Integer getInteger(String key);

    Long getLong(String key, Long instead);

    Long getLong(String key);

    LocalDateTime getLocalDateTime(String key, LocalDateTime instead);

    LocalDateTime getLocalDateTime(String key);

    <T> T getObject(Class<T> clazz, String key, T instead);

    <T> T getObject(Class<T> clazz, String key);

    String getString(String key, String instead);

    String getString(String key);

    ZonedDateTime getZonedDateTime(String key, ZonedDateTime instead);

    ZonedDateTime getZonedDateTime(String key);

    Iterator<String> getKeys();

}
