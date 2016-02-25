# Cloud REST API

## Installation

- Python `2.7.6`
- Django `1.5.12`
- Django REST framework `3.2.3`
- Heroku

```sh
pip install virtualenv
virtualenv tomato-cloud
source tomato-cloud/bin/activate
pip install -r requirements.txt
```

## Local

```sh
python manage.py syncdb
python manage.py runserver
```

## Structure

The Django project `tomato_cloud` consists of two API apps, `android_api` and `arduino_api`. These apps share a common root path `/api/v1`. The root of `android_api` is `/api/v1/android` and the root of `arduino_api` is `/api/v1/arduino`.

## Heroku

```sh
git subtree push--prefix tomato_cloud heroku master
```

`http://tomato-1230.herokuapp.com/api/v1/{android,arduino}`

