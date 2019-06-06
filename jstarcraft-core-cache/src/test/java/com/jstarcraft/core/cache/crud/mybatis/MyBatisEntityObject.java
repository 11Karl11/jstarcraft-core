package com.jstarcraft.core.cache.crud.mybatis;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jstarcraft.core.cache.annotation.CacheChange;
import com.jstarcraft.core.cache.annotation.CacheConfiguration;
import com.jstarcraft.core.cache.annotation.CacheConfiguration.Unit;
import com.jstarcraft.core.common.identification.IdentityObject;

@Entity
//@TableName("MyBatisEntityObject")
@CacheConfiguration(unit = Unit.ENTITY, indexes = { "firstName", "token" }, transienceStrategy = "lruMemoryStrategy", persistenceStrategy = "promptPersistenceStrategy")
public class MyBatisEntityObject implements IdentityObject<Integer> {

    @Id
    @TableId
    private Integer id;

    private String firstName;

    private String lastName;

    private int money;

    private int token;

    MyBatisEntityObject() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getMoney() {
        return money;
    }

    public int getToken() {
        return token;
    }

    public String[] toNames() {
        return new String[] { firstName, lastName };
    }

    public int[] toCurrencies() {
        return new int[] { money, token };
    }

    @CacheChange(values = { "true" })
    public boolean modify(String lastName, int money, boolean modify) {
        this.lastName = lastName;
        this.money = money;
        return modify;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof MyBatisEntityObject))
            return false;
        MyBatisEntityObject that = (MyBatisEntityObject) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.getFirstName(), that.getFirstName());
        equal.append(this.getLastName(), that.getLastName());
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(getFirstName());
        hash.append(getLastName());
        return hash.toHashCode();
    }

    public static MyBatisEntityObject instanceOf(Integer id, String firstName, String lastName, int money, int token) {
        MyBatisEntityObject instance = new MyBatisEntityObject();
        instance.id = id;
        instance.firstName = firstName;
        instance.lastName = lastName;
        instance.money = money;
        instance.token = token;
        return instance;
    }

}
