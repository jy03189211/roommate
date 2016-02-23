from django.db import models

class MotionSensorModel(models.Model):
	room_name = models.CharField(max_length=100)
	timestamp = models.DateTimeField()
	is_empty = models.BooleanField()
