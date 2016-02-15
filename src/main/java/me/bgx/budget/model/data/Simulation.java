package me.bgx.budget.model.data;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import lombok.Data;

@Data
@Entity
public class Simulation {
    @Id
    String id;

    Ref<Project> project;
}
