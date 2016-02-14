package me.bgx.budget.model.v1;

import java.util.Collection;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Subclass;

import lombok.Getter;
import lombok.Setter;
import me.bgx.budget.util.IsRule;
import me.bgx.budget.util.RuleField;

@IsRule(type="periodic-amount", label="Periodic amount")
@Subclass
public class PeriodicRule extends Rule {

    @Setter
    @Getter
    @RuleField(label = "From", order = 10)
    @NotNull
    private LocalDate from;

    @Setter
    @Getter
    @RuleField(label = "Number of periods", order = 11)
    private int periods;

    @Setter
    @Getter
    @RuleField(label = "Period", order = 12)
    @NotNull
    private Period period;

    @Setter
    @Getter
    @RuleField(label = "Amount", order = 13)
    private double amount;

    @Override
    public Collection<Amount> generate() {
        ImmutableList.Builder<Amount> amounts = new ImmutableList.Builder<>();
        LocalDate date = from;
        for (int i=0; i < periods; i++) {
            if (i > 10000) {
                throw new RuntimeException("Too many periods");
            }
            Amount singleAmount = Amount.builder()
                    .date(date)
                    .rule(this)
                    .amount(getAmount())
                    .description(getName())
                    .build();
            amounts.add(singleAmount);
            date = date.plus(period);
        }
        return amounts.build();
    }
}
