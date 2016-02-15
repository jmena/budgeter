package me.bgx.budget.model.generators;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.LocalDate;

import me.bgx.budget.model.data.rules.PeriodicAmortizedLoanRule;
import me.bgx.budget.model.generators.capitalinterestgen.CapitalInterestGenerator;

public class PeriodicAmortizedLoanGenerator extends GeneratorBase<PeriodicAmortizedLoanRule> {

    @Override
    public Collection<Amount> generate(LocalDate until) {

        // https://en.wikipedia.org/wiki/Mortgage_calculator
        // rP / (1 - (1 + r)^-N)
        double balance = -rule.getOwned();
        final double fixedPayment;
        if (rule.getRate() == 0.0) {
            fixedPayment = -rule.getOwned() / rule.getPeriods();
        } else {
            fixedPayment = -(rule.getRate() * rule.getOwned()) / (1.0 - Math.pow(1.0 + rule.getRate(), -rule.getPeriods()));
        }

        CapitalInterestGenerator capitalInterestGenerator = GeneratorUtil.newCapitalInterestGenerator(rule.isDetailed());
        LocalDate date = rule.getFrom();
        Collection<Amount> amounts = new ArrayList<>();

        int max = (rule.getPeriods() > 0) ? Math.min(rule.getPeriods(), N_PERIODS_LIMIT) : N_PERIODS_LIMIT;
        for (int i = 0; i < max; i++) {
            if (loopOutOfLimits(date, until)) {
                break;
            }
            double interest = balance * rule.getRate();
            double capitalContribution = fixedPayment - interest;

            amounts.addAll(capitalInterestGenerator.generate(rule, date, capitalContribution, interest));

            date = date.plus(rule.getPeriod());
            balance = balance + interest - fixedPayment;
        }
        return amounts;
    }
}
