delete from products
where category_id in (
    '0a1b2c3d-4e5f-6789-abcd-ef0123456789',
    '1b2c3d4e-5f60-789a-bcde-f0123456789a'
);

delete from categories
where id in (
    '0a1b2c3d-4e5f-6789-abcd-ef0123456789',
    '1b2c3d4e-5f60-789a-bcde-f0123456789a'
);

delete from users
where id in (
    '550e8400-e29b-41d4-a716-446655440000',
    'f47ac10b-58cc-4372-a567-0e02b2c3d479'
);
