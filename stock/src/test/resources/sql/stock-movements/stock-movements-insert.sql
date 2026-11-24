insert into users (id, email, password, profile)
values ('660e8400-e29b-41d4-a716-446655440000', 'stock-admin@gmail.com', '$2a$12$.3nyflpxjA93BXc/VhkTAezhJTJj0wbUsNUaOBnQ/OGBtb6Q/jeie', 'ADMIN');

insert into users (id, email, password, profile)
values ('a47ac10b-58cc-4372-a567-0e02b2c3d479', 'stock-attendant@gmail.com', '$2a$12$.3nyflpxjA93BXc/VhkTAezhJTJj0wbUsNUaOBnQ/OGBtb6Q/jeie', 'ATTENDANT');

insert into categories (id, name, description, image_url)
values ('2a1b2c3d-4e5f-6789-abcd-ef0123456789', 'Categoria de teste', 'Categoria para testes de movimentacao', 'categories/teste.png');

insert into suppliers (id, name, cnpj, email, phone)
values ('7a5b6c7d-8e9f-4012-abcd-ef0123456789', 'Fornecedor de teste', '11443377000181', 'fornecedor.stock@test.com', '1133334444');

insert into clients (id, name, cnpj, email, phone)
values ('8a5b6c7d-8e9f-4012-abcd-ef0123456789', 'Cliente de teste', '26881665000145', 'cliente.stock@test.com', '11988887777');

insert into products (id, name, description, category_id, code, quantity, unit_price, minimum_stock, image_url)
values ('4c5d6e7f-8091-4abc-def0-123456789abc', 'Teclado de teste', 'Teclado usado nos testes de movimentacao', '2a1b2c3d-4e5f-6789-abcd-ef0123456789', 'STOCK-TEST-001', 4, 89.90, 1, 'products/teclado-teste.png');

insert into stock_movements (id, product_id, type, quantity, unit_price, note, supplier_id, client_id, movement_code, created_at, created_by)
values ('9a1b2c3d-4e5f-6789-abcd-ef0123456789', '4c5d6e7f-8091-4abc-def0-123456789abc', 'ENTRY', 5, 89.90, 'Entrada fixture', '7a5b6c7d-8e9f-4012-abcd-ef0123456789', null, '202601-000101', TIMESTAMP '2026-01-01 10:00:00', 'fixture@test.com');

insert into stock_movements (id, product_id, type, quantity, unit_price, note, supplier_id, client_id, movement_code, created_at, created_by)
values ('9b1b2c3d-4e5f-6789-abcd-ef0123456789', '4c5d6e7f-8091-4abc-def0-123456789abc', 'EXIT', 2, 89.90, 'Saida fixture', null, '8a5b6c7d-8e9f-4012-abcd-ef0123456789', '202601-000102', TIMESTAMP '2026-01-02 10:00:00', 'fixture@test.com');
