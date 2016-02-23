from rest_framework import serializers

import models

class MotionSensorSerializer(serializers.HyperlinkedModelSerializer):
	class Meta:
		model = models.MotionSensorModel
		fields = ('room_name', 'timestamp', 'is_empty')

