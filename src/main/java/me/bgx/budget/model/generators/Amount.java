package me.bgx.budget.model.generators;

import org.joda.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import me.bgx.budget.model.data.rules.Rule;

@Data
@Builder
public class Amount {
    String description = "";
    LocalDate date;
    double amount;
    Rule rule;
}
