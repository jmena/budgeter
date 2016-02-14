package me.bgx.budget.model.v1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.annotation.Subclass;

import lombok.Getter;
import lombok.Setter;
import me.bgx.budget.util.IsRule;
import me.bgx.budget.util.RuleField;

@IsRule(type = "monthly-amortized-load", label = "Monthly amortized loan")
@Subclass
public class MonthlyAmortizedLoad extends Rule {

    @Setter
    @Getter
    @RuleField(label = "Number of periods", order = 10)
    private int periods;

    @Setter
    @Getter
    @NotNull
    @RuleField(label = "Start", order = 11)
    private LocalDate from;

    @Setter
    @Getter
    @RuleField(label = "Amount", order = 12)
    private double owned;

    @Setter
    @Getter
    @RuleField(label = "Rate", type = "percentage", order = 14)
    private double rate;

    @Setter
    @Getter
    @RuleField(label = "Detailed data", additionalInformation = "Split capital contribution and interest", order = 15)
    private boolean detailed;

    @Setter
    @Getter
    @NotNull
    @RuleField(label = "Periodicity", order = 13)
    private Period period;

    @Override
    public Collection<Amount> generate(LocalDate until) {
        Preconditions.checkState(0 <= periods && periods <= 10000);
        Preconditions.checkState(0.0 <= rate && rate <= 10000.0);
        // https://en.wikipedia.org/wiki/Mortgage_calculator
        // rP / (1 - (1 + r)^-N)
        final double fixedPayment;
        if (rate == 0.0) {
            fixedPayment = -owned / periods;
        } else {
            fixedPayment = -(rate * owned) / (1.0 - Math.pow(1.0 + rate, -periods));
        }

        LocalDate date = from;

        Collection<Amount> amounts = new ArrayList<>();
        double balance = -owned;
        int max = (periods > 0) ? Math.min(periods, N_PERIODS_LIMIT) : N_PERIODS_LIMIT;
        for (int i = 0; i < max; i++) {
            if (loopOutOfLimits(date, until)) {
                break;
            }
            double interest = balance * rate;
            double capitalContribution = fixedPayment - interest;
            generatePaymentInterestAmount(detailed, date, capitalContribution, interest, amounts);
            date = date.plus(period);
            balance = balance + interest - fixedPayment;
        }
        return amounts;
    }
}
