package me.bgx.budget.model.data.rules;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import com.googlecode.objectify.annotation.Subclass;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.bgx.budget.model.generators.PeriodicAmountGenerator;
import me.bgx.budget.util.IsRule;
import me.bgx.budget.util.RuleField;

@Data
@Subclass
@EqualsAndHashCode(callSuper = true)
@IsRule(type = "periodic-amount", label = "Periodic amount", generator = PeriodicAmountGenerator.class)
public class PeriodicAmountRule extends Rule {

    @NotNull
    @RuleField(label = "Start", order = 11)
    private LocalDate from;

    @RuleField(label = "Number of periods", type = "nperiods", order = 10)
    private int periods;

    @NotNull
    @RuleField(label = "Periodicity", order = 13)
    private Period period;

    @RuleField(label = "Amount", order = 12)
    private double amount;
}
