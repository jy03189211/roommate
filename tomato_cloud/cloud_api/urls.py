from django.conf.urls import url, include
from rest_framework_nested import routers
from . import views

router = routers.SimpleRouter()
router.register(r'rooms', views.RoomView)
router.register(r'sensors', views.SensorView)

room_router = routers.NestedSimpleRouter(router, r'rooms', lookup='room')
room_router.register(r'co2', views.CO2View)
room_router.register(r'motion', views.MotionView)
room_router.register(r'temperature', views.TemperatureView)
room_router.register(r'humidity', views.HumidityView)

urlpatterns = [
    url(r'^', include(router.urls)),
    url(r'^', include(room_router.urls)),
]
