package com.template.dal.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class ClassUtils {

    public static Field[] getAllFields(Class objClass) {
        if(objClass == null){
            return new Field[0];
        }
        List<Field> fields = new ArrayList<>();
        while (!objClass.isAssignableFrom(Object.class)){
            fields.addAll(Arrays.asList(objClass.getDeclaredFields()));
            objClass = objClass.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    public static void fillFieldValue(Object obj, Field field, Object value) {
        try {
            Method method = obj.getClass().getMethod(String.format("set%s", StringUtils.captureName(field.getName())), field.getType());
            method.invoke(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getValue(Object obj, String fieldName) {
        if(obj == null || fieldName == null){
            return null;
        }
        try {
            Class clazz = obj.getClass();
            PropertyDescriptor pd = new PropertyDescriptor(fieldName, clazz);
            return pd == null ? null : pd.getReadMethod().invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getFieldValue(Field field, Object obj) {
        return field == null ? null : getValue(obj,field.getName());
    }

    public static Field[] getAllFieldsWithFilter(Class objClass,Class filter) {
        Field[] fields = getAllFields(objClass);
        List<Field> list = Arrays.asList(fields).stream().filter(field ->
                filter.equals(field.getType())).collect(toList());
        return list.toArray(new Field[0]);
    }

    public static void tranMatchField(Object object, Function func, Class filter) {
        Field[] fields = ClassUtils.getAllFieldsWithFilter(object.getClass(), filter);
        Arrays.asList(fields).forEach(field -> {
            field.setAccessible(true);
            ClassUtils.fillFieldValue(object, field, func.apply(ClassUtils.getFieldValue(field, object)));
        });
    }
}
