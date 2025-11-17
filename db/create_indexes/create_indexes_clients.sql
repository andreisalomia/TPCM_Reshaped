CREATE INDEX IF NOT EXISTS idx_clients_customer_name ON Customer(name);
CREATE INDEX IF NOT EXISTS idx_clients_customer_phone ON Customer(contactNumber);

CREATE INDEX IF NOT EXISTS idx_clients_subscriber_msisdn ON Subscriber(MSISDN);