from locust import HttpLocust, TaskSet, task

class UserBehavior(TaskSet):
    @task(1)
    def hello(self):
        headerDict = {'Host': 'airlines.cooksys.com'}
        self.client.get("http://edge.platform.cooksys.com/airlines/icao/GNL",headers=headerDict)
        self.client.get("http://edge.platform.cooksys.com/airlines/icao/GNL",headers=headerDict)
        self.client.get("http://edge.platform.cooksys.com/airports/icao/AYGA",headers=headerDict)
        self.client.get("http://edge.platform.cooksys.com/airports/icao/AYGA",headers=headerDict)


class WebsiteUser(HttpLocust):
    task_set = UserBehavior

    min_wait = 1000
    max_wait = 2000
