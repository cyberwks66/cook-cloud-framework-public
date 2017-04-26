# Locust file for generating load to hello-world
#
# To install locust, follw the guide at http://docs.locust.io/en/latest/installation.html
#
# To run a basic load test, cd to the directory where this file is located, and run the command:
#
#    locust --host=http://<routerHost>:<routerPort>
#
# Point your browser to http://127.0.0.1:8089 - here you will enter the number of users and ramp-up time, then start a test
#
#

from locust import HttpLocust, TaskSet, task

class UserBehavior(TaskSet):
    @task(1)
    def hello(self):
        headerDict = {'Host': 'hello-world.cooksys.com:8764'}
        self.client.get("/hello",headers=headerDict)


class WebsiteUser(HttpLocust):
    task_set = UserBehavior

    min_wait = 1000
    max_wait = 2000
