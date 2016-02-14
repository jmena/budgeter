package me.bgx.budget.util.editors;

import java.beans.PropertyEditorSupport;

public class PercentagePropertyEditor extends PropertyEditorSupport {
    @Override
    public String getAsText() {
        Double value = (Double) getValue();
        if (getValue() == null) {
            return "0%";
        }
        String v = String.format("%f", value * 100);

        if (v.endsWith(".")) {
            v = v.substring(0, v.length() - 1);
        }
        return v + "%";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null) {
            setValue(0.0);
            return;
        }
        text = text.trim();
        try {
            if (text.endsWith("%")) {
                text = text.substring(0, text.length() - 1);
                setValue(Double.parseDouble(text) / 100);
            } else if (text.startsWith("%")) {
                text = text.substring(1, text.length());
                setValue(Double.parseDouble(text) / 100);
            } else {
                setValue(Double.parseDouble(text));
            }
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Invalid percentage: " + text, nfe);
        }
    }
}
