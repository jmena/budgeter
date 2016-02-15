package me.bgx.budget.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MapWithDefaultValue<K, V> extends HashMap<K, V> {
    DefaultValueProvider<V> defaultValueProvider;

    public static final DefaultValueProvider<Double> ZERO_DOUBLE_VALUE_PROVIDER = new DefaultValueProvider<Double>() {
        @Override
        public Double get() {
            return 0.0;
        }
    };
    public static final DefaultValueProvider<Integer> ZERO_INT_VALUE_PROVIDER = new DefaultValueProvider<Integer>() {
        @Override
        public Integer get() {
            return 0;
        }
    };
    public static final DefaultValueProvider<List<?>> EMPTY_LIST_VALUE_PROVIDER = new DefaultValueProvider<List<?>>() {
        @Override
        public List<?> get() {
            return new ArrayList();
        }
    };
    public static final DefaultValueProvider<Map<String, Double>> MAP_ZERO_DOUBLE_VALUE_PROVIDER = new DefaultValueProvider<Map<String, Double>>() {
        @Override
        public Map<String, Double> get() {
            return new MapWithDefaultValue<>(ZERO_DOUBLE_VALUE_PROVIDER);
        }
    };

    public interface DefaultValueProvider<V> {
        V get();
    }

    V getDefaultValue() {
        return defaultValueProvider.get();
    }

    @Override
    public V get(Object k) {
        if (!containsKey(k)) {
            put((K) k, getDefaultValue());
        }
        return super.get(k);
    }

}
