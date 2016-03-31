package fi.aalto_iot.tomato.db.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RoomModel extends RealmObject {
    @PrimaryKey
    private String roomName;
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
}
