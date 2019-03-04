package com.smartdigit.dev.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {

    public static Set flattenToSet(Collection source) {
        Set result = new HashSet();
        for (Object obj: source) {
            if (obj instanceof Collection) {
                result.addAll(flattenToSet((Collection) obj));
            } else {
                result.add(obj);
            }
        }
        return result;
    }
}
