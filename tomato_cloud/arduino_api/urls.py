from django.conf.urls import patterns, url, include
from rest_framework import routers
from . import views

router = routers.DefaultRouter()
router.register(r'motion', views.MotionSensorView)
router.register(r'temperaturehumidity', views.TemperatureHumiditySensorView)
router.register(r'co2', views.CO2SensorView)

urlpatterns = patterns('',
	url(r'^', include(router.urls)),
	url(r'^$', views.index, name='index'))
