CREATE OR REPLACE FUNCTION deleteUncommittedTransactions
RETURN NUMBER
IS
    CURSOR cursor_transactions IS
        SELECT transactionID, subscriberID
        FROM Transaction
        WHERE status = 'UNCOMMITTED'
        AND createdDate < SYSTIMESTAMP - INTERVAL '24' HOUR;

    v_deleted_count NUMBER := 0;
BEGIN
    FOR trans IN cursor_transactions LOOP
        INSERT INTO LogEvent (eventType, transactionID, logTimeStamp, subscriberID, detailedEvent
        ) VALUES (
            'TRANSACTION_DELETED',
            trans.transactionID,
            SYSTIMESTAMP,
            trans.subscriberID,
            'Uncommitted transaction deleted after 24 hours'
        );

        DELETE FROM Transaction
        WHERE transactionID = trans.transactionID;

        v_deleted_count := v_deleted_count + 1;
    END LOOP;

    RETURN v_deleted_count;
END;
