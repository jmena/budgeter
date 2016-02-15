package me.bgx.budget.model.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import lombok.Data;
import me.bgx.budget.model.data.rules.Rule;

@Data
@Entity
public class Project {
    @Id
    String id;

    String ownerUserId;

    String name;

    List<Ref<Rule>> rules = new ArrayList<>();

}
