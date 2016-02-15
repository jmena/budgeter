package me.bgx.budget.util;

import java.util.List;

import com.googlecode.objectify.Ref;

public class OfyUtils {

    public static <T> void preload(List<Ref<T>> lst) {
        for (Ref<?> ref : lst) {
            ref.get();
        }
    }
}
