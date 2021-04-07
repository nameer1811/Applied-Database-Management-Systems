#Assignment 1 Queries
#1
SELECT customerName
FROM classicmodels.customers
ORDER BY customerName;

#2
SELECT firstName, lastName, email
FROM classicmodels.employees
WHERE jobTitle = 'VP Sales';

#3
SELECT customerName, CONCAT(contactFirstName," ", contactLastName) AS "Contact"
FROM classicmodels.customers
WHERE customerName LIKE "%Inc" OR customerName LIKE "%Inc.";

#4
SELECT customerName
FROM classicmodels.customers
WHERE (country = "France" OR country = "Germany") AND (customerName LIKE "%Co." OR customerName LIKE "%Co");

#5
SELECT customerName AS 'Customer_Name', addressLine1 AS 'Address', ifnull(addressLine2, "") AS 'Suite', city AS City, state AS 'State', postalCode AS ZIP_Code
FROM classicmodels.customers
WHERE country = 'USA';

#6
SELECT productName, buyPrice 
FROM classicmodels.products
WHERE buyPrice > 98.00
ORDER BY buyPrice DESC
LIMIT 5;

#7
SELECT productName, quantityInStock, buyPrice
FROM classicmodels.products
WHERE buyPrice = (SELECT MAX(buyPrice) FROM classicmodels.products);

#8
INSERT INTO classicmodels.customers (customerNumber,customerName, contactLastName, contactFirstName, phone, addressLine1, addressLine2, city, state, postalCode, country, creditLimit)
SELECT MAX(customerNumber)+1 ,'Detectives Inc.', 'Holmes', 'Sherlock', '2743824490', '221B Baker Street', 'Apt 2', 'London', 'LL', '50876', 'England', '10000' 
FROM classicmodels.customers;

#9
UPDATE classicmodels.customers
SET creditLimit = 5000
WHERE customerName = 'Detectives Inc.';

#10
DELETE FROM classicmodels.customers WHERE  customerName = 'Detectives Inc.';

#11
SELECT CONCAT(firstName,' ', lastName) AS Name, email
FROM classicmodels.employees AS E,  classicmodels.offices AS O
WHERE E.officeCode = O.officeCode AND city = 'NYC';

#11 (Alternative)
SELECT CONCAT(firstName,' ', lastName) AS Name, email
FROM classicmodels.employees 
WHERE officeCode IN (SELECT officeCode FROM classicmodels.offices WHERE city = 'NYC');

#12
SELECT CONCAT(firstName,' ', lastName) AS Name, email
FROM classicmodels.employees AS E,  classicmodels.offices AS O
WHERE E.officeCode = O.officeCode AND (city = 'Sydney' OR city = 'Paris' OR city = 'London');

#12 (Alternative)
SELECT CONCAT(firstName,' ', lastName) AS Name, email
FROM classicmodels.employees 
WHERE officeCode IN (SELECT officeCode FROM classicmodels.offices WHERE(city = 'Sydney' OR city = 'Paris' OR city = 'London'));

#13
SELECT COUNT(C.customerNumber) AS 'No Orders'
FROM classicmodels.customers AS C
WHERE NOT EXISTS (SELECT * FROM classicmodels.orders AS O WHERE C.customerNumber = O.customerNumber);

#13 (Alternative)
SELECT ((SELECT COUNT(*) FROM classicmodels.customers) -
               (SELECT COUNT(DISTINCT(customerNumber)) FROM classicmodels.orders)) AS 'No Orders';


#14
SELECT DISTINCT CONCAT(firstName,' ', lastName) AS 'Name', extension
FROM classicmodels.employees AS E, classicmodels.customers AS C
WHERE E.employeeNumber = C.salesRepEmployeeNumber AND C.customerNumber IN (SELECT customerNumber FROM classicmodels.orders WHERE status = 'In Process');

#14 (Alternative)
SELECT CONCAT(firstName,' ', lastName) AS 'Name', extension
FROM classicmodels.employees
WHERE employeeNumber IN (SELECT salesRepEmployeeNumber
FROM classicmodels.customers
WHERE salesRepEmployeeNumber IS NOT NULL AND customerNumber IN (SELECT customerNumber
FROM classicmodels.orders
WHERE status = 'In Process'));

#15
SELECT C.customerName AS 'Customer', (SELECT SUM(P.amount)
FROM classicmodels.payments AS P 
WHERE C.customerNumber = P.customerNumber
GROUP BY P.customerNumber) AS 'Total_Payments' 
FROM classicmodels.customers AS C
WHERE C.customerNumber IN (SELECT DISTINCT (customerNumber) FROM classicmodels.payments);

#15 (Alternative)
SELECT(SELECT customerName
       FROM classicmodels.customers AS C
       WHERE C.customerNumber = O.customerNumber) AS 'Customer_Name',
      (SELECT SUM(amount)
       FROM classicmodels.payments AS P
       WHERE P.customerNumber = O.customerNumber GROUP BY P.customerNumber) AS 'Total_Amount'
FROM classicmodels.orders AS O
WHERE customerNumber IN (SELECT customerNumber
                        FROM classicmodels.orders
                        ORDER BY orderNumber)
GROUP BY O.customerNumber;

#16
SELECT orderNumber as 'Order_Num'
FROM classicmodels.orderdetails
GROUP BY orderNumber
HAVING COUNT(productCode) >= 18
ORDER BY orderNumber ASC;

#17
SELECT orderNumber as 'Order_Num', GROUP_CONCAT(productCode SEPARATOR ', ') AS 'Product_Code'
FROM classicmodels.orderdetails
GROUP BY orderNumber
HAVING COUNT(productCode) >= 18
ORDER BY orderNumber ASC;
