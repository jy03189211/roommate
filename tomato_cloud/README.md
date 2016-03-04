# Cloud REST API

## Installation

- Python `2.7.6`
- Django `1.5.12`
- Django REST framework `3.3.2`
- Heroku

```sh
pip install virtualenv
virtualenv tomato-cloud
source tomato-cloud/bin/activate
pip install -r requirements.txt
```

## Local

```sh
python manage.py makemigrations
python manage.py migrate
python manage.py runserver
```

## Structure

The Django project `tomato_cloud` has a root path `/api/v1`.

## Heroku

```sh
git subtree push--prefix tomato_cloud heroku master
```

`http://tomato-1230.herokuapp.com/api/v1`

### Motion `/api/v1/arduino/motion/`
With `POST` you need to provide two mandatory fields which are `room` and `motion\_detected`. `room` is string with max length of 100 characters and `motion\_detected` is boolean.

### Temperature and Humidity `/api/v1/temperaturehumidity/`
With `POST` you need to provide three mandatory fields which are `room`, `temperature` and `humidity`. `room` is string with max length of 100 characters. `temperature` and `humidity` are both floats.

### CO2 `/api/v1/co2/`
With `POST` you need to provide two mandatory fields which are `room` and `concentration`. `room` is string with max length of 100 characters and `concentration` is integer.

