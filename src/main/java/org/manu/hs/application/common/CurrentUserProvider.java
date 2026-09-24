package org.manu.hs.application.common;

public interface CurrentUserProvider {
    String userId();
    boolean isAdmin();
}