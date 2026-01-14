package com.sprint.mission.discodeit.entity;

public class User extends Common {
    private String accountId;           // 계정ID, 상속받은id(UUID)와 다름, 헷갈림 주의
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
    public void updateAccountId(String userId) {
        this.accountId = userId;
    }

    public String getName() {
        return this.name;
    }
    public void updateName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }
    public void updatePassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return this.mail;
    }
    public void updateMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return String.format("계정ID: %s\t이름: %s\t메일: %s", getAccountId(), getName(), getMail());
    }
}