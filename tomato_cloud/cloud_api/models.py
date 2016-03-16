from django.db import models

# class MotionSensor(models.Model):
#   room = models.CharField(max_length=100)
#   time = models.DateTimeField()
#   motion_detected = models.BooleanField()

# class TemperatureHumiditySensor(models.Model):
#   room = models.CharField(max_length=100)
#   time = models.DateTimeField()
#   temperature = models.FloatField()
#   humidity = models.FloatField()

# class CO2Sensor(models.Model):
#   room = models.CharField(max_length=100)
#   time = models.DateTimeField()
#   concentration = models.IntegerField()

class Room(models.Model):
  name = models.CharField(max_length=30)
  organization = models.CharField(max_length=30)
  location = models.CharField(max_length=30)
  size = models.IntegerField()

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
