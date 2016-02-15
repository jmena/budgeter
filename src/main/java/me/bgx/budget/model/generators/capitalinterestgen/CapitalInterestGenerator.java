package me.bgx.budget.model.generators.capitalinterestgen;

import java.util.Collection;

import org.joda.time.LocalDate;

import me.bgx.budget.model.data.rules.Rule;
import me.bgx.budget.model.generators.Amount;

public interface CapitalInterestGenerator {
    Collection<Amount> generate(Rule rule, LocalDate date, double capitalContribution, double interest);
}
