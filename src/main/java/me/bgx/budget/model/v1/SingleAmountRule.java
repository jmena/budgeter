package me.bgx.budget.model.v1;

import java.util.ArrayList;
import java.util.Collection;
import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.googlecode.objectify.annotation.Subclass;

import lombok.Getter;
import lombok.Setter;
import me.bgx.budget.util.IsRule;
import me.bgx.budget.util.RuleField;

@IsRule(type = "single-amount", label = "Single amount")
@Subclass
public class SingleAmountRule extends Rule {
    @Setter
    @Getter
    @NotNull
    @RuleField(label = "When", order = 10)
    private LocalDate when;

    @Setter
    @Getter
    @RuleField(label = "Amount", order = 11)
    private double amount;

    @Override
    public Collection<Amount> generate(LocalDate until) {
        Collection<Amount> amounts = new ArrayList<>();
        if (!loopOutOfLimits(when, until)) {
            amounts.add(Amount.builder()
                    .description(getName())
                    .date(when)
                    .rule(this)
                    .amount(amount)
                    .build());
        }

        return amounts;
    }
}
