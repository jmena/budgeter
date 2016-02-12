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
    IdGenerator idGenerator;

    public Rule get(String userId, String id) {
        Preconditions.checkNotNull(userId);
        if (Strings.isNullOrEmpty(userId) || Strings.isNullOrEmpty(id)) {
            return null;
        }
        Rule rule = ofy().load().type(Rule.class).id(id).now();
        if (!userId.equals(rule.getUserId())) {
            return null;
        }
        return rule;
    }

    public void save(String userId, Rule rule) {
        Preconditions.checkNotNull(userId);
        if (Strings.isNullOrEmpty(rule.getId()) || "new".equals(rule.getId())) {
            rule.setId(idGenerator.newId());
        }
        rule.setUserId(userId);
        ofy().save().entity(rule);
    }

    public void delete(String userId, String id) {
        Preconditions.checkNotNull(userId);
        Rule rule = get(userId, id);
        if (!userId.equals(rule.getUserId())) {
            return;
        }
        ofy().delete().type(Rule.class).id(id).now();
    }

    public Collection<? extends Rule> list(String userId) {
        Preconditions.checkNotNull(userId);
        List<Rule> rules = ofy().load().type(Rule.class).filter("userId", userId).list();
        return rules;
    }
}