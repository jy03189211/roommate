def update_room_status(room, sensor, validated_data):
    if not (room and sensor and validated_data):
        print 'ERROR: no room | no sensor | validated_data'
        return
    
    sensor_type = sensor.sensor_type
    if sensor.sensor_type == 'Motion':
        detected = validated_data.get('detected')
        if detected == room.available:
            room.available = not detected
            room.save()
    elif sensor.sensor_type == 'CO2':
        room.co2 = validated_data.get('concentration')
        room.save()
    elif sensor.sensor_type == 'Temperature':
        room.temperature = validated_data.get('temperature')
        room.save()
    elif sensor.sensor_type == 'Humidity':
        room.humidity = validated_data.get('humidity')
        room.save()
