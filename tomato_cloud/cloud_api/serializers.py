from rest_framework import serializers as s
import models

# class MotionSerializer(serializers.HyperlinkedModelSerializer):
#   time = serializers.DateTimeField(required=False)
#   class Meta:
#     model = models.MotionSensor
#     fields = ('room', 'time', 'motion_detected')

# class TemperatureHumiditySerializer(serializers.HyperlinkedModelSerializer):
#   time = serializers.DateTimeField(required=False)
#   class Meta:
#     model = models.TemperatureHumiditySensor
#     fields = ('room', 'time', 'temperature', 'humidity')

# class CO2Serializer(serializers.HyperlinkedModelSerializer):
#   time = serializers.DateTimeField(required=False)
#   class Meta:
#     model = models.CO2Sensor
#     fields = ('room', 'time', 'concentration')

class RoomSerializer(s.HyperlinkedModelSerializer):
  class Meta:
    model = models.Room

class SensorSerializer(s.HyperlinkedModelSerializer):
  class Meta:
    model = models.Sensor

class CO2Serializer(s.HyperlinkedModelSerializer):
  class Meta:
    model = models.CO2Measurement

class MotionSerializer(s.HyperlinkedModelSerializer):
  class Meta:
    model = models.MotionMeasurement

class TemperatureSerializer(s.HyperlinkedModelSerializer):
  class Meta:
    model = models.TemperatureMeasurement

class HumiditySerializer(s.HyperlinkedModelSerializer):
  class Meta:
    model = models.HumidityMeasurement
