--changeset andrey:002-add-payload-and-index
ALTER TABLE orders ADD COLUMN metadata JSONB;

-- Используем специфичный для PostgreSQL GIN-индекс для JSONB
CREATE INDEX idx_orders_metadata_gin ON orders USING gin (metadata);

--rollback DROP INDEX idx_orders_metadata_gin;
--rollback ALTER TABLE orders DROP COLUMN metadata;