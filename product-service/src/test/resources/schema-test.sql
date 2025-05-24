-- Drop existing tables
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;

-- Create tables for testing
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_title VARCHAR(255) NOT NULL,
    image_url VARCHAR(255),
    parent_category_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_category_id) REFERENCES categories (category_id)
);

CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_title VARCHAR(255) NOT NULL,
    image_url VARCHAR(255),
    sku VARCHAR(50) UNIQUE,
    price_unit DOUBLE NOT NULL,
    quantity INT DEFAULT 0,
    category_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories (category_id)
);
