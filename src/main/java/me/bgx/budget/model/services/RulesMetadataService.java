package me.bgx.budget.model.services;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import lombok.Getter;
import me.bgx.budget.model.data.rules.Rule;
import me.bgx.budget.model.generators.Generator;
import me.bgx.budget.model.generators.GeneratorBase;
import me.bgx.budget.util.EditorDescription;
import me.bgx.budget.util.FieldDescription;
import me.bgx.budget.util.IsRule;
import me.bgx.budget.util.RuleField;

@Service
public class RulesMetadataService {

    /**
     * All editors
     */
    @Getter
    private Map<String, EditorDescription> editors;

    private Map<Class<? extends Rule>, Class<? extends GeneratorBase>> generators;

    public RulesMetadataService() {
        initialize();
    }

    private void initialize() {
        editors = new LinkedHashMap<>();
        generators = new HashMap<>();

        for (Class<? extends Rule> clazz : Rule.ALL_RULES) {
            List<FieldDescription> fieldDescriptions = new ArrayList<>();
            Map<String, Collection<String>> fieldsByType = new HashMap<>();

            getAllFields(clazz, fieldDescriptions, fieldsByType);

            IsRule isRule = clazz.getAnnotation(IsRule.class);
            EditorDescription editorDescription = EditorDescription.builder()
                    .label(isRule.label())
                    .ruleType(isRule.type())
                    .clazz(clazz)
                    .fieldsByType(fieldsByType)
                    .fields(fieldDescriptions)
                    .build();
            editors.put(isRule.type(), editorDescription);

            generators.put(clazz, isRule.generator());
        }
        editors = sortLinkedHashMap(editors, new Comparator<Map.Entry<String, EditorDescription>>() {
            @Override
            public int compare(Map.Entry<String, EditorDescription> o1, Map.Entry<String, EditorDescription> o2) {
                return o1.getValue().getLabel().compareTo(o2.getValue().getLabel());
            }
        });
    }

    private void getAllFields(Class<?> clazz, List<FieldDescription> fieldDescriptions, Map<String, Collection<String>> fieldsByType) {
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            getAllFieldsAux(clazz, fieldDescriptions, fieldsByType);
        }
        // Sort fields by order number
        Collections.sort(fieldDescriptions, new Comparator<FieldDescription>() {
            @Override
            public int compare(FieldDescription fd1, FieldDescription fd2) {
                return fd1.getOrder() - fd2.getOrder();
            }
        });
    }

    // don't use this method. use getAllFields. Get all fields of the class clazz.
    private void getAllFieldsAux(Class<?> clazz, List<FieldDescription> fieldDescriptions, Map<String, Collection<String>> fieldsByType) {
        if (clazz == null) {
            return;
        }

        for (Field field : clazz.getDeclaredFields()) {
            RuleField ruleField = field.getAnnotation(RuleField.class);
            if (ruleField == null) {
                continue;
            }

            String fieldType = ruleField.type();
            Collection<String> names = fieldsByType.get(fieldType);
            if (names == null) {
                names = new ArrayList<>();
                fieldsByType.put(fieldType, names);
            }
            names.add(field.getName());

            if (Strings.isNullOrEmpty(fieldType)) {
                fieldType = field.getType().getSimpleName();
            }
            //
            FieldDescription fieldDescription = FieldDescription.builder()
                    .label(ruleField.label())
                    .path(field.getName())
                    .type(fieldType)
                    .order(ruleField.order())
                    .additionalInformation(ruleField.additionalInformation())
                    .build();
            fieldDescriptions.add(fieldDescription);
        }
    }

    private <K, V> Map<K, V> sortLinkedHashMap(Map<K, V> map, Comparator<Map.Entry<K, V>> c) {
        List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());
        Collections.sort(entries, c);
        Map<K, V> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public Rule newRuleFromType(String ruleType) {
        try {
            Rule rule = editors.get(ruleType).getClazz().newInstance();
            rule.setId("new");
            return rule;
        } catch (Exception e) {
            return null;
        }
    }

    public Collection<String> getProperties(String type, String property) {
        EditorDescription editorDescription = editors.get(type);
        if (editorDescription == null) {
            throw new RuntimeException("Editor doesn't exist: " + type);
        }
        Collection<String> properties = editorDescription.getFieldsByType().get(property);
        return (properties == null) ? Collections.<String>emptyList() : properties;
    }

    public Generator getGeneratorFor(Rule rule) {
        Class<? extends GeneratorBase> generatorClass = generators.get(rule.getClass());
        try {
            GeneratorBase<?> generatorBase = generatorClass.newInstance();
            generatorBase.setRule(rule);
            return generatorBase;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<FieldDescription> getFields(String type) {
        return editors.get(type).getFields();
    }

}
