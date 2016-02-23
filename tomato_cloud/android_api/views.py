from django.http import HttpResponse
from rest_framework import viewsets

import serializers
import models

# Create your views here.
def index(request):
	return HttpResponse('Hello world!')

class MotionSensorView(viewsets.ModelViewSet):
	queryset = models.MotionSensorModel.objects.all()
	serializer_class = serializers.MotionSensorSerializer