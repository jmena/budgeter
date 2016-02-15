package me.bgx.budget.model.data;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import lombok.Data;

@Data
@Entity
public class RegisteredUser {
    @Id
    String userId;

    Ref<Project> currentProject;

    List<Ref<Project>> projects = new ArrayList<>();
}
