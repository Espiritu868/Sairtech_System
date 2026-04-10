/*
SQLyog Community v13.1.7 (64 bit)
MySQL - 10.4.32-MariaDB : Database - db_reparaciones
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`db_reparaciones` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `db_reparaciones`;

/*Table structure for table `clientes` */

DROP TABLE IF EXISTS `clientes`;

CREATE TABLE `clientes` (
  `id_cliente` int(11) NOT NULL AUTO_INCREMENT,
  `numero_identidad` varchar(15) DEFAULT NULL,
  `nombre` varchar(75) NOT NULL,
  `apellido` varchar(75) NOT NULL,
  `telefono` varchar(20) NOT NULL CHECK (octet_length(`telefono`) >= 8),
  `correo` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `numero_identidad` (`numero_identidad`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `clientes` */

insert  into `clientes`(`id_cliente`,`numero_identidad`,`nombre`,`apellido`,`telefono`,`correo`) values 
(1,'0000-0000-00000','***','ELIMINADO','********','***'),
(2,'0000-0000-2','***','ELIMINADO','********','***'),
(3,'0000-0000-3','***','ELIMINADO','********','***'),
(4,'0000-0000-4','***','ELIMINADO','********','***'),
(5,'1601200600253','Angel Antonio','Muñoz Borjas','98456123','angelmuoz@gmail.com'),
(6,'1601200200223','Selvin Antonio','Hiraeta Avelar','96863040','selvinhiraetamc@gmail.com'),
(9,'0000-0000-9','***','ELIMINADO','********','***'),
(10,'1601200700212','Daniel Estuardo','Sagastume Rapalo','89518040','daniloborjas@gmail.com'),
(11,'0103197400254','Selvin Edgardo','Hiraeta Avelar','96863040','selvinedgardo@gmail.com'),
(12,'1601198400231','Rosa del Carmen','Ramirez Jimenez','97615243','rosaramirez@gmail.com'),
(13,'1601202200232','SAIRTECH','TECNOLOGIA','89518040','sairtech@gmail.com'),
(14,'1601200500252','Jeymi Sarahi','Chavez Diaz','98652341','jeymisarahimajano@gmail.com'),
(15,'1601200700074','Rosa Yissel','Chavez Ramirez','94609370','kironlleva@gmail.com'),
(16,'1611198400016','Rolando Antonio','Chavez Sabillon','97641430','yisselchavez49@gmail.com'),
(17,'160197800032','Luis Fernando','Teruel Umanzor','95173632','luisteruel@gmail.com'),
(18,'1601200100023','Dodanin Ever','Ramirez Jimenez','98741232','dodaninever@gmail.com'),
(19,'1601200400121','Ashlee','Ramirez Jimenez','87454512','ashleejimenez@gmail.com'),
(20,'1645200400212','Rolando Antonio','Chavez Sabillon','98741412','rolandoantonio@gmail.com'),
(21,'1601197633212','Ximena Mailen','Bados Ramirez','89518084','ximenabados@gmail.com'),
(22,'1415197400123','Diego Alejandro','Chavez','97451430','diegoale@gmail.com'),
(23,'1412174566666','Iris Rocio','Hiraeta Avelar','97451430','irisavelarh@gmail.com'),
(24,'14121945000365','Iliana','Bustamante','98745656','iliana@gmail.com'),
(25,'1614195700212','Sujeto','Prueba','99874545','sujetoprueba@gmail.com'),
(26,'1601200400254','Lourdes','Ramirez','32524565','lourdes@icloud.com'),
(27,'1214195433654','PRUEBA','USER','98744542','userprueba@gmail.com'),
(28,'1612197811421','SUJETO DE','PRUEBAS','87452323','sujetodeprueba@icloud.com'),
(29,'1601200700427','Anner','Reyes','93831219','anner@gmail.com'),
(30,'1601200700234','PRUEBA USER','USER PRUEBA','87454545','prueba');

/*Table structure for table `equipos_registrados` */

DROP TABLE IF EXISTS `equipos_registrados`;

CREATE TABLE `equipos_registrados` (
  `id_equipo` int(11) NOT NULL AUTO_INCREMENT,
  `id_cliente` int(11) NOT NULL,
  `id_tipo` int(11) NOT NULL,
  `id_marca` int(11) NOT NULL,
  `modelo` varchar(100) NOT NULL,
  `imei_serie` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_equipo`),
  KEY `id_cliente` (`id_cliente`),
  KEY `id_tipo` (`id_tipo`),
  KEY `id_marca` (`id_marca`),
  CONSTRAINT `equipos_registrados_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`),
  CONSTRAINT `equipos_registrados_ibfk_2` FOREIGN KEY (`id_tipo`) REFERENCES `tipos_equipo` (`id_tipo`),
  CONSTRAINT `equipos_registrados_ibfk_3` FOREIGN KEY (`id_marca`) REFERENCES `marcas` (`id_marca`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `equipos_registrados` */

insert  into `equipos_registrados`(`id_equipo`,`id_cliente`,`id_tipo`,`id_marca`,`modelo`,`imei_serie`) values 
(1,2,2,4,'Inspiron 15','1'),
(2,2,3,1,'sdadsa','dsada'),
(3,5,1,2,'Iphone 16 Pro Max','SN-20260329171945'),
(4,3,1,2,'Iphone XR','SN-20260329190323'),
(5,6,2,4,'DELL INSPIRON 15','SN-20260329211319'),
(6,12,1,1,'S24 ULTRA','SN-20260329211608'),
(7,5,1,1,'galaxy s9','SN-20260329235451'),
(8,11,1,10,'INFINIX 50 PRO','SN-20260330160907'),
(9,14,1,2,'iphone 12','SN-20260330173114'),
(10,10,1,10,'MOTOROLA E20','SN-20260331193713'),
(11,5,1,2,'IPHONE 14 PRO MAX','SN-20260331202220'),
(12,6,1,1,'Iphone 16 Pro Max','SN-20260402155338'),
(13,5,2,28,'2210','SN-20260402155555'),
(14,5,2,28,'2210','SN-20260402155555'),
(15,12,1,2,'S22 Ultra','SN-20260402155839'),
(16,15,1,1,'Iphone XR','SN-20260402162302'),
(17,12,1,13,'124','SN-20260402173428'),
(18,16,4,40,'Tab A9','SN-20260402174203'),
(19,15,7,63,'L3250','SN-20260402181704'),
(20,10,3,31,'PlayStation4','SN-20260402182928'),
(21,17,1,5,'G24','SN-20260402183644'),
(22,24,4,42,'Ideapad','SN-20260402185834'),
(23,24,1,13,'s0123','SN-20260402185851'),
(24,25,2,17,'Inspiron 1525','SN-20260402190604'),
(25,26,1,2,'s24 ULTRA','SN-20260402214214'),
(26,5,5,46,'AppleWatch (No Model Acces)','SN-20260403000541'),
(27,27,1,8,'G53','SN-20260403002344'),
(28,6,1,1,'Iphone 17 Pro','SN-20260404233104'),
(29,13,1,15,'X5B PLUS','SN-20260405200546'),
(30,28,1,13,'d231','SN-20260405203505'),
(31,28,1,1,'Iphone 17 Pro Max Orange','SN-20260406184554'),
(32,29,1,2,'S22 Ultra','SN-20260406185620'),
(33,29,1,14,'Pixel','SN-20260406191925'),
(34,30,1,1,'IPHONE 6 PLUS','SN-20260407190117');

/*Table structure for table `marcas` */

DROP TABLE IF EXISTS `marcas`;

CREATE TABLE `marcas` (
  `id_marca` int(11) NOT NULL AUTO_INCREMENT,
  `nombre_marca` varchar(50) NOT NULL,
  `id_tipo` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_marca`),
  KEY `id_tipo` (`id_tipo`),
  CONSTRAINT `marcas_ibfk_1` FOREIGN KEY (`id_tipo`) REFERENCES `tipos_equipo` (`id_tipo`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `marcas` */

insert  into `marcas`(`id_marca`,`nombre_marca`,`id_tipo`) values 
(1,'Apple',1),
(2,'Samsung',1),
(3,'Huawei',1),
(4,'Xiaomi',1),
(5,'Motorola',1),
(6,'Tecno',1),
(7,'Infinix',1),
(8,'BLU',1),
(9,'Sky',1),
(10,'LG',1),
(11,'Sony',1),
(12,'ZTE',1),
(13,'Alcatel',1),
(14,'Google',1),
(15,'Honor',1),
(16,'Otros',1),
(17,'Dell',2),
(18,'HP',2),
(19,'Acer',2),
(20,'Asus',2),
(21,'Lenovo',2),
(22,'MSI',2),
(23,'Apple',2),
(24,'Toshiba',2),
(25,'Gateway',2),
(26,'Sony Vaio',2),
(27,'Microsoft Surface',2),
(28,'Alienware',2),
(29,'Gigabyte',2),
(30,'Otros',2),
(31,'Sony (PlayStation)',3),
(32,'Microsoft (Xbox)',3),
(33,'Nintendo',3),
(34,'Sega',3),
(35,'Atari',3),
(36,'Valve (Steam Deck)',3),
(37,'Asus (ROG Ally)',3),
(38,'Otros',3),
(39,'Apple (iPad)',4),
(40,'Samsung (Galaxy Tab)',4),
(41,'Amazon (Fire)',4),
(42,'Lenovo',4),
(43,'Huawei',4),
(44,'Alcatel',4),
(45,'Otros',4),
(46,'Apple Watch',5),
(47,'Samsung Gear',5),
(48,'Huawei',5),
(49,'Garmin',5),
(50,'Fitbit',5),
(51,'Amazfit',5),
(52,'Xiaomi',5),
(53,'Otros',5),
(54,'JBL',6),
(55,'Bose',6),
(56,'Sony',6),
(57,'Beats',6),
(58,'Skullcandy',6),
(59,'Pioneer',6),
(60,'Sennheiser',6),
(61,'Logitech',6),
(62,'Otros',6),
(63,'Epson',7),
(64,'Canon',7),
(65,'HP',7),
(66,'Brother',7),
(67,'Lexmark',7),
(68,'Otros',7);

/*Table structure for table `ordenes_reparacion` */

DROP TABLE IF EXISTS `ordenes_reparacion`;

CREATE TABLE `ordenes_reparacion` (
  `id_orden` int(11) NOT NULL AUTO_INCREMENT,
  `id_equipo` int(11) NOT NULL,
  `fecha_ingreso` datetime DEFAULT current_timestamp(),
  `problema_reportado` text NOT NULL,
  `trabajo_realizado` text DEFAULT NULL,
  `costo` decimal(10,2) NOT NULL DEFAULT 0.00 CHECK (`costo` >= 0),
  `estado` varchar(50) DEFAULT NULL,
  `id_usuario_entrega` int(11) DEFAULT NULL,
  `seguridad_dispositivo` varchar(50) DEFAULT 'Sin Clave',
  PRIMARY KEY (`id_orden`),
  KEY `id_equipo` (`id_equipo`),
  KEY `fk_orden_usuario` (`id_usuario_entrega`),
  CONSTRAINT `fk_orden_usuario` FOREIGN KEY (`id_usuario_entrega`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `ordenes_reparacion_ibfk_1` FOREIGN KEY (`id_equipo`) REFERENCES `equipos_registrados` (`id_equipo`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `ordenes_reparacion` */

insert  into `ordenes_reparacion`(`id_orden`,`id_equipo`,`fecha_ingreso`,`problema_reportado`,`trabajo_realizado`,`costo`,`estado`,`id_usuario_entrega`,`seguridad_dispositivo`) values 
(3,5,'2026-03-29 21:13:44','NO ENCIENDE','Escribe aquí la reparación realizada.',0.00,'Entregado',NULL,'Sin Clave'),
(4,6,'2026-03-29 21:16:33','BATERIA MALA','CAMBIO DE BATERIA',600.00,'Entregado',NULL,'Sin Clave'),
(5,7,'2026-03-29 23:55:22','pantalla dañada','cambio de pantalla',4500.00,'Entregado',NULL,'Sin Clave'),
(6,8,'2026-03-30 16:09:35','PANTALLA DAÑADA','CAMBIO DE PANTALLA',0.00,'Entregado',NULL,'Sin Clave'),
(7,9,'2026-03-30 17:32:28','cambio de pantalla','remplado de pantalla',1800.00,'Entregado',NULL,'Sin Clave'),
(8,10,'2026-03-31 19:38:16','MARCO DOBLADO','CAMBIO DE MARCO',800.00,'Entregado',NULL,'Sin Clave'),
(9,11,'2026-03-31 20:24:40','PRUEBA','Escribe aquí la reparación realizada.',0.00,'Entregado',NULL,'Sin Clave'),
(10,11,'2026-03-31 20:25:17','DASD','',0.00,'Entregado',NULL,'Sin Clave'),
(11,10,'2026-03-31 20:32:13','pantalla dañada','cambio de pantalla',1500.00,'Entregado',NULL,'Sin Clave'),
(12,3,'2026-04-02 16:08:08','Cambio de Tapadera','Reemplazo de tapadera',1500.00,'Entregado',NULL,'Sin Clave'),
(13,16,'2026-04-02 16:23:23','Cambio de bateria','Cambio de bateria',1300.00,'Entregado',NULL,'Sin Clave'),
(14,17,'2026-04-02 17:35:17','Pantalla Dañada','Cambio de Pantalla',1500.00,'Entregado',NULL,'Sin Clave'),
(15,18,'2026-04-02 17:43:31','Actualizar software','Actualizacion realizada',2000.00,'Entregado',NULL,'Sin Clave'),
(16,19,'2026-04-02 18:17:19','No imprime','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'Sin Clave'),
(17,20,'2026-04-02 18:29:57','Limpieza interna + cambio de pasta termica','Limpieza interna + cambio de pasta termica',0.00,'Entregado',NULL,'Sin Clave'),
(18,21,'2026-04-02 18:37:11','No recuerda la contraseña, flasheo + cuenta','Escribe aquí la reparación realizada.',0.00,'Entregado',NULL,'Sin Clave'),
(19,23,'2026-04-02 19:00:23','Pantalla Dañada','No tiene reparación',0.00,'Entregado',NULL,'Sin Clave'),
(20,24,'2026-04-02 19:06:31','Cambio de HDD a SSD + Respaldo','cambio de HDD a SSD + Respaldo + Limpieza Interna y Cambio de pasta termica.',1500.00,'Entregado',NULL,'Sin Clave'),
(21,3,'2026-04-02 19:37:55','Pantalla Dañada','Cambio de Pantalla',1500.00,'Entregado',NULL,'Sin Clave'),
(22,3,'2026-04-02 19:49:55','pantalla','prueba_ver_si_funciona_ticket.',1500.00,'Entregado',NULL,'Sin Clave'),
(23,25,'2026-04-02 21:42:30','PANTALLA','CAMBIO PANTALLA',8000.00,'Entregado',1,'Sin Clave'),
(24,26,'2026-04-03 00:05:52','No enciende','No se reparó, placa dañada',0.00,'Entregado',1,'Sin Clave'),
(26,3,'2026-04-04 23:43:38','Cambio de Glass','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'Sin Clave'),
(27,28,'2026-04-04 23:50:09','Cambio de Glass, Deja apartado protector','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'Sin Clave'),
(28,6,'2026-04-04 23:52:54','Cambio de Glass - Cambio de Bateria','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'Sin Clave'),
(29,8,'2026-04-05 00:00:54','Cambio de Glass - Parte Prueba','Escribe aquí la reparación realizada.',0.00,' ',NULL,'Sin Clave'),
(30,10,'2026-04-05 00:23:00','solamente es para pruebas','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'Sin Clave'),
(31,9,'2026-04-05 00:28:59','Cambio de Pantalla  - Documento de Prueba','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'Sin Clave'),
(32,27,'2026-04-05 00:33:33','prueba','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'Sin Clave'),
(33,25,'2026-04-05 00:35:28','PRUEBA NADA MAS','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'Sin Clave'),
(34,14,'2026-04-05 00:36:18','Placa mala supongo','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'Sin Clave'),
(35,7,'2026-04-05 00:41:47','problemas en pantalla','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'Sin Clave'),
(36,27,'2026-04-05 00:53:12','documento de prueba','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'Sin Clave'),
(37,21,'2026-04-05 01:08:50','PRUEBA OTRA VEZ','Escribe aquí la reparación realizada.',0.00,' ',NULL,'Sin Clave'),
(38,25,'2026-04-05 01:11:07','prueba','Escribe aquí la reparación realizada.',0.00,' ',NULL,'Sin Clave'),
(39,5,'2026-04-05 01:11:31','Escribe aquí el pdasdaroblema de tu equipo.dsda','Escribe aquí la reparación realizada.',0.00,' ',NULL,'Sin Clave'),
(40,6,'2026-04-05 01:14:39','dasdads','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'Sin Clave'),
(41,9,'2026-04-05 01:15:32','Escribe aquí el problemdsdaa de tu equipo.','Escribe aquí la reparación realizada.',0.00,' ',NULL,'Sin Clave'),
(42,6,'2026-04-05 01:17:23','dsda','SOLO PARA PRUEBAS',1500.00,'Entregado',1,'Sin Clave'),
(43,17,'2026-04-05 01:21:18',';l,;l','CAMBIO DE PANTALLA',4500.00,'Entregado',1,'Sin Clave'),
(44,16,'2026-04-05 01:24:16','sdada','CAMBIO DE PANTALLA',1500.00,'Entregado',1,'Sin Clave'),
(45,29,'2026-04-05 20:06:27','CAMBIO DE PANTALLA, NO TRAE BANDEJA SIM, SOLO RED CLARO','CAMBIO DE PANTALLA',1500.00,'Recibido',1,'Sin Clave'),
(46,30,'2026-04-05 20:35:57','SOLAMENTE ES PARA PRUEBAS, PROBLEMA MODIFICADO','NUEVO TRABAJO REALIZADO, SOLO PRUEBAS',2000.00,'Entregado',1,'Sin Clave'),
(47,32,'2026-04-06 18:57:31','TElefono churuncuyo por se samsung...','El mono de ever va a trabajar en eso',25000.00,'Reparado',7,'Sin Clave'),
(48,33,'2026-04-06 19:19:53','NO DA IMAGEN','Escribe aquí la reparación realizada.\n\n[06/04/2026 23:10 - Estado cambiado a \'SIN REPARACION\' por: ECHAVEZ868]\n\n[06/04/2026 23:39 - Estado cambiado a \'REPARADO\' por: SELVIN]\n\n[06/04/2026 23:39 - EQUIPO ENTREGADO Y COBRADO POR: ADMIN]',1500.00,'Entregado',1,'Sin Clave'),
(49,3,'2026-04-07 00:11:40','ANGEL SE LA COME','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'Sin Clave'),
(50,6,'2026-04-07 00:12:18','Escribe aquí el problema de tu equipo.DSADADA','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'1234'),
(51,34,'2026-04-07 19:02:42','CAMBIO DE BATERIA, SE DESCARGA RAPIDO','CAMBIO DE BATERIA\n\n[07/04/2026 19:03 - Estado cambiado a \'REPARADO\' por: ADMIN]\n\n[07/04/2026 19:04 - EQUIPO ENTREGADO Y COBRADO POR: SELVIN]',1500.00,'Entregado',8,'P'),
(52,6,'2026-04-07 19:13:24','cambio de bateria','Escribe aquí la reparación realizada.',0.00,'Recibido',NULL,'1234');

/*Table structure for table `tipos_equipo` */

DROP TABLE IF EXISTS `tipos_equipo`;

CREATE TABLE `tipos_equipo` (
  `id_tipo` int(11) NOT NULL AUTO_INCREMENT,
  `nombre_tipo` varchar(50) NOT NULL,
  PRIMARY KEY (`id_tipo`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `tipos_equipo` */

insert  into `tipos_equipo`(`id_tipo`,`nombre_tipo`) values 
(1,'Smartphones'),
(2,'Computadoras'),
(3,'Consolas'),
(4,'Tablets'),
(5,'Smartwatches'),
(6,'Audio'),
(7,'Impresoras');

/*Table structure for table `usuarios` */

DROP TABLE IF EXISTS `usuarios`;

CREATE TABLE `usuarios` (
  `id_usuario` int(11) NOT NULL AUTO_INCREMENT,
  `usuario` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `rol` varchar(20) DEFAULT 'Tecnico',
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `usuario` (`usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `usuarios` */

insert  into `usuarios`(`id_usuario`,`usuario`,`password_hash`,`rol`) values 
(1,'admin','03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4','Administrador'),
(7,'angel','03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4','Tecnico'),
(8,'selvin','0ece2784eec73d4d46899f5f57dabb898323c67746e51f7ba10b171a7dd1c7ef','Administrador');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
