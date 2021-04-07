CREATE PROCEDURE `EmployeeLocation` (IN eID INT)
BEGIN
SELECT E.employeeNumber AS ID, CONCAT(E.firstName, " ", E.lastName) AS Name, 
    IFNULL(CONCAT(O.city,", ",O.state,", ", O.country), CONCAT(OFC.city,", ",OFC.country)) AS Location 
    FROM classicmodels.employees AS E 
    JOIN classicmodels.offices AS O ON O.officeCode = E.officeCode WHERE E.employeeNumber = eID;
END
