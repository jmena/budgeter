package me.bgx.budget.model.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.Key;

import lombok.Setter;
import me.bgx.budget.model.data.RegisteredUser;
import static com.googlecode.objectify.ObjectifyService.ofy;

@Service
public class RegisteredUsersStorageService {
    @Setter
    @Autowired
    AuthenticationService auth;

    public RegisteredUser load() {
        String userId = auth.getUserId();
        Preconditions.checkNotNull(userId);
        RegisteredUser registeredUser = ofy().load().type(RegisteredUser.class).id(userId).now();
        if (registeredUser == null) {
            // user doesn't exists, create a new one by default
            registeredUser = new RegisteredUser();
            registeredUser.setUserId(userId);
            ofy().save().entity(registeredUser).now();
        }
        return registeredUser;
    }

    public Key<RegisteredUser> save(RegisteredUser registeredUser) {
        String userId = auth.getUserId();
        registeredUser.setUserId(userId);
        return ofy().save().entity(registeredUser).now();
    }
}
