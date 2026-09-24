package org.manu.hs.application.common;

import org.springframework.stereotype.Component;

@Component
public class MutableCurrentUserProvider implements CurrentUserProvider {
 
    private String userId = "test-user";
    private boolean admin = false;
 
    public void actingAs(String userId, boolean admin) {
        this.userId = userId;
        this.admin = admin;
    }
 
    public void reset() {
        this.userId = "test-user";
        this.admin = false;
    }
 
    @Override
    public String userId() {
        return userId;
    }
 
    @Override
    public boolean isAdmin() {
        return admin;
    }
}