# Cloud REST API

## Installation

- Python `2.7.*`
- Django `1.5.12`
- Django REST framework `3.2.3`
- `https://console.cloud.google.com/home/dashboard?project=tomato-1230`

```sh
pip install django==1.5.12
pip install djangorestframework==3.2.3
```

If the environment is not working, then try

```sh
pip install markdown
pip install django-filter
```

## Running

```sh
python manage.py syncdb
python manage.py runserver
```

## Structure

The Django project `tomato_cloud` consists of two API apps, `android_api` and `arduino_api`. These apps share a common root path `/api/v1`. The root of `android_api` is `/api/v1/android` and the root of `/api/v1/arduino`.

## Remote

`tomato-1230.appspot.com/api/v1/{android,arduino}`
