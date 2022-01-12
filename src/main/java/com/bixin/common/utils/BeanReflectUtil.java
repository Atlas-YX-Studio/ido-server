package com.bixin.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author zhangcheng
 * create  2022/1/12
 */
public class BeanReflectUtil {

    public static void setFieldValue(final Object object,
                                     final String fieldName,
                                     final Object value) {
        Field field = getDeclaredField(object, fieldName);
        if (field == null)
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        makeAccessible(field);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            System.out.println(e);
        }
    }

    protected static Field getDeclaredField(final Object object,
                                            final String fieldName) {
        return getDeclaredField(object.getClass(), fieldName);
    }

    protected static Field getDeclaredField(final Class clazz, final String fieldName) {
        for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                System.out.println(e);
            }
        }
        return null;
    }

    protected static void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers())
                || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            field.setAccessible(true);
        }
    }

}
