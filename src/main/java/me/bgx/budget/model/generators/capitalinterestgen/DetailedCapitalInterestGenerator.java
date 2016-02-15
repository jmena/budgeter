package me.bgx.budget.model.generators.capitalinterestgen;

import java.util.Collection;

import org.joda.time.LocalDate;

import com.google.common.collect.ImmutableList;

import me.bgx.budget.model.data.rules.Rule;
import me.bgx.budget.model.generators.Amount;

public class DetailedCapitalInterestGenerator implements CapitalInterestGenerator {

    public Collection<Amount> generate(Rule rule, LocalDate date, double capitalContribution, double interest) {
        return new ImmutableList.Builder<Amount>()
                .add(Amount.builder()
                        .date(date)
                        .rule(rule)
                        .description(rule.getName() + " - capital contribution")
                        .amount(capitalContribution)
                        .build())
                .add(Amount.builder()
                        .date(date)
                        .rule(rule)
                        .description(rule.getName() + " - interest")
                        .amount(interest)
                        .build())
                .build();
    }
}
