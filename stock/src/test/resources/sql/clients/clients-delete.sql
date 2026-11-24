delete from clients
where id in (
    '1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d',
    '2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e',
    '3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f'
);

delete from users
where id in (
    '550e8400-e29b-41d4-a716-446655440000',
    'f47ac10b-58cc-4372-a567-0e02b2c3d479',
    '6ba7b810-9dad-11d1-80b4-00c04fd430c8'
);