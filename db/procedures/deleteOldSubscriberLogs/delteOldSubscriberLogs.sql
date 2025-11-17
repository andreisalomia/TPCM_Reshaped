CREATE OR REPLACE FUNCTION deleteOldSubscriberLogs
RETURN NUMBER
IS
    CURSOR cur_log IS
        SELECT logID, subscriberID
        FROM SubscriberLogs
        WHERE timeOfChange < SYSTIMESTAMP - INTERVAL '3' MONTH;

    v_deleted_count NUMBER := 0;
BEGIN
    FOR log IN cur_log LOOP
        DELETE FROM SubscriberLogs
        WHERE logID = log.logID;

        v_deleted_count := v_deleted_count + 1;
    END LOOP;

    RETURN v_deleted_count;
END;