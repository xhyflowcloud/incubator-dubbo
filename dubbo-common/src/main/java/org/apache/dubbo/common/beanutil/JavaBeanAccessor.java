package org.apache.dubbo.common.beanutil;

public enum JavaBeanAccessor {
    /**
     * Field accessor
     */
    FIELD,
    /**
     * Method accessor
     */
    Method,
    /**
     * Method prefer to field
     */
    ALL;

    public static boolean isAccessByMethod(JavaBeanAccessor accessor){
        return Method.equals(accessor) || ALL.equals(accessor);
    }

    public static boolean isAccessByField(JavaBeanAccessor accessor){
        return FIELD.equals(accessor) || ALL.equals(accessor);
    }
}
