import os
from locust import HttpUser, task, between

class EcommerceLoadTest(HttpUser):
    wait_time = between(1, 5)  # Wait time between tasks

    # Define the base host for the API Gateway, reading from an environment variable
    # If the variable is not set, default to http://api-gateway:8080/
    host = os.getenv("LOCUST_HOST", "http://api-gateway:8080/")

    @task
    def get_users(self):
        self.client.get("/user-service/api/users")

    @task
    def get_products(self):
        self.client.get("/product-service/api/products")

    @task
    def get_categories(self):
        self.client.get("/product-service/api/categories")

    @task
    def get_orders(self):
        self.client.get("/order-service/api/orders")

    @task
    def get_credentials(self):
        self.client.get("/user-service/api/credentials")

    @task
    def get_payments(self):
        self.client.get("/payment-service/api/payments")

    @task
    def get_shippings(self):
        self.client.get("/shipping-service/api/shippings")

    @task
    def get_favourites(self):
        self.client.get("/favourite-service/api/favourites")