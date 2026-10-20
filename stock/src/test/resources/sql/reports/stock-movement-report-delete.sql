delete from stock_movements
where id in ('f10e8400-e29b-41d4-a716-446655440007', 'f10e8400-e29b-41d4-a716-446655440008');

delete from products
where id = 'f10e8400-e29b-41d4-a716-446655440006';

delete from clients
where id = 'f10e8400-e29b-41d4-a716-446655440005';

delete from suppliers
where id = 'f10e8400-e29b-41d4-a716-446655440004';

delete from categories
where id = 'f10e8400-e29b-41d4-a716-446655440003';

delete from users
where id in ('f10e8400-e29b-41d4-a716-446655440001', 'f10e8400-e29b-41d4-a716-446655440002');
