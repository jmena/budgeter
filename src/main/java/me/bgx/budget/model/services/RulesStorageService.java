package me.bgx.budget.model.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.googlecode.objectify.Key;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.bgx.budget.model.data.rules.Rule;
import static com.googlecode.objectify.ObjectifyService.ofy;

@Slf4j
@Service
public class RulesStorageService {

    @Setter
    @Autowired
    AuthenticationService auth;

    @Setter
    @Autowired
    IdGenerator idGenerator;

    public Rule load(String id) {
        String userId = auth.getUserId();;
        Preconditions.checkNotNull(userId);
        if (Strings.isNullOrEmpty(userId) || !idGenerator.isValidId(id)) {
            return null;
        }
        return ofy().load().type(Rule.class).id(id).now();
    }

    public Key<Rule> save(Rule rule) {
        String userId = auth.getUserId();;
        Preconditions.checkNotNull(rule);
        Preconditions.checkNotNull(userId);

        // if it's a new rule, create an id
        if (!idGenerator.isValidId(rule.getId())) {
            rule.setId(idGenerator.newId());
        }

        return ofy().save().entity(rule).now();
    }

    public void delete(String id) {
        String userId = auth.getUserId();;
        Preconditions.checkNotNull(userId);
        ofy().delete().type(Rule.class).id(id).now();
    }

//    public Collection<Rule> list() {
//        String userId = auth.getUserId();;
//        Preconditions.checkNotNull(userId);
//        List<Rule> rules = ofy().load().type(Rule.class).filter("userId", userId).list();
//        return rules;
//    }
}
