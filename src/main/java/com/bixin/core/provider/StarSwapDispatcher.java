package com.bixin.core.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangcheng
 * create  2021-08-24 5:44 下午
 */
@Slf4j
@Component
public class StarSwapDispatcher {

    @Resource
    private IStarSwapProvider<?>[] providers;

    private final Map<String, IStarSwapProvider<?>> providerMap = new HashMap<>();

    @PostConstruct
    public void initProvider() {
        for (IStarSwapProvider<?> provider : providers) {
            Type[] types = provider.getClass().getGenericInterfaces();
            Class<?>[] typeArguments = Arrays.stream(types)
                    .filter(type -> {
                        if (type instanceof ParameterizedType) {
                            ParameterizedType pt = (ParameterizedType) type;
                            if (pt.getRawType().equals(IStarSwapProvider.class)) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .map(type -> {
                        ParameterizedType pt = (ParameterizedType) type;
                        Class<?> clazz = (Class<?>) pt.getActualTypeArguments()[0];
                        return clazz;
                    })
                    .toArray(Class<?>[]::new);

            if (typeArguments.length == 1) {
                Class<?> clazz = typeArguments[0];
                log.info("init provider set clazz {} by provider {}.", clazz.getName(), provider.getClass().getName());
                if (this.providerMap.put(clazz.getName(), provider) != null) {
                    throw new IllegalArgumentException("Duplicate provider found " + clazz.getName());
                }
            } else {
                throw new IllegalArgumentException("Dispatcher class " + provider.getClass().getName()
                        + " does not implement as a parameterized type.");
            }

        }

    }


    public <T> void dispatch(T entity) {
        @SuppressWarnings("unchecked")
        IStarSwapProvider<T> provider = (IStarSwapProvider<T>) providerMap.get(entity.getClass().getName());
        provider.dispatcher(entity);
    }


}
