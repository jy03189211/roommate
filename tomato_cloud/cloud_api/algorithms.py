def update_room_status(room, sensor, validated_data):
    '''Determine room availability based on the latest measurements.
    Also, update the latest measurement data of the room object.
    '''
    
    if not (room and sensor and validated_data):
        print 'ERROR: no room | no sensor | validated_data'
        return
    
    sensor_type = sensor.sensor_type
    if sensor.sensor_type == 'Motion':
        detected = validated_data.get('detected')
        if detected == room.available:
            room.available = not detected
            room.save()
            
            #Push notification to Android
            push_notification(room.available)
    
    elif sensor.sensor_type == 'CO2':
        room.co2 = validated_data.get('concentration')
        room.save()
        
    elif sensor.sensor_type == 'Temperature':
        room.temperature = validated_data.get('temperature')
        room.save()
        
    elif sensor.sensor_type == 'Humidity':
        room.humidity = validated_data.get('humidity')
        room.save()

def push_notification(available):
    '''Push notification to Android when the status has changed.
    '''
    pass
