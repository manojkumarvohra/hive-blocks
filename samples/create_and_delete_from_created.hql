use default;

CREATE TABLE IF NOT EXISTS default.blocks_hive(
  name varchar(10))
CLUSTERED BY (name) INTO 1 BUCKETS
STORED AS ORC 
TBLPROPERTIES ('transactional'='true');

DELETE FROM default.blocks_hive;