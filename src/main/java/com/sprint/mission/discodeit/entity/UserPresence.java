package com.sprint.mission.discodeit.entity;

public class UserPresence {
    private UserStatus status;
    private boolean isMicrophoneOn;
    private boolean isHeadsetOn;

    public UserPresence() {
        this.status = UserStatus.OFFLINE;
        this.isMicrophoneOn = false;
        this.isHeadsetOn = false;
    }

    // Getter
    public UserStatus getStatus() {
        return status;
    }

    public boolean isMicrophoneOn() {
        return isMicrophoneOn;
    }

    public boolean isHeadsetOn() {
        return isHeadsetOn;
    }

    // update
    public void changeStatus(UserStatus status) {
        this.status = status;
    }

    public void changeMicrophone(boolean isOn) {
        this.isMicrophoneOn = isOn;
    }

    public void changeHeadset(boolean isOn) {
        this.isHeadsetOn = isOn;
    }
}