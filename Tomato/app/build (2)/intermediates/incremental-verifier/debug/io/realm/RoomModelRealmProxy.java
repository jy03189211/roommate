package io.realm;


import android.util.JsonReader;
import android.util.JsonToken;
import fi.aalto_iot.tomato.db.data.RoomModel;
import io.realm.RealmFieldType;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.internal.ColumnInfo;
import io.realm.internal.ImplicitTransaction;
import io.realm.internal.LinkView;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.Table;
import io.realm.internal.TableOrView;
import io.realm.internal.android.JsonUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RoomModelRealmProxy extends RoomModel
    implements RealmObjectProxy {

    static final class RoomModelColumnInfo extends ColumnInfo {

        public final long idIndex;
        public final long roomNameIndex;
        public final long urlIndex;
        public final long organizationIndex;
        public final long locationIndex;
        public final long sizeIndex;
        public final long occupationIndex;
        public final long temperatureIndex;
        public final long humidityIndex;
        public final long co2Index;
        public final long isFollowedIndex;
        public final long pictureIndex;

        RoomModelColumnInfo(String path, Table table) {
            final Map<String, Long> indicesMap = new HashMap<String, Long>(12);
            this.idIndex = getValidColumnIndex(path, table, "RoomModel", "id");
            indicesMap.put("id", this.idIndex);

            this.roomNameIndex = getValidColumnIndex(path, table, "RoomModel", "roomName");
            indicesMap.put("roomName", this.roomNameIndex);

            this.urlIndex = getValidColumnIndex(path, table, "RoomModel", "url");
            indicesMap.put("url", this.urlIndex);

            this.organizationIndex = getValidColumnIndex(path, table, "RoomModel", "organization");
            indicesMap.put("organization", this.organizationIndex);

            this.locationIndex = getValidColumnIndex(path, table, "RoomModel", "location");
            indicesMap.put("location", this.locationIndex);

            this.sizeIndex = getValidColumnIndex(path, table, "RoomModel", "size");
            indicesMap.put("size", this.sizeIndex);

            this.occupationIndex = getValidColumnIndex(path, table, "RoomModel", "occupation");
            indicesMap.put("occupation", this.occupationIndex);

            this.temperatureIndex = getValidColumnIndex(path, table, "RoomModel", "temperature");
            indicesMap.put("temperature", this.temperatureIndex);

            this.humidityIndex = getValidColumnIndex(path, table, "RoomModel", "humidity");
            indicesMap.put("humidity", this.humidityIndex);

            this.co2Index = getValidColumnIndex(path, table, "RoomModel", "co2");
            indicesMap.put("co2", this.co2Index);

            this.isFollowedIndex = getValidColumnIndex(path, table, "RoomModel", "isFollowed");
            indicesMap.put("isFollowed", this.isFollowedIndex);

            this.pictureIndex = getValidColumnIndex(path, table, "RoomModel", "picture");
            indicesMap.put("picture", this.pictureIndex);

            setIndicesMap(indicesMap);
        }
    }

    private final RoomModelColumnInfo columnInfo;
    private static final List<String> FIELD_NAMES;
    static {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("id");
        fieldNames.add("roomName");
        fieldNames.add("url");
        fieldNames.add("organization");
        fieldNames.add("location");
        fieldNames.add("size");
        fieldNames.add("occupation");
        fieldNames.add("temperature");
        fieldNames.add("humidity");
        fieldNames.add("co2");
        fieldNames.add("isFollowed");
        fieldNames.add("picture");
        FIELD_NAMES = Collections.unmodifiableList(fieldNames);
    }

    RoomModelRealmProxy(ColumnInfo columnInfo) {
        this.columnInfo = (RoomModelColumnInfo) columnInfo;
    }

    @Override
    @SuppressWarnings("cast")
    public int getId() {
        realm.checkIfValid();
        return (int) row.getLong(columnInfo.idIndex);
    }

    @Override
    public void setId(int value) {
        realm.checkIfValid();
        row.setLong(columnInfo.idIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String getRoomName() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(columnInfo.roomNameIndex);
    }

    @Override
    public void setRoomName(String value) {
        realm.checkIfValid();
        if (value == null) {
            row.setNull(columnInfo.roomNameIndex);
            return;
        }
        row.setString(columnInfo.roomNameIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String getUrl() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(columnInfo.urlIndex);
    }

    @Override
    public void setUrl(String value) {
        realm.checkIfValid();
        if (value == null) {
            row.setNull(columnInfo.urlIndex);
            return;
        }
        row.setString(columnInfo.urlIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String getOrganization() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(columnInfo.organizationIndex);
    }

    @Override
    public void setOrganization(String value) {
        realm.checkIfValid();
        if (value == null) {
            row.setNull(columnInfo.organizationIndex);
            return;
        }
        row.setString(columnInfo.organizationIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String getLocation() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(columnInfo.locationIndex);
    }

    @Override
    public void setLocation(String value) {
        realm.checkIfValid();
        if (value == null) {
            row.setNull(columnInfo.locationIndex);
            return;
        }
        row.setString(columnInfo.locationIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public int getSize() {
        realm.checkIfValid();
        return (int) row.getLong(columnInfo.sizeIndex);
    }

    @Override
    public void setSize(int value) {
        realm.checkIfValid();
        row.setLong(columnInfo.sizeIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public boolean getOccupation() {
        realm.checkIfValid();
        return (boolean) row.getBoolean(columnInfo.occupationIndex);
    }

    @Override
    public void setOccupation(boolean value) {
        realm.checkIfValid();
        row.setBoolean(columnInfo.occupationIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public int getTemperature() {
        realm.checkIfValid();
        return (int) row.getLong(columnInfo.temperatureIndex);
    }

    @Override
    public void setTemperature(int value) {
        realm.checkIfValid();
        row.setLong(columnInfo.temperatureIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public int getHumidity() {
        realm.checkIfValid();
        return (int) row.getLong(columnInfo.humidityIndex);
    }

    @Override
    public void setHumidity(int value) {
        realm.checkIfValid();
        row.setLong(columnInfo.humidityIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public int getCo2() {
        realm.checkIfValid();
        return (int) row.getLong(columnInfo.co2Index);
    }

    @Override
    public void setCo2(int value) {
        realm.checkIfValid();
        row.setLong(columnInfo.co2Index, value);
    }

    @Override
    @SuppressWarnings("cast")
    public boolean isFollowed() {
        realm.checkIfValid();
        return (boolean) row.getBoolean(columnInfo.isFollowedIndex);
    }

    @Override
    public void setFollowed(boolean value) {
        realm.checkIfValid();
        row.setBoolean(columnInfo.isFollowedIndex, value);
    }

    @Override
    @SuppressWarnings("cast")
    public String getPicture() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(columnInfo.pictureIndex);
    }

    @Override
    public void setPicture(String value) {
        realm.checkIfValid();
        if (value == null) {
            row.setNull(columnInfo.pictureIndex);
            return;
        }
        row.setString(columnInfo.pictureIndex, value);
    }

    public static Table initTable(ImplicitTransaction transaction) {
        if (!transaction.hasTable("class_RoomModel")) {
            Table table = transaction.getTable("class_RoomModel");
            table.addColumn(RealmFieldType.INTEGER, "id", Table.NOT_NULLABLE);
            table.addColumn(RealmFieldType.STRING, "roomName", Table.NULLABLE);
            table.addColumn(RealmFieldType.STRING, "url", Table.NULLABLE);
            table.addColumn(RealmFieldType.STRING, "organization", Table.NULLABLE);
            table.addColumn(RealmFieldType.STRING, "location", Table.NULLABLE);
            table.addColumn(RealmFieldType.INTEGER, "size", Table.NOT_NULLABLE);
            table.addColumn(RealmFieldType.BOOLEAN, "occupation", Table.NOT_NULLABLE);
            table.addColumn(RealmFieldType.INTEGER, "temperature", Table.NOT_NULLABLE);
            table.addColumn(RealmFieldType.INTEGER, "humidity", Table.NOT_NULLABLE);
            table.addColumn(RealmFieldType.INTEGER, "co2", Table.NOT_NULLABLE);
            table.addColumn(RealmFieldType.BOOLEAN, "isFollowed", Table.NOT_NULLABLE);
            table.addColumn(RealmFieldType.STRING, "picture", Table.NULLABLE);
            table.addSearchIndex(table.getColumnIndex("id"));
            table.setPrimaryKey("id");
            return table;
        }
        return transaction.getTable("class_RoomModel");
    }

    public static RoomModelColumnInfo validateTable(ImplicitTransaction transaction) {
        if (transaction.hasTable("class_RoomModel")) {
            Table table = transaction.getTable("class_RoomModel");
            if (table.getColumnCount() != 12) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field count does not match - expected 12 but was " + table.getColumnCount());
            }
            Map<String, RealmFieldType> columnTypes = new HashMap<String, RealmFieldType>();
            for (long i = 0; i < 12; i++) {
                columnTypes.put(table.getColumnName(i), table.getColumnType(i));
            }

            final RoomModelColumnInfo columnInfo = new RoomModelColumnInfo(transaction.getPath(), table);

            if (!columnTypes.containsKey("id")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'id' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("id") != RealmFieldType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'int' for field 'id' in existing Realm file.");
            }
            if (table.isColumnNullable(columnInfo.idIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'id' does support null values in the existing Realm file. Use corresponding boxed type for field 'id' or migrate using io.realm.internal.Table.convertColumnToNotNullable().");
            }
            if (table.getPrimaryKey() != table.getColumnIndex("id")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Primary key not defined for field 'id' in existing Realm file. Add @PrimaryKey.");
            }
            if (!table.hasSearchIndex(table.getColumnIndex("id"))) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Index not defined for field 'id' in existing Realm file. Either set @Index or migrate using io.realm.internal.Table.removeSearchIndex().");
            }
            if (!columnTypes.containsKey("roomName")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'roomName' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("roomName") != RealmFieldType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'roomName' in existing Realm file.");
            }
            if (!table.isColumnNullable(columnInfo.roomNameIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'roomName' is required. Either set @Required to field 'roomName' or migrate using io.realm.internal.Table.convertColumnToNullable().");
            }
            if (!columnTypes.containsKey("url")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'url' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("url") != RealmFieldType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'url' in existing Realm file.");
            }
            if (!table.isColumnNullable(columnInfo.urlIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'url' is required. Either set @Required to field 'url' or migrate using io.realm.internal.Table.convertColumnToNullable().");
            }
            if (!columnTypes.containsKey("organization")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'organization' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("organization") != RealmFieldType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'organization' in existing Realm file.");
            }
            if (!table.isColumnNullable(columnInfo.organizationIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'organization' is required. Either set @Required to field 'organization' or migrate using io.realm.internal.Table.convertColumnToNullable().");
            }
            if (!columnTypes.containsKey("location")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'location' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("location") != RealmFieldType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'location' in existing Realm file.");
            }
            if (!table.isColumnNullable(columnInfo.locationIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'location' is required. Either set @Required to field 'location' or migrate using io.realm.internal.Table.convertColumnToNullable().");
            }
            if (!columnTypes.containsKey("size")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'size' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("size") != RealmFieldType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'int' for field 'size' in existing Realm file.");
            }
            if (table.isColumnNullable(columnInfo.sizeIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'size' does support null values in the existing Realm file. Use corresponding boxed type for field 'size' or migrate using io.realm.internal.Table.convertColumnToNotNullable().");
            }
            if (!columnTypes.containsKey("occupation")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'occupation' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("occupation") != RealmFieldType.BOOLEAN) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'boolean' for field 'occupation' in existing Realm file.");
            }
            if (table.isColumnNullable(columnInfo.occupationIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'occupation' does support null values in the existing Realm file. Use corresponding boxed type for field 'occupation' or migrate using io.realm.internal.Table.convertColumnToNotNullable().");
            }
            if (!columnTypes.containsKey("temperature")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'temperature' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("temperature") != RealmFieldType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'int' for field 'temperature' in existing Realm file.");
            }
            if (table.isColumnNullable(columnInfo.temperatureIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'temperature' does support null values in the existing Realm file. Use corresponding boxed type for field 'temperature' or migrate using io.realm.internal.Table.convertColumnToNotNullable().");
            }
            if (!columnTypes.containsKey("humidity")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'humidity' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("humidity") != RealmFieldType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'int' for field 'humidity' in existing Realm file.");
            }
            if (table.isColumnNullable(columnInfo.humidityIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'humidity' does support null values in the existing Realm file. Use corresponding boxed type for field 'humidity' or migrate using io.realm.internal.Table.convertColumnToNotNullable().");
            }
            if (!columnTypes.containsKey("co2")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'co2' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("co2") != RealmFieldType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'int' for field 'co2' in existing Realm file.");
            }
            if (table.isColumnNullable(columnInfo.co2Index)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'co2' does support null values in the existing Realm file. Use corresponding boxed type for field 'co2' or migrate using io.realm.internal.Table.convertColumnToNotNullable().");
            }
            if (!columnTypes.containsKey("isFollowed")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'isFollowed' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("isFollowed") != RealmFieldType.BOOLEAN) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'boolean' for field 'isFollowed' in existing Realm file.");
            }
            if (table.isColumnNullable(columnInfo.isFollowedIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'isFollowed' does support null values in the existing Realm file. Use corresponding boxed type for field 'isFollowed' or migrate using io.realm.internal.Table.convertColumnToNotNullable().");
            }
            if (!columnTypes.containsKey("picture")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'picture' in existing Realm file. Either remove field or migrate using io.realm.internal.Table.addColumn().");
            }
            if (columnTypes.get("picture") != RealmFieldType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'picture' in existing Realm file.");
            }
            if (!table.isColumnNullable(columnInfo.pictureIndex)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field 'picture' is required. Either set @Required to field 'picture' or migrate using io.realm.internal.Table.convertColumnToNullable().");
            }
            return columnInfo;
        } else {
            throw new RealmMigrationNeededException(transaction.getPath(), "The RoomModel class is missing from the schema for this Realm.");
        }
    }

    public static String getTableName() {
        return "class_RoomModel";
    }

    public static List<String> getFieldNames() {
        return FIELD_NAMES;
    }

    @SuppressWarnings("cast")
    public static RoomModel createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        RoomModel obj = null;
        if (update) {
            Table table = realm.getTable(RoomModel.class);
            long pkColumnIndex = table.getPrimaryKey();
            if (!json.isNull("id")) {
                long rowIndex = table.findFirstLong(pkColumnIndex, json.getLong("id"));
                if (rowIndex != TableOrView.NO_MATCH) {
                    obj = new RoomModelRealmProxy(realm.schema.getColumnInfo(RoomModel.class));
                    obj.realm = realm;
                    obj.row = table.getUncheckedRow(rowIndex);
                }
            }
        }
        if (obj == null) {
            if (json.has("id")) {
                if (json.isNull("id")) {
                    obj = realm.createObject(RoomModel.class, null);
                } else {
                    obj = realm.createObject(RoomModel.class, json.getInt("id"));
                }
            } else {
                obj = realm.createObject(RoomModel.class);
            }
        }
        if (json.has("id")) {
            if (json.isNull("id")) {
                throw new IllegalArgumentException("Trying to set non-nullable field id to null.");
            } else {
                obj.setId((int) json.getInt("id"));
            }
        }
        if (json.has("roomName")) {
            if (json.isNull("roomName")) {
                obj.setRoomName(null);
            } else {
                obj.setRoomName((String) json.getString("roomName"));
            }
        }
        if (json.has("url")) {
            if (json.isNull("url")) {
                obj.setUrl(null);
            } else {
                obj.setUrl((String) json.getString("url"));
            }
        }
        if (json.has("organization")) {
            if (json.isNull("organization")) {
                obj.setOrganization(null);
            } else {
                obj.setOrganization((String) json.getString("organization"));
            }
        }
        if (json.has("location")) {
            if (json.isNull("location")) {
                obj.setLocation(null);
            } else {
                obj.setLocation((String) json.getString("location"));
            }
        }
        if (json.has("size")) {
            if (json.isNull("size")) {
                throw new IllegalArgumentException("Trying to set non-nullable field size to null.");
            } else {
                obj.setSize((int) json.getInt("size"));
            }
        }
        if (json.has("occupation")) {
            if (json.isNull("occupation")) {
                throw new IllegalArgumentException("Trying to set non-nullable field occupation to null.");
            } else {
                obj.setOccupation((boolean) json.getBoolean("occupation"));
            }
        }
        if (json.has("temperature")) {
            if (json.isNull("temperature")) {
                throw new IllegalArgumentException("Trying to set non-nullable field temperature to null.");
            } else {
                obj.setTemperature((int) json.getInt("temperature"));
            }
        }
        if (json.has("humidity")) {
            if (json.isNull("humidity")) {
                throw new IllegalArgumentException("Trying to set non-nullable field humidity to null.");
            } else {
                obj.setHumidity((int) json.getInt("humidity"));
            }
        }
        if (json.has("co2")) {
            if (json.isNull("co2")) {
                throw new IllegalArgumentException("Trying to set non-nullable field co2 to null.");
            } else {
                obj.setCo2((int) json.getInt("co2"));
            }
        }
        if (json.has("isFollowed")) {
            if (json.isNull("isFollowed")) {
                throw new IllegalArgumentException("Trying to set non-nullable field isFollowed to null.");
            } else {
                obj.setFollowed((boolean) json.getBoolean("isFollowed"));
            }
        }
        if (json.has("picture")) {
            if (json.isNull("picture")) {
                obj.setPicture(null);
            } else {
                obj.setPicture((String) json.getString("picture"));
            }
        }
        return obj;
    }

    @SuppressWarnings("cast")
    public static RoomModel createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        RoomModel obj = realm.createObject(RoomModel.class);
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field id to null.");
                } else {
                    obj.setId((int) reader.nextInt());
                }
            } else if (name.equals("roomName")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    obj.setRoomName(null);
                } else {
                    obj.setRoomName((String) reader.nextString());
                }
            } else if (name.equals("url")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    obj.setUrl(null);
                } else {
                    obj.setUrl((String) reader.nextString());
                }
            } else if (name.equals("organization")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    obj.setOrganization(null);
                } else {
                    obj.setOrganization((String) reader.nextString());
                }
            } else if (name.equals("location")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    obj.setLocation(null);
                } else {
                    obj.setLocation((String) reader.nextString());
                }
            } else if (name.equals("size")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field size to null.");
                } else {
                    obj.setSize((int) reader.nextInt());
                }
            } else if (name.equals("occupation")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field occupation to null.");
                } else {
                    obj.setOccupation((boolean) reader.nextBoolean());
                }
            } else if (name.equals("temperature")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field temperature to null.");
                } else {
                    obj.setTemperature((int) reader.nextInt());
                }
            } else if (name.equals("humidity")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field humidity to null.");
                } else {
                    obj.setHumidity((int) reader.nextInt());
                }
            } else if (name.equals("co2")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field co2 to null.");
                } else {
                    obj.setCo2((int) reader.nextInt());
                }
            } else if (name.equals("isFollowed")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    throw new IllegalArgumentException("Trying to set non-nullable field isFollowed to null.");
                } else {
                    obj.setFollowed((boolean) reader.nextBoolean());
                }
            } else if (name.equals("picture")) {
                if (reader.peek() == JsonToken.NULL) {
                    reader.skipValue();
                    obj.setPicture(null);
                } else {
                    obj.setPicture((String) reader.nextString());
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return obj;
    }

    public static RoomModel copyOrUpdate(Realm realm, RoomModel object, boolean update, Map<RealmObject,RealmObjectProxy> cache) {
        if (object.realm != null && object.realm.getPath().equals(realm.getPath())) {
            return object;
        }
        RoomModel realmObject = null;
        boolean canUpdate = update;
        if (canUpdate) {
            Table table = realm.getTable(RoomModel.class);
            long pkColumnIndex = table.getPrimaryKey();
            long rowIndex = table.findFirstLong(pkColumnIndex, object.getId());
            if (rowIndex != TableOrView.NO_MATCH) {
                realmObject = new RoomModelRealmProxy(realm.schema.getColumnInfo(RoomModel.class));
                realmObject.realm = realm;
                realmObject.row = table.getUncheckedRow(rowIndex);
                cache.put(object, (RealmObjectProxy) realmObject);
            } else {
                canUpdate = false;
            }
        }

        if (canUpdate) {
            return update(realm, realmObject, object, cache);
        } else {
            return copy(realm, object, update, cache);
        }
    }

    public static RoomModel copy(Realm realm, RoomModel newObject, boolean update, Map<RealmObject,RealmObjectProxy> cache) {
        RoomModel realmObject = realm.createObject(RoomModel.class, newObject.getId());
        cache.put(newObject, (RealmObjectProxy) realmObject);
        realmObject.setId(newObject.getId());
        realmObject.setRoomName(newObject.getRoomName());
        realmObject.setUrl(newObject.getUrl());
        realmObject.setOrganization(newObject.getOrganization());
        realmObject.setLocation(newObject.getLocation());
        realmObject.setSize(newObject.getSize());
        realmObject.setOccupation(newObject.getOccupation());
        realmObject.setTemperature(newObject.getTemperature());
        realmObject.setHumidity(newObject.getHumidity());
        realmObject.setCo2(newObject.getCo2());
        realmObject.setFollowed(newObject.isFollowed());
        realmObject.setPicture(newObject.getPicture());
        return realmObject;
    }

    public static RoomModel createDetachedCopy(RoomModel realmObject, int currentDepth, int maxDepth, Map<RealmObject, CacheData<RealmObject>> cache) {
        if (currentDepth > maxDepth || realmObject == null) {
            return null;
        }
        CacheData<RoomModel> cachedObject = (CacheData) cache.get(realmObject);
        RoomModel standaloneObject;
        if (cachedObject != null) {
            // Reuse cached object or recreate it because it was encountered at a lower depth.
            if (currentDepth >= cachedObject.minDepth) {
                return cachedObject.object;
            } else {
                standaloneObject = cachedObject.object;
                cachedObject.minDepth = currentDepth;
            }
        } else {
            standaloneObject = new RoomModel();
            cache.put(realmObject, new RealmObjectProxy.CacheData<RealmObject>(currentDepth, standaloneObject));
        }
        standaloneObject.setId(realmObject.getId());
        standaloneObject.setRoomName(realmObject.getRoomName());
        standaloneObject.setUrl(realmObject.getUrl());
        standaloneObject.setOrganization(realmObject.getOrganization());
        standaloneObject.setLocation(realmObject.getLocation());
        standaloneObject.setSize(realmObject.getSize());
        standaloneObject.setOccupation(realmObject.getOccupation());
        standaloneObject.setTemperature(realmObject.getTemperature());
        standaloneObject.setHumidity(realmObject.getHumidity());
        standaloneObject.setCo2(realmObject.getCo2());
        standaloneObject.setFollowed(realmObject.isFollowed());
        standaloneObject.setPicture(realmObject.getPicture());
        return standaloneObject;
    }

    static RoomModel update(Realm realm, RoomModel realmObject, RoomModel newObject, Map<RealmObject, RealmObjectProxy> cache) {
        realmObject.setRoomName(newObject.getRoomName());
        realmObject.setUrl(newObject.getUrl());
        realmObject.setOrganization(newObject.getOrganization());
        realmObject.setLocation(newObject.getLocation());
        realmObject.setSize(newObject.getSize());
        realmObject.setOccupation(newObject.getOccupation());
        realmObject.setTemperature(newObject.getTemperature());
        realmObject.setHumidity(newObject.getHumidity());
        realmObject.setCo2(newObject.getCo2());
        realmObject.setFollowed(newObject.isFollowed());
        realmObject.setPicture(newObject.getPicture());
        return realmObject;
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("RoomModel = [");
        stringBuilder.append("{id:");
        stringBuilder.append(getId());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{roomName:");
        stringBuilder.append(getRoomName() != null ? getRoomName() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{url:");
        stringBuilder.append(getUrl() != null ? getUrl() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{organization:");
        stringBuilder.append(getOrganization() != null ? getOrganization() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{location:");
        stringBuilder.append(getLocation() != null ? getLocation() : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{size:");
        stringBuilder.append(getSize());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{occupation:");
        stringBuilder.append(getOccupation());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{temperature:");
        stringBuilder.append(getTemperature());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{humidity:");
        stringBuilder.append(getHumidity());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{co2:");
        stringBuilder.append(getCo2());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{isFollowed:");
        stringBuilder.append(isFollowed());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{picture:");
        stringBuilder.append(getPicture() != null ? getPicture() : "null");
        stringBuilder.append("}");
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        String realmName = realm.getPath();
        String tableName = row.getTable().getName();
        long rowIndex = row.getIndex();

        int result = 17;
        result = 31 * result + ((realmName != null) ? realmName.hashCode() : 0);
        result = 31 * result + ((tableName != null) ? tableName.hashCode() : 0);
        result = 31 * result + (int) (rowIndex ^ (rowIndex >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomModelRealmProxy aRoomModel = (RoomModelRealmProxy)o;

        String path = realm.getPath();
        String otherPath = aRoomModel.realm.getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;;

        String tableName = row.getTable().getName();
        String otherTableName = aRoomModel.row.getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (row.getIndex() != aRoomModel.row.getIndex()) return false;

        return true;
    }

}
