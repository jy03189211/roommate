from django.http import HttpResponse
from django.utils import timezone
from rest_framework import viewsets
from rest_framework.response import Response
from rest_framework.decorators import detail_route
import models, serializers


# class MotionSensorView(viewsets.ModelViewSet):
#   queryset = models.MotionSensor.objects.all()
#   serializer_class = serializers.MotionSerializer

#   def perform_create(self, serializer):
#     serializer.save(time=timezone.now())


# class TemperatureHumiditySensorView(viewsets.ModelViewSet):
#   queryset = models.TemperatureHumiditySensor.objects.all()
#   serializer_class = serializers.TemperatureHumiditySerializer

#   def perform_create(self, serializer):
#     serializer.save(time=timezone.now())


# class CO2SensorView(viewsets.ModelViewSet):
#   queryset = models.CO2Sensor.objects.all()
#   serializer_class = serializers.CO2Serializer

#   def perform_create(self, serializer):
#     serializer.save(time=timezone.now())

class RoomView(viewsets.ModelViewSet):
  queryset = models.Room.objects.all()
  serializer_class = serializers.RoomSerializer
  
class SensorView(viewsets.ModelViewSet):
  queryset = models.Sensor.objects.all()
  serializer_class = serializers.SensorSerializer

  # def list(self, request, room_pk=None):
  #   queryset = self.queryset.filter(room=room_pk)
  #   serializer = serializers.SensorSerializer(queryset, many=True)
  #   return Response(serializer.data)

  # def retrieve(self, request, pk=None, room_pk=None):
  #   queryset = self.queryset.get(pk=pk, room=room_pk)
  #   serializer = serializers.SensorSerializer(queryset)
  #   return Response(serializer.data)

# class MeasurementView(viewsets.ModelViewSet):
#   co2_query = models.CO2Measurement.objects.all()
#   motion_query = models.MotionMeasurement.objects.all()
#   temperature_query = models.TemperatureMeasurement.objects.all()
#   humidity_query = models.HumidityMeasurement.objects.all()

#   queryset = co2_query | motion_query | temperature_query | humidity_query

class CO2View(viewsets.ModelViewSet):
  queryset = models.CO2Measurement.objects.all()
  serializer_class = serializers.CO2Serializer

class MotionView(viewsets.ModelViewSet):
  queryset = models.MotionMeasurement.objects.all()
  serializer_class = serializers.MotionSerializer

class TemperatureView(viewsets.ModelViewSet):
  queryset = models.TemperatureMeasurement.objects.all()
  serializer_class = serializers.TemperatureSerializer

class HumidityView(viewsets.ModelViewSet):
  queryset = models.HumidityMeasurement.objects.all()
  serializer_class = serializers.HumiditySerializer
