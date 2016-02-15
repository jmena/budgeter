package me.bgx.budget.model.generators;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.LocalDate;

import me.bgx.budget.model.data.rules.SingleAmountRule;

public class SingleAmountGenerator extends GeneratorBase<SingleAmountRule> {
    @Override
    public Collection<Amount> generate(LocalDate until) {
        Collection<Amount> amounts = new ArrayList<>();
        if (!loopOutOfLimits(rule.getWhen(), until)) {
            amounts.add(Amount.builder()
                    .description(rule.getName())
                    .date(rule.getWhen())
                    .rule(rule)
                    .amount(rule.getAmount())
                    .build());
        }
        return amounts;
    }
}
