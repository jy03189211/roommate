# Cloud REST API

## Installation

- Python `2.7.6`
- Django `1.9.2`
- Django REST framework `3.3.2`
- Heroku (http://tomato-1230.herokuapp.com)

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

The Django project `tomato_cloud` has a root path `/api/v1/`.

### Rooms
```sh
//list rooms
GET /api/v1/rooms

//add new room
POST /api/v1/rooms
```

### Room
```sh
//return room data by id
GET /api/v1/rooms/<id>
```

### Sensors
```sh
//list sensors
GET /api/v1/sensors

//add new sensor
POST /api/v1/sensors
```

### Sensor
```sh
//return sensor data
GET /api/v1/sensors/<id>
```

### Sensor data
```sh
//return sensor data by room id and sensor type
GET /api/v1/rooms/<room_id>/{motion,co2,temperature,humidity}
```

## Heroku

```sh
git subtree push --prefix tomato_cloud https://git.heroku.com/tomato-1230.git master
```

