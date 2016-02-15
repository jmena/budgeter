package me.bgx.budget.model.generators;

import java.util.Collection;

import org.joda.time.LocalDate;

public interface Generator {

    Collection<Amount> generate(LocalDate until);
}
