package com.frameworkium.core.api.dto;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.*;

import java.io.Serializable;

public abstract class AbstractDTO<T> implements Serializable, Cloneable {

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T clone() throws CloneNotSupportedException {
        try {
            return (T) SerializationUtils.clone(this);
        } catch (Exception e) {
            throw new CloneNotSupportedException(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(
                this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
