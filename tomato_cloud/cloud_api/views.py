from django.http import HttpResponse
from django.utils import timezone
from rest_framework import viewsets
from rest_framework.response import Response
from rest_framework.decorators import detail_route
from rest_framework import filters

import models, serializers

class RoomView(viewsets.ModelViewSet):
  queryset = models.Room.objects.all()
  serializer_class = serializers.RoomSerializer
  
class SensorView(viewsets.ModelViewSet):
  queryset = models.Sensor.objects.all()
  serializer_class = serializers.SensorSerializer

class MetaSensorView(viewsets.ModelViewSet):
  def list(self, request, room_pk=None):
    try:
      room = models.Room.objects.get(pk=room_pk)
      sensor = models.Sensor.objects.get(room=room, sensor_type=self.sensor_type)
      queryset = self.queryset.filter(sensor=sensor).order_by('-timestamp')
      serializer = self.serializer_class(queryset, many=True, context={'request': request})
      return Response(serializer.data)
    except:
      return Response()
      
  def retrieve(self, request, pk=None, room_pk=None):
    try:
      queryset = self.queryset.get(pk=pk)
      serializer = self.serializer_class(queryset, many=True, context={'request': request})
      return Response(serializer.data)
    except:
      return Response()  

class CO2View(MetaSensorView):
  model = models.CO2Measurement
  queryset = model.objects.all()
  sensor_type = 'CO2'
  serializer_class = serializers.CO2Serializer

class MotionView(MetaSensorView):
  model = models.MotionMeasurement
  queryset = model.objects.all()
  sensor_type = 'Motion'
  serializer_class = serializers.MotionSerializer

class TemperatureView(MetaSensorView):
  model = models.TemperatureMeasurement
  queryset = model.objects.all()
  sensor_type = 'Temperature'
  serializer_class = serializers.TemperatureSerializer

class HumidityView(MetaSensorView):
  model = models.HumidityMeasurement
  queryset = model.objects.all()
  sensor_type = 'Humidity'
  serializer_class = serializers.HumiditySerializer
