CREATE INDEX IF NOT EXISTS idx_customer_name ON Customer(name);

CREATE INDEX IF NOT EXISTS idx_subscriber_msisdn ON Subscriber(MSISDN);

CREATE INDEX idx_transaction_subscriber ON Transaction(subscriberID);
CREATE INDEX idx_transaction_createdDate ON Transaction(createdDate);
CREATE INDEX idx_transaction_tpid ON Transaction(tpid);