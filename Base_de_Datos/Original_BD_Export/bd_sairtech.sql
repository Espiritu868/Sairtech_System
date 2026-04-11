/*
SQLyog Community v13.1.7 (64 bit)
MySQL - 10.4.32-MariaDB : Database - db_sairtech
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`db_sairtech` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `db_sairtech`;

/*Table structure for table `categorias_productos` */

DROP TABLE IF EXISTS `categorias_productos`;

CREATE TABLE `categorias_productos` (
  `id_categoria` int(11) NOT NULL AUTO_INCREMENT,
  `nombre_categoria` varchar(50) NOT NULL,
  `descripcion` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id_categoria`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `categorias_productos` */

insert  into `categorias_productos`(`id_categoria`,`nombre_categoria`,`descripcion`) values 
(1,'Accesorios','Cables, cargadores, fundas, audífonos, etc.'),
(2,'Repuestos','Pantallas, baterías, pines de carga, flex, etc.'),
(3,'Herramientas','Insumos del taller para la venta'),
(4,'Servicios','Mano de obra, revisiones, software (No controla stock)');

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `clientes` */

insert  into `clientes`(`id_cliente`,`numero_identidad`,`nombre`,`apellido`,`telefono`,`correo`) values 
(1,'1601200400252','EVER','CHAVEZ','33680903','chavesramiresever@gmail.com'),
(2,'1611198400232','ROSA','CHAVEZ RAMIREZ','98744545','rosaramirez@gmail.com'),
(3,'1611197800012','SELVIN ANTONIO','HIRAETA AVELAR','96863040','selvinhiraetamc@gmail.com');

/*Table structure for table `detalles_venta` */

DROP TABLE IF EXISTS `detalles_venta`;

CREATE TABLE `detalles_venta` (
  `id_detalle` int(11) NOT NULL AUTO_INCREMENT,
  `id_venta` int(11) NOT NULL,
  `id_producto` int(11) DEFAULT NULL,
  `descripcion` varchar(150) NOT NULL,
  `cantidad` int(11) NOT NULL DEFAULT 1 CHECK (`cantidad` > 0),
  `precio_unitario` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `id_venta` (`id_venta`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `detalles_venta_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`) ON DELETE CASCADE,
  CONSTRAINT `detalles_venta_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `detalles_venta` */

insert  into `detalles_venta`(`id_detalle`,`id_venta`,`id_producto`,`descripcion`,`cantidad`,`precio_unitario`,`subtotal`) values 
(1,1,NULL,'Orden #5 - Rep: EQEQWQEQWEQWEQWQWEQ (ROSA CHAVEZ RAMIREZ)',1,0.00,0.00),
(2,2,NULL,'Orden #4 - Rep: dsa (EVER CHAVEZ)',1,1500.00,1500.00),
(3,3,NULL,'Orden #2 - Rep: POVA 6 (EVER CHAVEZ)',1,0.00,0.00),
(4,4,1,'Cargador Iphone 13 Pro Max',1,400.00,400.00),
(5,5,NULL,'Orden #9 - Rep: c14 (SELVIN ANTONIO HIRAETA AVELAR)',1,1500.00,1500.00);

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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `equipos_registrados` */

insert  into `equipos_registrados`(`id_equipo`,`id_cliente`,`id_tipo`,`id_marca`,`modelo`,`imei_serie`) values 
(1,1,1,1,'IPHONE 16 PRO MAX','SN-20260410103524'),
(2,1,1,6,'POVA 6','SN-20260410110845'),
(3,1,1,13,'dsa','SN-20260410111543'),
(4,1,1,13,'dsa','SN-20260410111756'),
(5,2,2,19,'EQEQWQEQWEQWEQWQWEQ','SN-20260410141100'),
(6,3,1,1,'IPHONE 17 PRO MAX','SN-20260410145233'),
(7,1,2,20,'NITRO V16','SN-20260410174524'),
(8,3,1,15,'X6B','SN-20260410192504'),
(9,3,4,44,'c14','SN-20260410213503');

/*Table structure for table `lotes_knijico` */

DROP TABLE IF EXISTS `lotes_knijico`;

CREATE TABLE `lotes_knijico` (
  `id_lote` int(11) NOT NULL AUTO_INCREMENT,
  `nombre_lote` varchar(100) NOT NULL,
  `fecha_ingreso` datetime DEFAULT current_timestamp(),
  `estado` varchar(20) DEFAULT 'Activo',
  PRIMARY KEY (`id_lote`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `lotes_knijico` */

insert  into `lotes_knijico`(`id_lote`,`nombre_lote`,`fecha_ingreso`,`estado`) values 
(1,'Lote 1','2026-04-11 09:44:46','Activo'),
(2,'Lote 2','2026-04-11 09:46:42','Activo');

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
  `fecha_entrega` datetime DEFAULT NULL,
  PRIMARY KEY (`id_orden`),
  KEY `id_equipo` (`id_equipo`),
  KEY `fk_orden_usuario` (`id_usuario_entrega`),
  CONSTRAINT `fk_orden_usuario` FOREIGN KEY (`id_usuario_entrega`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `ordenes_reparacion_ibfk_1` FOREIGN KEY (`id_equipo`) REFERENCES `equipos_registrados` (`id_equipo`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `ordenes_reparacion` */

insert  into `ordenes_reparacion`(`id_orden`,`id_equipo`,`fecha_ingreso`,`problema_reportado`,`trabajo_realizado`,`costo`,`estado`,`id_usuario_entrega`,`seguridad_dispositivo`,`fecha_entrega`) values 
(1,1,'2026-04-10 10:35:38','CAMBIO DE TAPADERA','\n\n[10/04/2026 10:36 - Estado cambiado a \'EN REVISION\' por: ADMIN]\n\n[10/04/2026 10:36 - EQUIPO ENTREGADO Y COBRADO POR: ADMIN]',1500.00,'Entregado',1,'Sin Clave',NULL),
(2,2,'2026-04-10 11:08:45','PROBLEMA DE PLACA, CAMBIAR PLACA','',0.00,'Entregado',1,'Patrón',NULL),
(3,3,'2026-04-10 11:15:43','dsada','sdadasd',0.00,'Recibido',NULL,'1234',NULL),
(4,4,'2026-04-10 11:17:56','prueba','',1500.00,'Entregado',1,'Sin Clave',NULL),
(5,5,'2026-04-10 14:11:00','PRUEBA','',0.00,'Entregado',1,'Sin Clave',NULL),
(6,6,'2026-04-10 14:52:33','CAMBIO DE BATERIA','',0.00,'Recibido',NULL,'0000',NULL),
(7,7,'2026-04-10 17:45:24','BISAGRAS','',0.00,'Recibido',NULL,'Sin Clave',NULL),
(8,8,'2026-04-10 19:25:04','CAMBIO DE PANTALLA','',0.00,'Recibido',NULL,'Sin Clave',NULL),
(9,9,'2026-04-10 21:35:03','cambio de pantalla','modulo de carga\n[10/04/2026 21:36 - Estado cambiado a \'REPARADO\' por: ADMIN]',1500.00,'Entregado',1,'Patrón',NULL);

/*Table structure for table `pantallas_knijico` */

DROP TABLE IF EXISTS `pantallas_knijico`;

CREATE TABLE `pantallas_knijico` (
  `id_pantalla` int(11) NOT NULL AUTO_INCREMENT,
  `id_lote` int(11) NOT NULL,
  `modelo_equipo` varchar(150) NOT NULL,
  `precio_compra` decimal(10,2) NOT NULL,
  `precio_cliente` decimal(10,2) NOT NULL,
  `precio_tecnico` decimal(10,2) NOT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `estado` varchar(20) DEFAULT 'Activo',
  `numero_caja` int(11) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_pantalla`),
  KEY `id_lote` (`id_lote`),
  CONSTRAINT `pantallas_knijico_ibfk_1` FOREIGN KEY (`id_lote`) REFERENCES `lotes_knijico` (`id_lote`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `pantallas_knijico` */

insert  into `pantallas_knijico`(`id_pantalla`,`id_lote`,`modelo_equipo`,`precio_compra`,`precio_cliente`,`precio_tecnico`,`stock`,`estado`,`numero_caja`) values 
(1,1,'LCD KNIJICO SAMSUNG GALAXY A12 UNIVERSAL C1',250.00,1300.00,450.00,3,'Activo',1),
(2,1,'LCD KNIJICO SAMSUNG GALAXY A10S C1',200.00,1000.00,400.00,3,'Oculto',1),
(3,2,'LCD KNIJICO IPHONE 8 PLUS C3',250.00,1000.00,500.00,3,'Activo',3);

/*Table structure for table `productos` */

DROP TABLE IF EXISTS `productos`;

CREATE TABLE `productos` (
  `id_producto` int(11) NOT NULL AUTO_INCREMENT,
  `codigo_barras` varchar(50) DEFAULT NULL,
  `nombre_producto` varchar(100) NOT NULL,
  `id_categoria` int(11) NOT NULL,
  `precio_compra` decimal(10,2) NOT NULL DEFAULT 0.00 CHECK (`precio_compra` >= 0),
  `precio_venta` decimal(10,2) NOT NULL DEFAULT 0.00 CHECK (`precio_venta` >= 0),
  `stock` int(11) NOT NULL DEFAULT 0,
  `stock_minimo` int(11) NOT NULL DEFAULT 5,
  `id_proveedor` int(11) DEFAULT NULL,
  `aplica_precio_tecnico` tinyint(1) DEFAULT 0,
  `precio_tecnico` decimal(10,2) DEFAULT 0.00,
  `ubicacion` varchar(100) DEFAULT '',
  PRIMARY KEY (`id_producto`),
  UNIQUE KEY `codigo_barras` (`codigo_barras`),
  KEY `id_categoria` (`id_categoria`),
  KEY `id_proveedor` (`id_proveedor`),
  CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categorias_productos` (`id_categoria`),
  CONSTRAINT `productos_ibfk_2` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedores` (`id_proveedor`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `productos` */

insert  into `productos`(`id_producto`,`codigo_barras`,`nombre_producto`,`id_categoria`,`precio_compra`,`precio_venta`,`stock`,`stock_minimo`,`id_proveedor`,`aplica_precio_tecnico`,`precio_tecnico`,`ubicacion`) values 
(1,'00000000001','Cargador Iphone 13 Pro Max',1,200.00,400.00,9,5,NULL,0,0.00,'Vitrina 3'),
(2,'00000000002','CARGADOR XIAOMI 67W',1,90.00,220.00,3,1,NULL,0,0.00,'Vitrina 2'),
(3,'00000000003','LCD IPHONE 8 PLUS',2,300.00,1000.00,1,0,2,1,400.00,'Vitrina 1');

/*Table structure for table `proveedores` */

DROP TABLE IF EXISTS `proveedores`;

CREATE TABLE `proveedores` (
  `id_proveedor` int(11) NOT NULL AUTO_INCREMENT,
  `empresa` varchar(150) NOT NULL,
  `nombre_contacto` varchar(100) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `direccion` text DEFAULT NULL,
  `tipo_repuestos` varchar(150) DEFAULT NULL,
  `estado` varchar(20) DEFAULT 'Activo',
  PRIMARY KEY (`id_proveedor`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `proveedores` */

insert  into `proveedores`(`id_proveedor`,`empresa`,`nombre_contacto`,`telefono`,`direccion`,`tipo_repuestos`,`estado`) values 
(1,'CL CELL','LILIBETH','','SAN PEDRO SULA','VARIADO','Activo'),
(2,'EDWAR CELL','EDWAR','','SAN PEDRIO','PANTALLAS LCD','Activo');

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `usuarios` */

insert  into `usuarios`(`id_usuario`,`usuario`,`password_hash`,`rol`) values 
(1,'admin','03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4','Administrador'),
(2,'Ever','28f35a42f0895d84a16e25149a18aa1dcfdbcf680fb20a17d955696e5ef34936','Administrador'),
(4,'apin','81a83544cf93c245178cbc1620030f1123f435af867c79d87135983c52ab39d9','Tecnico');

/*Table structure for table `ventas` */

DROP TABLE IF EXISTS `ventas`;

CREATE TABLE `ventas` (
  `id_venta` int(11) NOT NULL AUTO_INCREMENT,
  `fecha_venta` datetime DEFAULT current_timestamp(),
  `id_cliente` int(11) DEFAULT NULL,
  `id_usuario` int(11) NOT NULL,
  `id_orden` int(11) DEFAULT NULL,
  `total` decimal(10,2) NOT NULL DEFAULT 0.00,
  `metodo_pago` varchar(50) DEFAULT 'Efectivo',
  PRIMARY KEY (`id_venta`),
  KEY `id_cliente` (`id_cliente`),
  KEY `id_usuario` (`id_usuario`),
  KEY `id_orden` (`id_orden`),
  CONSTRAINT `ventas_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`),
  CONSTRAINT `ventas_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `ventas_ibfk_3` FOREIGN KEY (`id_orden`) REFERENCES `ordenes_reparacion` (`id_orden`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `ventas` */

insert  into `ventas`(`id_venta`,`fecha_venta`,`id_cliente`,`id_usuario`,`id_orden`,`total`,`metodo_pago`) values 
(1,'2026-04-10 14:18:37',NULL,1,5,0.00,'Efectivo'),
(2,'2026-04-10 14:20:27',NULL,1,4,1500.00,'Efectivo'),
(3,'2026-04-10 14:40:00',NULL,1,2,0.00,'Efectivo'),
(4,'2026-04-10 17:47:23',NULL,1,NULL,400.00,'Efectivo'),
(5,'2026-04-10 21:37:24',NULL,1,9,1500.00,'Efectivo');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
