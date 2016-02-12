package me.bgx.budget.model.v1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import lombok.Getter;
import lombok.Setter;
import me.bgx.budget.util.EditorDescription;
import me.bgx.budget.util.FieldDescription;
import me.bgx.budget.util.IsRule;
import me.bgx.budget.util.RuleField;

@Entity
public abstract class Rule {

    public static final Collection<Class<? extends Rule>> ALL_RULES = new ImmutableList.Builder<Class<? extends Rule>>()
            .add(MonthlyAmortizedLoad.class)
            .add(PeriodicRule.class)
            .add(SimpleInterestRule.class)
            .add(SingleAmountRule.class)
            .build();

    /**
     * All editors
     */
    public static final Map<String, EditorDescription> EDITORS;

    static {
        Map<String, EditorDescription> editors = new HashMap<>();
        for (Class<? extends Rule> clazz : ALL_RULES) {
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
        }
        editors = sortLinkedHashMap(editors, new Comparator<Map.Entry<String, EditorDescription>>() {
            @Override
            public int compare(Map.Entry<String, EditorDescription> o1, Map.Entry<String, EditorDescription> o2) {
                return o1.getValue().getLabel().compareTo(o2.getValue().getLabel());
            }
        });
        EDITORS = Collections.unmodifiableMap(editors);
    }

    private static void getAllFields(Class<?> clazz, List<FieldDescription> fieldDescriptions, Map<String, Collection<String>> fieldsByType) {
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

    // use getAllFields
    private static void getAllFieldsAux(Class<?> clazz, List<FieldDescription> fieldDescriptions, Map<String, Collection<String>> fieldsByType) {
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
                    .build();
            fieldDescriptions.add(fieldDescription);
        }
    }

    private static <K,V> Map<K,V> sortLinkedHashMap(Map<K,V> map, Comparator<Map.Entry<K,V>> c) {
        List<Map.Entry<K,V>> entries = new ArrayList<>(map.entrySet());
        Collections.sort(entries, c);
        Map<K,V> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    @Setter
    @Getter
    @Index
    private String userId;

    @Id
    @Setter
    @Getter
    private String id;

    @Setter
    @Getter
    @RuleField(label = "Name", order = 1)
    private String name;

    @Setter
    @Getter
    @RuleField(label = "Tags", type = "tags", order = 2)
    private List<String> tags;

    @Setter
    @Getter
    @RuleField(label = "Notes", type = "textarea", order = 100)
    private String notes;

    public final String getType() {
        return getClass().getAnnotation(IsRule.class).type();
    }

    public final String getLabel() {
        return getClass().getAnnotation(IsRule.class).label();
    }

    public abstract Collection<Amount> generate();

    public static Rule newInstanceFromType(String ruleType) {
        try {
            Rule rule = EDITORS.get(ruleType).getClazz().newInstance();
            rule.setId("new");
            return rule;
        } catch (Exception e) {
            return null;
        }
    }

    protected void generatePaymentInterestAmount(boolean detailed, LocalDate date, double capitalContribution, double interest, Collection<Amount> amounts) {
        if (detailed) {
            amounts.add(Amount.builder()
                    .date(date)
                    .rule(this)
                    .description(name + " - capital contribution")
                    .amount(capitalContribution)
                    .build());

            amounts.add(Amount.builder()
                    .date(date)
                    .rule(this)
                    .description(name + " - interest")
                    .amount(interest)
                    .build());
        } else {
            amounts.add(Amount.builder()
                    .date(date)
                    .rule(this)
                    .description(name)
                    .amount(capitalContribution + interest)
                    .build());
        }
    }
}
