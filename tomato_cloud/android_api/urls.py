from django.conf.urls import patterns, url, include
from rest_framework import routers

import views

router = routers.DefaultRouter()
router.register(r'sensors/motion', views.MotionSensorView)

urlpatterns = patterns('',
	url(r'^', include(router.urls)),
	url(r'^$', views.index, name='index')
)
