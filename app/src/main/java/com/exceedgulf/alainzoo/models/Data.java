package com.exceedgulf.alainzoo.models;

public class Data {

    private CurrentUser current_user;
    private String csrf_token;
    private String logout_token;

    public CurrentUser getCurrentUser() {
        return current_user;
    }

    public void setCurrentUser(CurrentUser currentUser) {
        this.current_user = currentUser;
    }

    public String getCsrfToken() {
        return csrf_token;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrf_token = csrfToken;
    }

    public String getLogoutToken() {
        return logout_token;
    }

    public void setLogoutToken(String logoutToken) {
        this.logout_token = logoutToken;
    }

}