package com.sprint.mission.discodeit.entity;

import java.util.Objects;

public class User extends Common {
    private String accountId;
    private String password;
    private String name;
    private String mail;

    public User(String accountId, String password, String name, String mail) {
        super();
        this.accountId = accountId;
        this.password = password;
        this.name = name;
        this.mail = mail;
    }

    public String getAccountId() {
        return this.accountId;
    }
    void updateAccountId(String userId) {
        this.accountId = userId;
    }

    public String getName() {
        return this.name;
    }
    void updateName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }
    void updatePassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return this.mail;
    }
    void updateMail(String mail) {
        this.mail = mail;
    }

    public void update(String accountId, String password, String name, String mail) {
        boolean isChanged = false;
        if (!Objects.equals(getAccountId(), accountId)) {
            updateAccountId(accountId);
            isChanged = true;
        }
        if (!Objects.equals(getPassword(), password)) {
            updatePassword(password);
            isChanged = true;
        }
        if (!Objects.equals(getName(), name)) {
            updateName(name);
            isChanged = true;
        }
        if (!Objects.equals(getMail(), mail)) {
            updateMail(mail);
            isChanged = true;
        }

        if (isChanged) {
            updateUpdatedAt();
        }
    }

    @Override
    public String toString() {
        return String.format("계정ID: %s\t이름: %s\t메일: %s", getAccountId(), getName(), getMail());
    }
}
