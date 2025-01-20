package org.sber.proxy;

import org.sber.annotation.Cache;
import org.sber.storage.Storage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CachedInvocationHandler implements InvocationHandler {
    private final Object delegate;
    private final Storage storage;

    public CachedInvocationHandler(Object delegate, Storage storage) {
        this.delegate = delegate;
        this.storage = storage;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Cache cache = method.getAnnotation(Cache.class);
        if (cache == null) {
            return invoke(method, args);
        }
        return getCacheOrInvoke(cache, method, args);
    }

    private Object getCacheOrInvoke(Cache cache, Method method, Object[] args) throws Throwable {
        Object key = key(cache.exclude(), method, args);
        Object value = storage.get(key);

        if (value != null) {
            return value;
        }

        Object result = invoke(method, args);

        if (result instanceof List<?> list) {
            Object limitedList = limitList(cache.limit(), list);
            storage.store(key, limitedList);
        } else {
            storage.store(key, result);
        }

        return result;
    }

    private Object limitList(long limit, List<?> list) {
        if (limit > 0) {
            return list.stream().limit(limit).toList();
        }
        return list;
    }

    private Object invoke(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(delegate, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Impossible");
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private Object key(int[] exclude, Method method, Object[] args) {
        Set<Integer> excludedIndices = Arrays.stream(exclude)
                .boxed()
                .collect(Collectors.toSet());

        List<Object> key = new ArrayList<>();

        String keyValue = method.getName();
        key.add(keyValue);
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (!excludedIndices.contains(i)) {
                    key.add(args[i]);
                }
            }
        }
        return key;
    }
}