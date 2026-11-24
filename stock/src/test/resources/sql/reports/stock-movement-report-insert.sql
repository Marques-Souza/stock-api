insert into users (id, email, password, profile)
values ('f10e8400-e29b-41d4-a716-446655440001', 'report-admin@gmail.com', '$2a$12$.3nyflpxjA93BXc/VhkTAezhJTJj0wbUsNUaOBnQ/OGBtb6Q/jeie', 'ADMIN');

insert into users (id, email, password, profile)
values ('f10e8400-e29b-41d4-a716-446655440002', 'report-attendant@gmail.com', '$2a$12$.3nyflpxjA93BXc/VhkTAezhJTJj0wbUsNUaOBnQ/OGBtb6Q/jeie', 'ATTENDANT');

insert into categories (id, name, description, image_url)
values ('f10e8400-e29b-41d4-a716-446655440003', 'Categoria relatório', 'Categoria para testes de relatório', 'categories/report.png');

insert into suppliers (id, name, cnpj, email, phone)
values ('f10e8400-e29b-41d4-a716-446655440004', 'Fornecedor relatório', '11443377000181', 'report.supplier@test.com', '1133334444');

insert into clients (id, name, cnpj, email, phone)
values ('f10e8400-e29b-41d4-a716-446655440005', 'Cliente relatório', '26881665000145', 'report.client@test.com', '11988887777');

insert into products (id, name, description, category_id, code, quantity, unit_price, minimum_stock, image_url)
values ('f10e8400-e29b-41d4-a716-446655440006', 'Produto relatório', 'Produto usado nos testes do relatório', 'f10e8400-e29b-41d4-a716-446655440003', 'REPORT-001', 8, 89.90, 1, 'products/report.png');

insert into stock_movements (id, product_id, type, quantity, unit_price, note, supplier_id, client_id, movement_code, created_at, created_by)
values ('f10e8400-e29b-41d4-a716-446655440007', 'f10e8400-e29b-41d4-a716-446655440006', 'ENTRY', 5, 89.90, 'Entrada para relatório', 'f10e8400-e29b-41d4-a716-446655440004', null, '202601-900001', TIMESTAMP '2026-01-01 10:00:00', 'report-admin@gmail.com');

insert into stock_movements (id, product_id, type, quantity, unit_price, note, supplier_id, client_id, movement_code, created_at, created_by)
values ('f10e8400-e29b-41d4-a716-446655440008', 'f10e8400-e29b-41d4-a716-446655440006', 'EXIT', 2, 89.90, 'Saida para relatório', null, 'f10e8400-e29b-41d4-a716-446655440005', '202601-900002', TIMESTAMP '2026-01-02 10:00:00', 'report-attendant@gmail.com');
