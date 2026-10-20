delete from stock_movements
where product_id = '4c5d6e7f-8091-4abc-def0-123456789abc';

delete from products
where id = '4c5d6e7f-8091-4abc-def0-123456789abc';

delete from clients
where id = '8a5b6c7d-8e9f-4012-abcd-ef0123456789';

delete from suppliers
where id = '7a5b6c7d-8e9f-4012-abcd-ef0123456789';

delete from categories
where id = '2a1b2c3d-4e5f-6789-abcd-ef0123456789';

delete from users
where id in ('660e8400-e29b-41d4-a716-446655440000', 'a47ac10b-58cc-4372-a567-0e02b2c3d479');
