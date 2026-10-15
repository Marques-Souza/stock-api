insert into users (id, email, password, profile)
values ('550e8400-e29b-41d4-a716-446655440000', 'support@gmail.com', '$2a$12$.3nyflpxjA93BXc/VhkTAezhJTJj0wbUsNUaOBnQ/OGBtb6Q/jeie', 'ADMIN');

insert into users (id, email, password, profile)
values ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'karla@gmail.com', '$2a$12$.3nyflpxjA93BXc/VhkTAezhJTJj0wbUsNUaOBnQ/OGBtb6Q/jeie', 'ATTENDANT');

insert into categories (id, name, description, image_url)
values ('0a1b2c3d-4e5f-6789-abcd-ef0123456789', 'Eletronicos', 'Aparelhos e acessorios', 'categories/eletronicos.png');

insert into categories (id, name, description, image_url)
values ('1b2c3d4e-5f60-789a-bcde-f0123456789a', 'Moveis', 'Moveis para casa e escritorio', 'categories/moveis.png');

insert into products (id, name, description, category_id, code, quantity, unit_price, minimum_stock, image_url)
values ('2c3d4e5f-6071-489a-bcde-f0123456789a', 'Teclado', 'Teclado USB', '0a1b2c3d-4e5f-6789-abcd-ef0123456789', 'PR-001', 4, 89.90, 2, 'products/teclado.png');

insert into products (id, name, description, category_id, code, quantity, unit_price, minimum_stock, image_url)
values ('3d4e5f60-7182-49ab-cdef-0123456789ab', 'Cadeira', 'Cadeira de escritorio', '1b2c3d4e-5f60-789a-bcde-f0123456789a', 'PR-002', 7, 249.90, 3, 'products/cadeira.png');
