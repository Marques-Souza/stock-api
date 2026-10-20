SELECT
    products.code AS codigo_produto,
    products.name AS nome_produto,
    categories.name AS categoria,
    stock_movements.type AS tipo_movimentacao,
    stock_movements.quantity AS quantidade,
    stock_movements.unit_price AS preco_unitario,
    (stock_movements.quantity * stock_movements.unit_price) AS valor_total,
    stock_movements.movement_code AS codigo_movimentacao,
    stock_movements.note AS observacao,
    suppliers.name AS fornecedor,
    suppliers.cnpj AS cnpj_fornecedor,
    clients.name AS cliente,
    clients.cnpj AS cnpj_cliente,
    stock_movements.created_at AS data_movimentacao,
    stock_movements.created_by AS criado_por
FROM stock_movements
    INNER JOIN products ON stock_movements.product_id = products.id
    INNER JOIN categories ON products.category_id = categories.id
    LEFT JOIN suppliers ON stock_movements.supplier_id = suppliers.id
    LEFT JOIN clients ON stock_movements.client_id = clients.id
WHERE

ORDER BY 
    stock_movements.created_at DESC;
