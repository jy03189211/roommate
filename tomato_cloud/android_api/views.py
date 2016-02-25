from django.http import HttpResponse
from rest_framework import viewsets

from arduino_api import models
from arduino_api import serializers

def index(request):
	return HttpResponse('Hello world!')

class MotionSensorView(viewsets.ModelViewSet):
	queryset = models.MotionSensor.objects.all()
	serializer_class = serializers.MotionSerializer
