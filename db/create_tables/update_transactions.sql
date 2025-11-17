ALTER TABLE Transaction ADD msisdn VARCHAR2(20);

ALTER TABLE Transaction DROP COLUMN isCounted;
ALTER TABLE Transaction ADD partialReservation CHAR(1) DEFAULT 'N' CHECK (partialReservation IN ('Y', 'N'));

CREATE INDEX idx_transaction_msisdn ON Transaction(msisdn);

UPDATE Transaction t
SET t.msisdn = (
    SELECT s.MSISDN 
    FROM Subscriber s 
    WHERE s.subscriberID = t.subscriberID
)
WHERE t.msisdn IS NULL;