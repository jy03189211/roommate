from django.db import models

class Room(models.Model):
  name = models.CharField(max_length=30)
  organization = models.CharField(max_length=30)
  location = models.CharField(max_length=30)
  size = models.IntegerField()
  available = models.BooleanField(default=False)

  def __str__(self):
    return self.name

class Sensor(models.Model):
  sensor_type = models.CharField(max_length=30)
  room = models.ForeignKey(Room)

  def __str__(self):
    return self.sensor_type + " (" + self.room.name + ")"

class Measurement(models.Model):
  timestamp = models.DateTimeField()
  sensor = models.ForeignKey(Sensor)

  class Meta:
    abstract = True

class CO2Measurement(Measurement):
  concentration = models.IntegerField()

class MotionMeasurement(Measurement):
  detected = models.BooleanField()

class TemperatureMeasurement(Measurement):
  temperature = models.IntegerField()

class HumidityMeasurement(Measurement):
  humidity = models.IntegerField()
