-- Insert Accounts only if they don't exist
INSERT INTO account (id, account_number, balance, created_at, updated_at, user_id)
SELECT 1, 'ACC123456789', 1000.00, '2024-12-11T00:00:00', '2024-12-11T00:00:00', 2
    WHERE NOT EXISTS (SELECT 1 FROM account WHERE id = 1);

INSERT INTO account (id, account_number, balance, created_at, updated_at, user_id)
SELECT 2, 'ACC987654321', 500.00, '2024-12-11T00:00:00', '2024-12-11T00:00:00', 2
    WHERE NOT EXISTS (SELECT 1 FROM account WHERE id = 2);

-- Insert Cards only if they don't exist
INSERT INTO card (id, card_number, card_type, expiry_date, account_id)
SELECT 1, 'CARD1234567890123456', 'CREDIT', '2026-12-31', 1
    WHERE NOT EXISTS (SELECT 1 FROM card WHERE id = 1 OR account_id = 1);

INSERT INTO card (id, card_number, card_type, expiry_date, account_id)
SELECT 2, 'CARD5475475474577474', 'CREDIT', '2026-12-31', 2
    WHERE NOT EXISTS (SELECT 1 FROM card WHERE id = 2 OR account_id = 2);

INSERT INTO card (id, card_number, card_type, expiry_date, account_id)
SELECT 3, 'CARD9876543210987654', 'DEBIT', '2026-12-31', 2
    WHERE NOT EXISTS (SELECT 1 FROM card WHERE id = 3 OR account_id = 2);
