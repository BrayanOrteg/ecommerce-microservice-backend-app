-- Limpiar datos de prueba después de cada test
-- Esto mantiene la base de datos limpia entre tests sin afectar el esquema

-- Deshabilitar temporalmente las restricciones de foreign key (H2 syntax)
SET REFERENTIAL_INTEGRITY FALSE;

-- Limpiar todas las tablas
DELETE FROM products;
DELETE FROM categories;

-- Reiniciar secuencias/auto_increment para IDs consistentes (H2 syntax)
ALTER TABLE products ALTER COLUMN product_id RESTART WITH 1;
ALTER TABLE categories ALTER COLUMN category_id RESTART WITH 1;

-- Rehabilitar las restricciones de foreign key
SET REFERENTIAL_INTEGRITY TRUE;
