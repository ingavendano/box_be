INSERT IGNORE INTO tracking_statuses (code, name, description, sort_order, active) VALUES 
('RECEIVED', 'Recibido en bodega', 'Paquete recibido en nuestras instalaciones', 1, 1),
('IN_TRANSIT', 'En camino', 'En tránsito (Aéreo/Marítimo)', 2, 1),
('IN_CUSTOMS', 'En aduanas', 'En proceso de liberación aduanal', 3, 1),
('READY_PICKUP', 'Listo para retirar', 'Listo para retirar o en ruta de entrega final', 4, 1),
('DELIVERED', 'Entregado', 'Paquete entregado al cliente', 5, 1),
('CANCELLED', 'Cancelado', 'Envío cancelado', 6, 1);
