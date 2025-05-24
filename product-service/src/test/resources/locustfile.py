from locust import HttpUser, task, between

class ProductServiceUser(HttpUser):
    wait_time = between(1, 3)  # Tiempo de espera entre solicitudes (1-3 segundos)
    
    def on_start(self):
        # Inicialización del usuario (si es necesario)
        pass
    
    @task(3)
    def get_all_products(self):
        # Obtener todos los productos (peso 3 - tarea más frecuente)
        self.client.get("/api/products")
    
    @task(2)
    def get_product_by_id(self):
        # Obtener un producto específico por ID (peso 2)
        # Simulamos IDs aleatorios entre 1 y 6 (basados en nuestros datos de prueba)
        product_id = self.get_random_product_id()
        self.client.get(f"/api/products/{product_id}")
    
    @task(1)
    def create_product(self):
        # Crear un nuevo producto (peso 1 - menos frecuente)
        payload = {
            "productTitle": f"Test Product {self.environment.runner.user_count}",
            "imageUrl": "http://example.com/test.jpg",
            "sku": f"TEST{self.environment.runner.user_count}",
            "priceUnit": 99.99,
            "quantity": 10,
            "categoryDto": {
                "categoryId": 1,
                "categoryTitle": "Electronics",
                "imageUrl": "http://example.com/category.jpg"
            }
        }
        self.client.post("/api/products", json=payload)
    
    @task(1)
    def update_product(self):
        # Actualizar un producto existente (peso 1)
        product_id = self.get_random_product_id()
        payload = {
            "productId": product_id,
            "productTitle": f"Updated Product {product_id}",
            "imageUrl": "http://example.com/updated.jpg",
            "sku": f"UPDATE{product_id}",
            "priceUnit": 129.99,
            "quantity": 15,
            "categoryDto": {
                "categoryId": 1,
                "categoryTitle": "Electronics",
                "imageUrl": "http://example.com/category.jpg"
            }
        }
        self.client.put("/api/products", json=payload)
    
    @task
    def search_products_by_category(self):
        # Simular búsqueda de productos por categoría
        # Nota: Esta es una URL hipotética, puede que necesite ajustarse según la API real
        category_id = self.get_random_category_id()
        self.client.get(f"/api/categories/{category_id}/products")
    
    def get_random_product_id(self):
        # Método auxiliar para obtener un ID de producto aleatorio
        import random
        return random.randint(1, 6)  # Asumiendo que tenemos 6 productos en la BD de prueba
    
    def get_random_category_id(self):
        # Método auxiliar para obtener un ID de categoría aleatorio
        import random
        return random.randint(1, 5)  # Asumiendo que tenemos 5 categorías en la BD de prueba

# Para ejecutar:
# 1. Instala Locust: pip install locust
# 2. Guarda este archivo como locustfile.py
# 3. Ejecuta: locust -H http://localhost:8500 (o la URL base de tu servicio de productos)
# 4. Abre http://localhost:8089 en el navegador para la interfaz de Locust
# 5. Configura el número de usuarios y la tasa de incremento, luego inicia la prueba
