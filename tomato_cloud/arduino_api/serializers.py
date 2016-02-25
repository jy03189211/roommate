from rest_framework import serializers
import models

class MotionSerializer(serializers.HyperlinkedModelSerializer):
  time = serializers.DateTimeField(required=False)
  class Meta:
    model = models.MotionSensor
    fields = ('room', 'time', 'motion_detected')

class TemperatureHumiditySerializer(serializers.HyperlinkedModelSerializer):
  time = serializers.DateTimeField(required=False)
  class Meta:
    model = models.TemperatureHumiditySensor
    fields = ('room', 'time', 'temperature', 'humidity')

class CO2Serializer(serializers.HyperlinkedModelSerializer):
  time = serializers.DateTimeField(required=False)
  class Meta:
    model = models.CO2Sensor
    fields = ('room', 'time', 'concentration')
