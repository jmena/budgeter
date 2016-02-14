package me.bgx.budget.model.v1;

import java.util.ArrayList;
import java.util.Collection;
import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import com.googlecode.objectify.annotation.Subclass;

import lombok.Getter;
import lombok.Setter;
import me.bgx.budget.util.IsRule;
import me.bgx.budget.util.RuleField;

@IsRule(type = "periodic-amount", label = "Periodic amount")
@Subclass
public class PeriodicRule extends Rule {

    @Setter
    @Getter
    @RuleField(label = "Start", order = 11)
    @NotNull
    private LocalDate from;

    @Setter
    @Getter
    @RuleField(label = "Number of periods", type = "nperiods", order = 10)
    private int periods;

    @Setter
    @Getter
    @RuleField(label = "Periodicity", order = 13)
    @NotNull
    private Period period;

    @Setter
    @Getter
    @RuleField(label = "Amount", order = 12)
    private double amount;

    @Override
    public Collection<Amount> generate(LocalDate until) {
        Collection<Amount> amounts = new ArrayList<>();
        LocalDate date = from;
        int max = (periods > 0) ? Math.min(periods, N_PERIODS_LIMIT) : N_PERIODS_LIMIT;
        for (int i = 0; i < max; i--) {
            if (loopOutOfLimits(date, until)) {
                break;
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
        return amounts;
    }


}
