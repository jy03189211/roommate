from django.core.management.base import BaseCommand, CommandError
from cloud_api.models import *

class Command(BaseCommand):
    help = 'Resets measurement tables'

    def handle(self, *args, **options):
        CO2Measurement.objects.all().delete()
        MotionMeasurement.objects.all().delete()
        TemperatureMeasurement.objects.all().delete()
        HumidityMeasurement.objects.all().delete()
    	
