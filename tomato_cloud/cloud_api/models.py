from django.db import models

class MotionSensor(models.Model):
  room = models.CharField(max_length=100)
  time = models.DateTimeField()
  motion_detected = models.BooleanField()

class TemperatureHumiditySensor(models.Model):
  room = models.CharField(max_length=100)
  time = models.DateTimeField()
  temperature = models.FloatField()
  humidity = models.FloatField()

class CO2Sensor(models.Model):
  room = models.CharField(max_length=100)
  time = models.DateTimeField()
  concentration = models.IntegerField()
