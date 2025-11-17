CREATE OR REPLACE FUNCTION deleteOldAppEvents
RETURN NUMBER
IS
    CURSOR cur_event IS
        SELECT eventID
        FROM LogEvent
        WHERE logTimeStamp < SYSTIMESTAMP - INTERVAL '3' MONTH;

    v_deleted_count NUMBER := 0;
BEGIN
    FOR event IN cur_event LOOP
        DELETE FROM LogEvent
        WHERE eventID = event.eventID;

        v_deleted_count := v_deleted_count + 1;
    END LOOP;

    RETURN v_deleted_count;
END;