package me.bgx.budget.model.v1;

import org.joda.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Amount {
    String description = "";
    LocalDate date;
    double amount;
    Rule rule;
}
