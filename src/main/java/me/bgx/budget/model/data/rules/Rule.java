package me.bgx.budget.model.data.rules;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import lombok.Data;
import me.bgx.budget.util.IsRule;
import me.bgx.budget.util.RuleField;

@Data
@Entity
public abstract class Rule {
    public static final Collection<Class<? extends Rule>> ALL_RULES = new ImmutableList.Builder<Class<? extends Rule>>()
            .add(PeriodicAmortizedLoanRule.class)
            .add(PeriodicAmountRule.class)
            .add(SimpleInterestRule.class)
            .add(SingleAmountRule.class)
            .build();

    @Id
    private String id;

    @RuleField(label = "Name", order = 1)
    private String name;

    @RuleField(label = "Tags", type = "tags", order = 2)
    private List<String> tags;

    @RuleField(label = "Notes", type = "textarea", order = 100)
    private String notes;

    public final String getType() {
        return getClass().getAnnotation(IsRule.class).type();
    }

    public final String getLabel() {
        return getClass().getAnnotation(IsRule.class).label();
    }

}
