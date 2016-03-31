from django.conf.urls import url, include
from rest_framework_nested import routers
from . import views

# router = routers.DefaultRouter()
# router.register(r'motion', views.MotionSensorView)
# router.register(r'temperaturehumidity', views.TemperatureHumiditySensorView)
# router.register(r'co2', views.CO2SensorView)

# urlpatterns = [
# 	url(r'^', include(router.urls)),
# ]

router = routers.SimpleRouter()
router.register(r'rooms', views.RoomView)
router.register(r'sensors', views.SensorView)
router.register(r'measurements', views.CO2View)

room_router = routers.NestedSimpleRouter(router, r'rooms', lookup='room')
room_router.register(r'co2', views.CO2View)
room_router.register(r'motion', views.MotionView)
room_router.register(r'temperature', views.TemperatureView)
room_router.register(r'humidity', views.HumidityView)

urlpatterns = [
    url(r'^', include(router.urls)),
    url(r'^', include(room_router.urls)),
]
