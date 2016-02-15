package me.bgx.budget.model.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

import lombok.Setter;
import me.bgx.budget.model.data.Project;
import me.bgx.budget.model.data.RegisteredUser;
import static com.googlecode.objectify.ObjectifyService.ofy;

@Service
public class ProjectsStorageService {

    @Setter
    @Autowired
    IdGenerator idGenerator;

    @Setter
    @Autowired
    RegisteredUsersStorageService registeredUserStorageService;

    public Project load(String id) {
        if (!idGenerator.isValidId(id)) {
            return null;
        }
        return ofy().load().type(Project.class).id(id).now();
    }

    public Key<Project> save(Project project) {
        Preconditions.checkNotNull(project);

        if (!idGenerator.isValidId(project.getId())) {
            project.setId(idGenerator.newId());
        }
        Key<Project> savedKey = ofy().save().entity(project).now();
        updateProjectsForUser(savedKey);
        return savedKey;
    }

    private void updateProjectsForUser(Key<Project> savedKey) {
        RegisteredUser registeredUser = registeredUserStorageService.load();
        for (Ref<Project> refProject : registeredUser.getProjects()) {
            if (savedKey.equivalent(refProject)) {
                // project already related to the user. nothing to do here
                return;
            }
        }
        // if we reach this point, it's because the project is not registered for the user, so, register it.
        Ref<Project> savedRef = Ref.create(savedKey);
        registeredUser.getProjects().add(savedRef);
        if (registeredUser.getProjects().size() == 1) {
            registeredUser.setCurrentProject(savedRef);
        }
        registeredUserStorageService.save(registeredUser);
    }
}
