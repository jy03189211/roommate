def update_room_status(room, sensor, validated_data):
    if not (room and sensor and validated_data):
        return
    
    sensor_type = sensor.sensor_type
    if sensor.sensor_type == 'Motion':
        detected = validated_data.get('detected')
        if detected != room.available:
            room.available = detected
            room.save()
