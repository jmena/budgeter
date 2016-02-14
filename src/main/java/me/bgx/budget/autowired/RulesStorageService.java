package me.bgx.budget.autowired;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.bgx.budget.model.v1.Rule;
import static com.googlecode.objectify.ObjectifyService.ofy;

@Slf4j
@Service
public class RulesStorageService {

    @Autowired
    @Setter
    AuthenticationService auth;

    @Autowired
    @Setter
    IdGenerator idGenerator;

    public Rule load(String id) {
        String userId = auth.getUserId();;
        Preconditions.checkNotNull(userId);
        if (Strings.isNullOrEmpty(userId) || !idGenerator.isValidId(id)) {
            return null;
        }
        Rule rule = ofy().load().type(Rule.class).id(id).now();
        if (rule != null && !userId.equals(rule.getUserId())) {
            return null;
        }
        return rule;
    }

    public void save(Rule rule) {
        String userId = auth.getUserId();;
        Preconditions.checkNotNull(rule);
        Preconditions.checkNotNull(userId);

        // if it's a new rule, create an id
        if (!idGenerator.isValidId(rule.getId())) {
            rule.setId(idGenerator.newId());
        }

        // userId not set, use default
        if (rule.getUserId() == null) {
            rule.setUserId(userId);
        }

        // if we're updating a rule, check if the userId matches between the stored rule and the new rule
        Rule storedRule = load(rule.getId());
        if (storedRule != null && !userId.equals(storedRule.getUserId())) {
            return;
        }

        ofy().save().entity(rule);
    }

    public void delete(String id) {
        String userId = auth.getUserId();;
        Preconditions.checkNotNull(userId);
        Rule rule = load(id);
        if (rule == null || !userId.equals(rule.getUserId())) {
            return;
        }
        ofy().delete().type(Rule.class).id(id).now();
    }

    public Collection<? extends Rule> list() {
        String userId = auth.getUserId();;
        Preconditions.checkNotNull(userId);
        List<Rule> rules = ofy().load().type(Rule.class).filter("userId", userId).list();
        return rules;
    }
}
