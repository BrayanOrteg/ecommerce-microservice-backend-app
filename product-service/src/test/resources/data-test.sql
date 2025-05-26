-- Limpiar datos existentes antes de insertar datos de prueba
DELETE FROM products;
DELETE FROM categories;

-- Insertar datos de prueba para categorías
INSERT INTO categories (category_id, category_title, image_url, parent_category_id) VALUES
(1, 'Electronics', 'http://example.com/electronics.jpg', NULL),
(2, 'Clothing', 'http://example.com/clothing.jpg', NULL),
(3, 'Books', 'http://example.com/books.jpg', NULL),
(4, 'Smartphones', 'http://example.com/smartphones.jpg', 1),
(5, 'Laptops', 'http://example.com/laptops.jpg', 1);

-- Insertar datos de prueba para productos
INSERT INTO products (product_id, product_title, image_url, sku, price_unit, quantity, category_id) VALUES
(1, 'iPhone 13', 'http://example.com/iphone13.jpg', 'IPHONE13', 999.99, 10, 4),
(2, 'Samsung Galaxy S21', 'http://example.com/galaxys21.jpg', 'GALAXYS21', 899.99, 15, 4),
(3, 'MacBook Pro', 'http://example.com/macbook.jpg', 'MACBOOKPRO', 1999.99, 5, 5),
(4, 'Dell XPS 15', 'http://example.com/dellxps.jpg', 'DELLXPS15', 1499.99, 8, 5),
(5, 'T-Shirt', 'http://example.com/tshirt.jpg', 'TSHIRT01', 19.99, 50, 2),
(6, 'Programming Java', 'http://example.com/javaprog.jpg', 'JAVAPROG', 49.99, 20, 3);
