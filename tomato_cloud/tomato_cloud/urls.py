from django.conf.urls import include, url

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = [
    # Examples:
    # url(r'^$', 'tomato_cloud.views.home', name='home'),
    # url(r'^tomato_cloud/', include('tomato_cloud.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    # url(r'^admin/', include(admin.site.urls)),
    url(r'^api/v1/android/', include('android_api.urls')),
	url(r'^api/v1/arduino/', include('arduino_api.urls')),
]
