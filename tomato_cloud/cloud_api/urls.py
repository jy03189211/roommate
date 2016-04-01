from django.conf.urls import url, include
from rest_framework_nested import routers
from . import views

router = routers.SimpleRouter()
router.register(r'rooms', views.RoomView)
router.register(r'sensors', views.SensorView)
router.register(r'measurements/co2', views.CO2View)
router.register(r'measurements/motion', views.MotionView)
router.register(r'measurements/temperature', views.TemperatureView)
router.register(r'measurements/humidity', views.HumidityView)

room_router = routers.NestedSimpleRouter(router, r'rooms', lookup='room')
room_router.register(r'co2', views.CO2View, base_name='room-co2')
room_router.register(r'motion', views.MotionView, base_name='room-motion')
room_router.register(r'temperature', views.TemperatureView, base_name='room-temperature')
room_router.register(r'humidity', views.HumidityView, base_name='room-humidity')

urlpatterns = [
    url(r'^', include(router.urls)),
    url(r'^', include(room_router.urls)),
]
