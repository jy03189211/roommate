from django.http import HttpResponse
from django.utils import timezone
from rest_framework import viewsets
from rest_framework.decorators import detail_route
import models, serializers


class MotionSensorView(viewsets.ModelViewSet):
  queryset = models.MotionSensor.objects.all()
  serializer_class = serializers.MotionSerializer

  def perform_create(self, serializer):
    serializer.save(time=timezone.now())


class TemperatureHumiditySensorView(viewsets.ModelViewSet):
  queryset = models.TemperatureHumiditySensor.objects.all()
  serializer_class = serializers.TemperatureHumiditySerializer

  def perform_create(self, serializer):
    serializer.save(time=timezone.now())


class CO2SensorView(viewsets.ModelViewSet):
  queryset = models.CO2Sensor.objects.all()
  serializer_class = serializers.CO2Serializer

  def perform_create(self, serializer):
    serializer.save(time=timezone.now())
