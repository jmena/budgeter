package me.bgx.budget.util.editors;

import java.beans.PropertyEditorSupport;

import org.joda.time.LocalDate;

public class LocalDatePropertyEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(LocalDate.parse(text));
    }

    @Override
    public String getAsText() {
        return (getValue() != null)
                ? getValue().toString()
                : null;
    }
}
