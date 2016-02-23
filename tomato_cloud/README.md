# Cloud REST API

## Setup

- Python 2.7.*
- Django 1.5
- ( Django REST framework (v3) )
- `https://console.cloud.google.com/home/dashboard?project=tomato-1230`

## Structure

The Django project `tomato_cloud` consists of two API apps, `android_api` and `arduino_api`. These apps share a common root path `/api/v1`. The root of `android_api` is `/api/v1/android` and the root of `/api/v1/arduino`.

## Remote

`tomato-1230.appspot.com/api/v1/{android,arduino}`
