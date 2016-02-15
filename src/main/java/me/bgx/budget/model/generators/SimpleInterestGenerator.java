package me.bgx.budget.model.generators;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.LocalDate;

import me.bgx.budget.model.data.rules.SimpleInterestRule;
import me.bgx.budget.model.generators.capitalinterestgen.CapitalInterestGenerator;

public class SimpleInterestGenerator extends GeneratorBase<SimpleInterestRule> {

    @Override
    public Collection<Amount> generate(LocalDate until) {
        Collection<Amount> amounts = new ArrayList<>();

        double payment = -rule.getOwned() / rule.getPeriods();
        double balance = -rule.getOwned();

        CapitalInterestGenerator capitalInterestGenerator = GeneratorUtil.newCapitalInterestGenerator(rule.isDetailed());
        LocalDate date = rule.getFrom();
        int max = (rule.getPeriods() > 0) ? Math.min(rule.getPeriods(), N_PERIODS_LIMIT) : N_PERIODS_LIMIT;

        for (int i = 0; i < max; i++) {
            if (loopOutOfLimits(date, until)) {
                break;
            }
            double interest = balance * rule.getRate();
            amounts.addAll(capitalInterestGenerator.generate(rule, date, payment, interest));
            date = date.plus(rule.getPeriod());
            balance = balance - payment;
        }
        return amounts;
    }
}
