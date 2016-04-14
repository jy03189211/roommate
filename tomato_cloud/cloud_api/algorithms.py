from gcm.models import get_device_model
from gcm.api import GCMMessage
import sys

CO2_LEVEL = {
    'very fresh': (0, 0),
    'fresh': (600, 0),
    'ok': (700, -1),
    'drowsy': (900, -2),
    'poor': (1200, -3)
}

# TEMP_LEVEL = {
#     'cold': (-273, -2),
#     'chilly': (19, -1),
#     'good': (20, 0),
#     'warm': (22, -1),
#     'hot': (24, -2)
# }

def update_room_status(room, sensor, validated_data):
    '''Determine room availability based on the latest measurements.
    Also, update the latest measurement data of the room object.
    '''
    
    if not (room and sensor and validated_data):
        print 'ERROR: no room | no sensor | validated_data'
        sys.stdout.flush()
        return
    
    sensor_type = sensor.sensor_type
    if sensor.sensor_type == 'motion':
        detected = validated_data.get('detected')
        if detected == room.available:
            room.available = not detected
            room.save()
            
            #Push notification about availability
            push_notification_available(room)
    
    elif sensor.sensor_type == 'co2':
        room.co2 = validated_data.get('concentration')
        room.save()

        if room.co2 >= CO2_LEVEL['drowsy'][0]:
            #Push notification about air quality
            push_notification_air_quality(room)
        
    elif sensor.sensor_type == 'temperature':
        room.temperature = validated_data.get('temperature')
        room.save()
        
    elif sensor.sensor_type == 'humidity':
        room.humidity = validated_data.get('humidity')
        room.save()

def push_notification_available(room):
    '''Push notification to Android when the status has changed.
    '''
    try:
        print 'Sending push notification to /topics/' + str(room.pk) + '.'
        sys.stdout.flush()
        
    	GCMMessage().send({'notification_type': 'room_availability', 'available': room.available, 'room_id': room.pk},
                          to='/topics/' + str(room.pk))

        print 'Push notification was sent.'
        sys.stdout.flush()
    except:
	print 'Push notification was not sent.'
        sys.stdout.flush()

def push_notification_air_quality(room):
    try:
        #quality = calculate_air_quality(room)
        
        print 'Sending push notification about air quality..' + str(quality)
        sys.stdout.flush()

        GCMMessage().send({'notification_type': 'air_quality', 'room_id': room.pk},
                          to='/topics/' + str(room.pk) + '-quality')

        print 'Push notification was sent.'
        sys.stdout.flush()
    except:
        print 'Push notification was not sent.'
        sys.stdout.flush()

def calculate_air_quality(room):
    pass
