CREATE OR REPLACE PROCEDURE resetThreshold IS
BEGIN
  UPDATE Limit
  SET consumedAmount = 0, lastReset = SYSTIMESTAMP
  WHERE subscriberID IN (
    SELECT s.subscriberID
    FROM Subscriber s
    INNER JOIN Customer c on s.customerID = c.customerID
    WHERE c.billCycleDay = EXTRACT(DAY FROM SYSDATE) AND
          s.status = 'ACTIVE'
  );
END;
