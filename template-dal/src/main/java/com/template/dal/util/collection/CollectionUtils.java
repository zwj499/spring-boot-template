package com.template.dal.util.collection;

import com.template.dal.func.Functions;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class CollectionUtils {

    public static <F, T> List<T> map(Collection<F> source, Function<F, T> func) {
        if (isEmpty(source)) {
            return Collections.emptyList();
        }
        List<T> resultList = new ArrayList<>();
        for (F f : source) {
            T t = func.apply(f);
            resultList.add(t);
        }
        return resultList;
    }

    public static <F, T> List<T> selectList(Collection<F> source, Function<F, T> func) {
        return selectList(source, func, true);
    }

    public static <F, T> List<T> selectNotNullList(Collection<F> source, Function<F, T> func) {
        return selectList(source, func, false);
    }


    public static <F, T> List<T> selectList(Collection<F> source, Function<F, T> func, boolean isExistNullValue) {
        if (isEmpty(source)) {
            return Collections.EMPTY_LIST;
        }
        List<T> resultList = new ArrayList<>();
        for (F f : source) {
            T t = func.apply(f);
            if (t != null || isExistNullValue) {
                resultList.add(t);
            }
        }
        return resultList;
    }

    public static <F, T> List<T> selectListByCopy(Collection<F> source, Class<T> clz) {
        return selectList(source, f -> {
            T t = null;
            try {
                t = clz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            BeanUtils.copyProperties(f, t);
            return t;
        });
    }

    public static <T> boolean isEmpty(Collection<T> source) {
        return (source == null || source.size() <= 0);
    }

    public static <E> boolean isEmpty(Map<String, E> map) {
        return map == null || map.size() == 0;
    }


    public static <E> List<E> filter(Collection<E> source, Predicate<E> predicate) {
        if (isEmpty(source) || predicate == null) {
            return Collections.emptyList();
        }
        List<E> result = new ArrayList<>();
        for (E element : source) {
            if (predicate.test(element)) {
                result.add(element);
            }
        }
        return result;
    }


    /**
     * @param left            左集合
     * @param right           右集合(如果后面构造的key相同，后面的元素覆盖前面的，与sql的leftJoin有些区别)
     * @param leftKeyBuilder  左集合元素构造key的方法
     * @param rightKeyBuilder 右集合元素构造key的方法
     * @param resultBuilder   返回集合元素构造的方式
     * @return
     */
    public static <F1, F2, K, R> List<R> leftJoin(Collection<F1> left, Collection<F2> right, Function<F1, K> leftKeyBuilder, Function<F2, K> rightKeyBuilder, BiFunction<F1, F2, R> resultBuilder) {
        if (org.apache.commons.collections.CollectionUtils.isEmpty(left)) {
            return Collections.EMPTY_LIST;
        }
        Map<K, F2> rightMap = toMap(right, rightKeyBuilder);

        List<R> result = new ArrayList<>();
        for (F1 element : left) {
            F2 rightElement = rightMap.get(leftKeyBuilder.apply(element));
            result.add(resultBuilder.apply(element, rightElement));
        }
        return result;
    }


    public static <K, E> Map<K, E> toMap(Collection<E> elements, Function<E, K> keyBuilder) {
        if (isEmpty(elements) || keyBuilder == null) {
            return Collections.EMPTY_MAP;
        }
        Map<K, E> result = new HashMap<>();
        for (E element : elements) {
            result.put(keyBuilder.apply(element), element);
        }
        return result;
    }

    public static <E, K> Map<K, List<E>> toMapList(Collection<E> elements, Function<E, K> keyFunc) {
        return toMapList(elements, keyFunc, Functions.identity());
    }

    public static <E, K, V> Map<K, List<V>> toMapList(Collection<E> elements, Function<E, K> keyFunc, Function<E, V> valueFunc) {
        if (isEmpty(elements) || keyFunc == null || valueFunc == null) {
            return Collections.EMPTY_MAP;
        }
        Map<K, List<V>> result = new HashMap<>();
        for (E element : elements) {
            K key = keyFunc.apply(element);
            List<V> values = result.get(key);
            if (values == null) {
                values = new ArrayList<>();
                result.put(key, values);
            }
            values.add(valueFunc.apply(element));
        }
        return result;
    }


    public static <E, K, R> Map<K, R> groupBy(Collection<E> elements, Function<E, K> keyFunc, BiFunction<K, List<E>, R> resultBuilder) {
        if (isEmpty(elements) || keyFunc == null || resultBuilder == null) {
            return Collections.EMPTY_MAP;
        }
        Map<K, List<E>> map = toMapList(elements, keyFunc, Functions.identity());

        Map<K, R> resultMap = new HashMap<>();
        for (Map.Entry<K, List<E>> entry : map.entrySet()) {
            resultMap.put(entry.getKey(), resultBuilder.apply(entry.getKey(), entry.getValue()));
        }
        return resultMap;
    }


    public static <E> E findFirst(Collection<E> elements, Predicate<E> predicate) {
        if (CollectionUtils.isEmpty(elements)) {
            return null;
        }
        for (E element : elements) {
            if (predicate.test(element)) {
                return element;
            }
        }
        return null;
    }

    public static <E, R> R reduce(Collection<E> elements, R initValue, BiFunction<R, E, R> reduceFunc) {
        if (isEmpty(elements)) {
            return initValue;
        }
        R result = initValue;
        for (E element : elements) {
            result = reduceFunc.apply(result, element);
        }
        return result;
    }

    public static <E> void forEach(Collection<E> collection, Consumer<? super E> action) {
        if (isEmpty(collection) || action == null) {
            return;
        }
        collection.forEach(action);
    }
}