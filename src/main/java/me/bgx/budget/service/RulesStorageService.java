package me.bgx.budget.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.googlecode.objectify.ObjectifyService;

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

    public Rule get(String id) {
        if (Strings.isNullOrEmpty(id)) {
            return null;
        }
        return ofy().load().type(Rule.class).id(id).now();
    }

    public void save(Rule rule) {
        if (Strings.isNullOrEmpty(rule.getId()) || "new".equals(rule.getId())) {
            rule.setId(idGenerator.newId());
        }
        ofy().save().entity(rule);
    }

    public void delete(String id) {
        ofy().delete().type(Rule.class).id(id).now();
    }

    public Collection<? extends Rule> list() {
        ObjectifyService.begin();
        List<Rule> rules = ofy().load().type(Rule.class).list();
        return rules;
    }
}
