insert into ROLE (UUID,ROLE_ID,ROLE_NAME, DESCRIPTION) values (RANDOM_UUID(),'211','USER', '一般使用者');
insert into ROLE (UUID,ROLE_ID,ROLE_NAME, DESCRIPTION) values (RANDOM_UUID(),'222','VIP', '特殊使用者');
insert into ROLE (UUID,ROLE_ID,ROLE_NAME, DESCRIPTION) values (RANDOM_UUID(),'233','ADMIN', '管理員');

insert into EMP(UUID,EMP_ID,EMP_NAME,PASSWORD) values (RANDOM_UUID(),RANDOM_UUID(),'user','pwd');
insert into EMP(UUID,EMP_ID,EMP_NAME,PASSWORD) values (RANDOM_UUID(),RANDOM_UUID(),'vip','pwd');
insert into EMP(UUID,EMP_ID,EMP_NAME,PASSWORD) values (RANDOM_UUID(),RANDOM_UUID(),'admin','pwd');



insert into EMP_ROLE(UUID, EMP_ID, ROLE_ID) 
values (RANDOM_UUID(),(select EMP_ID from EMP where EMP_NAME = 'user'), (select ROLE_ID from ROLE where ROLE_NAME = 'USER'));
insert into EMP_ROLE(UUID, EMP_ID, ROLE_ID) 
values (RANDOM_UUID(),(select EMP_ID from EMP where EMP_NAME = 'vip'), (select ROLE_ID from ROLE where ROLE_NAME = 'USER') );
insert into EMP_ROLE(UUID, EMP_ID, ROLE_ID) 
values (RANDOM_UUID(),(select EMP_ID from EMP where EMP_NAME = 'vip'), (select ROLE_ID from ROLE where ROLE_NAME = 'VIP') );
insert into EMP_ROLE(UUID, EMP_ID, ROLE_ID) 
values (RANDOM_UUID(),(select EMP_ID from EMP where EMP_NAME = 'admin'), (select ROLE_ID from ROLE where ROLE_NAME = 'USER'));
insert into EMP_ROLE(UUID, EMP_ID, ROLE_ID) 
values (RANDOM_UUID(),(select EMP_ID from EMP where EMP_NAME = 'admin'), (select ROLE_ID from ROLE where ROLE_NAME = 'VIP'));
insert into EMP_ROLE(UUID, EMP_ID, ROLE_ID) 
values (RANDOM_UUID(),(select EMP_ID from EMP where EMP_NAME = 'admin'), (select ROLE_ID from ROLE where ROLE_NAME = 'ADMIN'));


