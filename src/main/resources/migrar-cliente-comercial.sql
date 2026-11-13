ALTER TABLE cliente_has_comercial
    ADD COLUMN fecha DATE NULL AFTER comercial_id,
    ADD COLUMN prioridad INT UNSIGNED NULL AFTER fecha;
