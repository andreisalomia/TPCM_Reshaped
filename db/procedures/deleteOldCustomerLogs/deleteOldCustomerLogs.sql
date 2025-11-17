CREATE OR REPLACE FUNCTION deleteOldCustomerLogs
RETURN NUMBER
IS
    CURSOR cur_log IS
        SELECT logID, customerID
        FROM CustomerLogs
        WHERE timeOfChange < SYSTIMESTAMP - INTERVAL '3' MONTH;

    v_deleted_count NUMBER := 0;
BEGIN
    FOR log IN cur_log LOOP
        DELETE FROM CustomerLogs
        WHERE logID = log.logID;
        
        v_deleted_count := v_deleted_count + 1;
    END LOOP;

    RETURN v_deleted_count;
END;