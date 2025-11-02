CREATE TABLE IF NOT EXISTS sales (
    id SERIAL PRIMARY KEY,
    order_id BIGINT,
    total NUMERIC(19,2),
    created_at TIMESTAMP
);


CREATE TABLE IF NOT EXISTS sale_items (
    id SERIAL PRIMARY KEY,
    sale_id BIGINT,
    product_id BIGINT,
    quantity INT,
    price NUMERIC(19,2)
);