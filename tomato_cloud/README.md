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
pip install -r requirements.txt
python manage.py makemigrations cloud_api
python manage.py migrate
python manage.py runserver
```

## Structure

The Django project `tomato_cloud` has a root path `/api/v1/rooms`.

## Heroku

```sh
git subtree push--prefix tomato_cloud heroku master
```

