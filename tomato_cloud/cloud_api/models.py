from django.db import models
from django.utils import timezone

class Room(models.Model):
  name = models.CharField(max_length=30)
  organization = models.CharField(max_length=30)
  location = models.CharField(max_length=30)
  size = models.IntegerField()
  available = models.BooleanField(default=False)
  co2 = models.IntegerField(default=999)
  temperature = models.IntegerField(default=25)
  humidity = models.IntegerField(default=30)

  def __str__(self):
    return self.name

class Sensor(models.Model):
  SENSOR_TYPES = (
    ('motion', 'Motion'),
    ('co2', 'CO2'),
    ('temperature', 'Temperature'),
    ('humidity', 'Humidity')
  )
  sensor_type = models.CharField(max_length=30,
                                 choices=SENSOR_TYPES,
                                 default='motion')
  room = models.ForeignKey(Room)

  def __str__(self):
    return self.sensor_type + " (" + self.room.name + ")"

class Measurement(models.Model):
  timestamp = models.DateTimeField(auto_now_add=True)
  sensor = models.ForeignKey(Sensor, default=1)

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
