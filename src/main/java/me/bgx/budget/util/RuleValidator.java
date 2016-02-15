package me.bgx.budget.util;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import me.bgx.budget.model.data.rules.Rule;

public class RuleValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Rule.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Rule rule = (Rule) target;
//        ValidationUtils.rejectIfEmpty(errors, "from", "from.empty");
//        ValidationUtils.rejectIfEmpty(errors, "to", "to.empty");
//        if (rule.getFrom().compareTo(rule.getTo()) > 0) {
//            errors.reject("from.after.to");
//        }
    }
}
