INSERT INTO `category` VALUES 
  (1,'Education'),
  (2,'Medical'),
  (3,'Information Technology');
INSERT INTO `job` VALUES 
  (1, 'bao ve', 5000000.00, 'benh vien da nang', 'abc', b'0', 'ielts 9.0', 'da nang'),
  (2, 'giao vien', 10000000.00, 'high school', 'bcd', b'0', '5 nam kinh nghiem', 'da nang');
INSERT INTO `categoryofjob` VALUES 
  (1, 1, 1), -- Job 1 thuộc Category 1 (Education)
  (2, 2, 2); -- Job 2 thuộc Category 2 (Medical)
INSERT INTO `user` VALUES 
(1, 'ahihi', 'user01', '1234567', 1),
(2, 'alo alo', 'user01', '123456', 2),
(3, 'super idol', 'admin01', '1234567', 3);
INSERT INTO `usersavejob` VALUES (1, 1, 1);
INSERT INTO `userapplyjob` VALUES (2,1,1,'facebook.com',1);
