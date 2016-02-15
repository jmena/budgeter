package me.bgx.budget.model.data.rules;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.googlecode.objectify.annotation.Subclass;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.bgx.budget.model.generators.SingleAmountGenerator;
import me.bgx.budget.util.IsRule;
import me.bgx.budget.util.RuleField;

@Data
@Subclass
@EqualsAndHashCode(callSuper = true)
@IsRule(type = "single-amount", label = "Single amount", generator = SingleAmountGenerator.class)
public class SingleAmountRule extends Rule {

    @NotNull
    @RuleField(label = "When", order = 10)
    private LocalDate when;

    @RuleField(label = "Amount", order = 11)
    private double amount;

}
