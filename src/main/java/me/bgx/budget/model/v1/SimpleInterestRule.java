package me.bgx.budget.model.v1;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Subclass;

import lombok.Getter;
import lombok.Setter;
import me.bgx.budget.util.IsRule;
import me.bgx.budget.util.RuleField;

@IsRule(type = "simple-interest", label = "Simple interest periodic amount")
@Subclass
public class SimpleInterestRule extends Rule {

    @Setter
    @Getter
    @RuleField(label = "Number of periods", order = 10)
    private int periods;

    @Setter
    @Getter
    @RuleField(label = "Start", order = 11)
    private LocalDate from;

    @Setter
    @Getter
    @RuleField(label = "Owned", order = 12)
    private double owned;

    @Setter
    @Getter
    @RuleField(label = "Rate", type = "percentage", order = 13)
    private double rate;

    @Setter
    @Getter
    @RuleField(label = "Generate separated interest data", order = 14)
    private boolean detailed;

    @Setter
    @Getter
    @RuleField(label = "Periodicity", order = 15)
    private Period period;


    @Override
    public Collection<Amount> generate() {
        Collection<Amount> amounts = new ArrayList<>(periods);

        double payment = -owned / periods;
        double balance = -owned;

        LocalDate date = from;
        for (int i = 0; i < periods; i++) {
            double interest = balance * rate;
            generatePaymentInterestAmount(detailed, date, payment, interest, amounts);
            date = date.plus(period);
            balance = balance - payment;
        }
        return amounts;
    }
}
