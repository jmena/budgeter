package me.bgx.budget.model.data.rules;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import com.googlecode.objectify.annotation.Subclass;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.bgx.budget.model.generators.SimpleInterestGenerator;
import me.bgx.budget.util.IsRule;
import me.bgx.budget.util.RuleField;

@Data
@Subclass
@EqualsAndHashCode(callSuper = true)
@IsRule(type = "simple-interest", label = "Simple interest periodic amount", generator = SimpleInterestGenerator.class)
public class SimpleInterestRule extends Rule {

    @RuleField(label = "Number of periods", order = 10)
    private int periods;

    @NotNull
    @RuleField(label = "Start", order = 11)
    private LocalDate from;

    @RuleField(label = "Amount", order = 12)
    private double owned;

    @RuleField(label = "Rate", type = "percentage", order = 14)
    private double rate;

    @RuleField(label = "Detailed data", additionalInformation = "Split capital contribution and interest", order = 15)
    private boolean detailed;

    @NotNull
    @RuleField(label = "Periodicity", order = 13)
    private Period period;

}
