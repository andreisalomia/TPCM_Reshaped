CREATE OR REPLACE PROCEDURE partitionTransactions
IS
    next_month_start   DATE;
    next_month_end     DATE;
    part_name          VARCHAR2(64);
    partition_exists   NUMBER;
BEGIN
    next_month_start := TRUNC(ADD_MONTHS(SYSDATE, 1), 'MM');
    next_month_end   := ADD_MONTHS(next_month_start, 1);

    part_name := 'PART_' || TO_CHAR(next_month_start, 'YYYY_MM');

    SELECT COUNT(*) INTO partition_exists
    FROM user_tab_partitions
    WHERE table_name = 'TRANSACTION'
      AND partition_name = part_name;

    IF partition_exists = 0 THEN
        EXECUTE IMMEDIATE '
            ALTER TABLE Transaction ADD PARTITION ' || part_name || '
            VALUES LESS THAN (TO_DATE(''' || TO_CHAR(next_month_end, 'YYYY-MM-DD') || ''', ''YYYY-MM-DD''))';
    END IF;
END;

-- alter table Transaction add partition PART_2020_01 values less than (to_date('2020-02-01', 'YYYY-MM-DD'));
-- de ce merge cu ''' in loc de ''?

-- testing
EXEC partitionTransactions;

SELECT partition_name, high_value
FROM user_tab_partitions
WHERE table_name = 'TRANSACTION';
