IMPORTANT NOTE: 
Please don’t change the folder structure. All the databases are stored in DBLAB.

I have 7 classes.
Record, Table, Databases, Query, Type, Logic and PrintTable
Record, Table, Databases and Type have test code, you can run them.

Query is where you can try all the extensions.
I support 11 query commands:
SHOW DATABASE; USE; SHOW TABASES; SELECT; JOIN; UPDATE; DELETE; SAVE; CREATE; INSERT; QUIT

Starting Query needs some time, because it contains a very large database(in DBLAB/TEST)

******The Query Command Instruction:**********

First: SHOW DATABASES;

Then: USE NBA; USE TEST; USE PET; //You can change database anytime!

Then: SHOW DATABASES;
In NBA, you can try these code:

SELECT * FROM players;
SELECT ID, Name FROM players WHERE Gender = Male;
SELECT * FROM players JOIN teams ON players.Team = teams.Team;
SELECT * FROM players JOIN teams ON players.Team = teams.Team WHERE ID > 10;
If you use PET, you can also try:
SELECT * FROM animal JOIN human ON animal.Owner = human.Username;
+---------------------------+
|ID|Name    |Kind|Owner|Name|
+---------------------------+
| 1|Fido    |dog |ab123|Jo  |
| 2|Wanda   |fish|ef789|Amy |
| 3|Garfield|cat |ab123|Jo  |
+---------------------------+

INSERT INTO players (ID, Name, Gender, Team) VALUES (35, Durant, Male, GSW);
INSERT INTO players (ID, Name, Gender, Team) VALUES (35, Keley, Male, GSW);
//The last one is invalid! You will get a warning 

CREATE TABLE pets (Name String NOT, ID Integer KEY, Owner String NOT);
//****************field, Type, not a key, field, Type, is a key,field, Type, not a key..
//You must use a database first, or you will got a warning.

UPDATE players SET ID = 25 WHERE ID = 23;
UPDATE players SET Gender = Male WHERE Team = Bristol;

DELETE FROM players WHERE ID > 30;

SAVE players;
****************************************************************
You can try all the invalid input, most time you will get a waring. My query could detect some invalid input(both syntax error or logical).
Typical examples like:

SELECT * FROM ha; ->Warning: Please make sure you choose a right table!
SELECT salary FROM players;->Warning: Your chosen part is invalid!
SELECT * FROM players WHERE ID > ij;->Warning: Your type has error!
SELECT * FROM players WHERE ID = 100;->Warning :No result!
SELECT * FROM players WHERE salary = 999;->Warning: Your field name in comparison is wrong!

INSERT INTO players (ID, Name, Gender, Team) VALUES (23, Jordan, Male, GSW);
->Warning: You can't have two same key if you set a key!

DELETE FROM players WHERE ID * 30;->Warning: Invalid symbol!
***********************************************************************
You can USE TEST to test my data structure.
If you use 
SELECT * FROM test WHERE Name = AqdfJU;
+-------------------+
|ID   |Name  |Gender|
+-------------------+
|99809|AqdfJU|Male  |
+-------------------+
Totally 1 rows
Running time：32ms

If you use 
SELECT * FROM test WHERE ID = 99809;
+-------------------+
|ID   |Name  |Gender|
+-------------------+
|99809|AqdfJU|Male  |
+-------------------+
Totally 1 rows
Running time：0ms

This is because of that I use treeMap. ID is key, Name is not key. O(logN) win!











