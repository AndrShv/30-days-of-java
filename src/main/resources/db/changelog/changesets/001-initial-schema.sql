--changeset andrey:001-create-orders-table
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INT NOT NULL,
    total_amount NUMERIC(12, 2) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_orders_status_created ON orders (status, created_at);

--rollback DROP TABLE orders;