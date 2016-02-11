package me.bgx.budget.model.v1;

import java.util.Collection;

import org.joda.time.LocalDate;

import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Subclass;

import lombok.Getter;
import lombok.Setter;
import me.bgx.budget.util.IsRule;
import me.bgx.budget.util.RuleField;

@IsRule(type="single-amount", label="Single amount")
@Subclass
public class SingleAmountRule extends Rule {
    @Setter
    @Getter
    @RuleField(label = "When", order = 10)
    private LocalDate when;

    @Setter
    @Getter
    @RuleField(label="Amount", order = 11)
    private double amount;

    @Override
    public Collection<Amount> generate() {
        ImmutableList.Builder<Amount> amounts = new ImmutableList.Builder<>();
        amounts.add(
                Amount.builder()
                        .description(getName())
                        .date(when)
                        .rule(this)
                        .amount(amount)
                        .build());
        return amounts.build();
    }
}
