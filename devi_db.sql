-- =============================================================
-- Base de datos: dev_i2_web (EXCLUSIVA para el proyecto DEV-I 2.0 WEB)
-- Proyecto: DEV-I 2.0 WEB - Sistema de Gestion de Citas
-- Framework: Jakarta Faces (JSF) con CDI y JDBC
-- Autor: Andres Aguiar
-- Fecha: 2026-08-11
-- =============================================================

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

-- Crear la base de datos exclusiva para este proyecto
CREATE DATABASE IF NOT EXISTS `dev_i2_web` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `dev_i2_web`;

-- =============================================================
-- Tabla: usuarios
-- Almacena las credenciales de los agentes del call center
-- =============================================================
CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(50) NOT NULL COMMENT 'Nombre de usuario para login',
  `password` varchar(255) NOT NULL COMMENT 'Contrasena del usuario',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_unique` (`user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Insertar usuario de prueba (usuario: admin, contrasena: 1234)
INSERT INTO `usuarios` (`user`, `password`) VALUES
('admin', '1234'),
('agente1', '0165');

-- =============================================================
-- Tabla: citas
-- Almacena las citas registradas por los agentes
-- =============================================================
CREATE TABLE `citas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cliente_nombre` varchar(100) DEFAULT NULL COMMENT 'Nombre completo del cliente',
  `correo` varchar(100) DEFAULT NULL COMMENT 'Correo electronico del cliente',
  `telefono` varchar(50) DEFAULT NULL COMMENT 'Telefono de contacto',
  `documento` varchar(50) DEFAULT NULL COMMENT 'Documento de identidad del cliente',
  `fecha_cita` varchar(20) DEFAULT NULL COMMENT 'Fecha de la cita',
  `descripcion` text DEFAULT NULL COMMENT 'Descripcion o notas de la cita',
  `estado` varchar(20) DEFAULT 'Pendiente' COMMENT 'Estado: Pendiente, Confirmada, Cancelada',
  `id_agente` int(11) DEFAULT NULL COMMENT 'ID del agente que registro la cita',
  PRIMARY KEY (`id`),
  KEY `fk_agente` (`id_agente`),
  CONSTRAINT `fk_agente` FOREIGN KEY (`id_agente`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Insertar citas de ejemplo
INSERT INTO `citas` (`cliente_nombre`, `correo`, `telefono`, `documento`, `fecha_cita`, `descripcion`, `estado`, `id_agente`) VALUES
('Juan Perez', 'juan@gmail.com', '3001234567', '80123456', '2026-08-15', 'Consulta general sobre servicios', 'Pendiente', 1),
('Maria Lopez', 'maria@hotmail.com', '3109876543', '52987654', '2026-08-16', 'Revision de contrato vigente', 'Confirmada', 1);

COMMIT;
