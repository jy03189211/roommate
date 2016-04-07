from rest_framework import serializers as s
import models
import algorithms as alg

class RoomSerializer(s.HyperlinkedModelSerializer):
  class Meta:
    model = models.Room

class SensorSerializer(s.HyperlinkedModelSerializer):
  class Meta:
    model = models.Sensor

class MetaSensorSerializer(s.HyperlinkedModelSerializer):
  class Meta:
    model = models.Sensor
    exclude = ('url',)
    
  def create(self, validated_data):
    sensor = validated_data.get('sensor')
    room = models.Room.objects.get(sensor=sensor)
    alg.update_room_status(room, sensor, validated_data)    

    return self.Meta.model.objects.create(**validated_data)

class CO2Serializer(MetaSensorSerializer):
  def __init__(self, *args, **kwargs):
    super(CO2Serializer, self).__init__(*args, **kwargs)
    self.Meta.model = models.CO2Measurement

class MotionSerializer(MetaSensorSerializer):
  def __init__(self, *args, **kwargs):
    super(MotionSerializer, self).__init__(*args, **kwargs)
    self.Meta.model = models.MotionMeasurement

class TemperatureSerializer(MetaSensorSerializer):
  def __init__(self, *args, **kwargs):
    super(TemperatureSerializer, self).__init__(*args, **kwargs)
    self.Meta.model = models.TemperatureMeasurement

class HumiditySerializer(MetaSensorSerializer):
  def __init__(self, *args, **kwargs):
    super(HumiditySerializer, self).__init__(*args, **kwargs)
    self.Meta.model = models.HumidityMeasurement
