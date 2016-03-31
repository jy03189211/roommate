package fi.aalto_iot.tomato.db.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RoomModel extends RealmObject {
    @PrimaryKey
    private String roomName;
    private String organization;
    private String location;
    private int size;
    private boolean occupation;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean getOccupation() {
        return occupation;
    }

    public void setOccupation(boolean occupation) {
        this.occupation = occupation;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
