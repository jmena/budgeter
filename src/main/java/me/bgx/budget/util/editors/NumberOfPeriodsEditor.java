package me.bgx.budget.util.editors;

import java.beans.PropertyEditorSupport;

public class NumberOfPeriodsEditor extends PropertyEditorSupport {
    @Override
    public String getAsText() {
        int v = (Integer) getValue();

        if (v <= -1) {
            return "inf";
        }
        return String.valueOf(v);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null) {
            setValue(0);
            return;
        }
        text = text.trim().toLowerCase();
        try {
            int v;
            if ("inf".equals(text)) {
                v = -1;
            } else {
                v = Integer.parseInt(text);
            }
            setValue(v);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Invalid number of periods: " + text, nfe);
        }
    }
}
