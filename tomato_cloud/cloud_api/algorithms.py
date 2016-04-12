from gcm.models import get_device_model
from gcm.api import GCMMessage
import sys

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
            
            #Push notification to Android
            push_notification(room)
    
    elif sensor.sensor_type == 'co2':
        room.co2 = validated_data.get('concentration')
        room.save()
        
    elif sensor.sensor_type == 'temperature':
        room.temperature = validated_data.get('temperature')
        room.save()
        
    elif sensor.sensor_type == 'humidity':
        room.humidity = validated_data.get('humidity')
        room.save()

def push_notification(room):
    '''Push notification to Android when the status has changed.
    '''
    try:
    	Device = get_device_model()
        
	print 'Sending push notification to /topics/' + str(room.pk) + '.'
        sys.stdout.flush()
        
    	GCMMessage().send({'message': room.available}, to='/topics/' + str(room.pk))

        print 'Push notification was sent.'
        sys.stdout.flush()
    except:
	print 'Push notification was not sent.'
        sys.stdout.flush()
