package me.bgx.budget.model.generators;

import org.joda.time.LocalDate;

import me.bgx.budget.model.data.rules.Rule;

public abstract class GeneratorBase<T extends Rule> implements Generator {

    protected T rule;

    public void setRule(Rule rule) {
        this.rule = (T) rule;
    }

    protected static final int N_PERIODS_LIMIT = 10000;

    protected boolean loopOutOfLimits(LocalDate date, LocalDate until) {
        return (until != null && date.compareTo(until) > 0);
    }
}
