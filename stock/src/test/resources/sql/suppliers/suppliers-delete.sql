delete from suppliers
where id in (
    '4a5b6c7d-8e9f-0a1b-2c3d-4e5f6a7b8c9d',
    '5b6c7d8e-9f0a-1b2c-3d4e-5f6a7b8c9d0e',
    '6c7d8e9f-0a1b-2c3d-4e5f-6a7b8c9d0e1f'
);

delete from users
where id in (
    '550e8400-e29b-41d4-a716-446655440000',
    'f47ac10b-58cc-4372-a567-0e02b2c3d479',
    '6ba7b810-9dad-11d1-80b4-00c04fd430c8'
);
