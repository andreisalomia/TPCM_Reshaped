CREATE OR REPLACE FUNCTION deleteOldTransactions
RETURN NUMBER
IS
    CURSOR cur_transactions IS
        SELECT transactionID, subscriberID
        FROM Transaction
        WHERE createdDate < SYSTIMESTAMP - INTERVAL '3' MONTH;

    v_deleted_count NUMBER := 0;
BEGIN
    FOR trans IN cur_transactions LOOP
        INSERT INTO LogEvent (
            eventType, transactionID, logTimeStamp, subscriberID, detailedEvent
        ) VALUES (
            'TRANSACTION_DELETED',
            trans.transactionID,
            SYSTIMESTAMP,
            trans.subscriberID,
            'Transaction deleted after 3 months'
        );

        DELETE FROM Transaction
        WHERE transactionID = trans.transactionID;

        v_deleted_count := v_deleted_count + 1;
    END LOOP;

    RETURN v_deleted_count;
END;
