package me.bgx.budget.security;

import java.security.Principal;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Builder
public class User implements Principal {

    public enum SourceLogin {
        GOOGLE
    }

    @Getter
    SourceLogin source;

    @Getter
    String email;

    @Getter
    boolean admin;

    @Override
    public String getName() {
        return email;
    }
}
