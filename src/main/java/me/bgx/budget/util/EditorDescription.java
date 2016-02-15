package me.bgx.budget.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import me.bgx.budget.model.data.rules.Rule;

@Builder
public class EditorDescription {
    @Getter
    String label;

    @Getter
    String ruleType;

    @Getter
    Class<? extends Rule> clazz;

    @Getter
    Map<String, Collection<String>> fieldsByType;

    @Getter
    Collection<FieldDescription> fields = new ArrayList<>();
}
