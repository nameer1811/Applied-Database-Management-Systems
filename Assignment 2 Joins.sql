#Assignment 2 Joins

#1
SELECT C.customerNumber AS Customer_Number, C.customerName AS Business_Name, CONCAT(C.contactFirstName," ",C.contactLastName) AS Customer_Concat_Name, CONCAT(E.firstName," ",E.lastName) AS Sales_Rep_Name, E.email AS Sales_Rep_Email, E.extension AS Sales_Rep_Phone_Number
FROM classicmodels.customers AS C
JOIN classicmodels.employees AS E ON C.salesRepEmployeeNumber = E.employeeNumber
ORDER BY C.customerNumber ASC;

#2
SELECT CONCAT(E.firstName," ",E.lastName) AS Superior_Name, E.jobTitle AS Superior_Title, CONCAT(EM.firstName," ",EM.lastName) AS Employee_Name, EM.jobTitle AS Employee_Title
FROM classicmodels.employees AS E
INNER JOIN classicmodels.employees AS EM on E.employeeNumber = EM.reportsTo
ORDER BY E.employeeNumber;

#3
SELECT CONCAT(E.firstName," ", E.lastName) As Name, O.addressLine1 AS Address_1, IFNULL(O.addressLine2,"") AS Address_2, O.city AS City, IFNULL(O.state,"") AS State, O.Country AS Country
FROM classicmodels.employees AS E
JOIN classicmodels.offices AS O ON O.officeCode = E.officeCode;

#4
SELECT OD.orderNumber, P.productName, OD.quantityOrdered
FROM classicmodels.orderdetails AS OD
JOIN classicmodels.products AS P ON P.productCode = OD.productCode
WHERE OD.orderNumber IN (SELECT orderNumber 
						FROM classicmodels.orders 
                        WHERE customerNumber IN (SELECT customerNumber 
												FROM classicmodels.customers 
                                                WHERE customerName = "Herkku Gifts")); 
                                                
#5
SELECT OD.orderNumber, SUM(OD.quantityOrdered * OD.priceEach)
FROM classicmodels.orderdetails AS OD
JOIN classicmodels.orders AS O ON OD.orderNumber = O.orderNumber
WHERE O.customerNumber IN (SELECT customerNumber 
						FROM classicmodels.customers 
						WHERE customerName = "Herkku Gifts")
GROUP BY OD.orderNumber;

#6
SELECT S.name, (SELECT title FROM university.course WHERE course_id = T.course_ID) AS Course_Title,  T.course_ID, T.grade
FROM university.student AS S
JOIN university.takes AS T ON S.ID = T.ID
WHERE T.course_ID IN (SELECT course_id FROM university.course WHERE dept_name = "Comp. Sci.");

#7
SELECT (SELECT I.name 
		FROM university.instructor AS I 
        WHERE I.ID = T.ID) AS Name, C.course_id, C.title
FROM university.course As C
JOIN university.teaches AS T On C.course_id = T.course_id
WHERE C.dept_name = "Comp. Sci.";

#8
SELECT S.name AS Student_Name, I.name  AS Advisor_Name
FROM university.student AS S
JOIN university.advisor AS A ON A.s_ID = S.ID
JOIN university.instructor AS I ON A.i_ID = I.ID;

SELECT S.name AS Student_Name, (SELECT I.name FROM university.instructor AS I WHERE I.ID = A.i_ID) AS Advisor_Name
FROM university.student AS S
JOIN university.advisor AS A ON A.s_ID = S.ID;

SELECT S.name, I.name 
FROM university.instructor AS I
JOIN advisor AS A ON A.i_ID = I.ID
JOIN student AS S ON S.ID  = A.s_ID 
WHERE I.dept_name = 'Comp. Sci.';



#9
SELECT S.name AS Student_Name, S.ID, S.dept_name 
FROM university.student AS S
LEFT JOIN university.advisor AS A ON A.s_ID = S.ID
WHERE A.s_ID IS NULL;

#10
SELECT COUNT(C.customerNumber) AS 'No Orders'
FROM classicmodels.customers AS C
LEFT JOIN classicmodels.orders AS O ON C.customerNumber = O.customerNumber
WHERE O.customerNumber IS NULL;