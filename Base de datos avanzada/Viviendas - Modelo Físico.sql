-- MySQL Workbench Forward Engineering
CREATE USER 'ProyectoIntegrador'@'localhost' IDENTIFIED BY 'bdcomputacion23';
GRANT ALL PRIVILEGES ON Viviendas.* TO 'ProyectoIntegrador'@'localhost';

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema Viviendas
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema Viviendas
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `Viviendas` DEFAULT CHARACTER SET utf8 ;
USE `Viviendas` ;

-- -----------------------------------------------------
-- Table `Viviendas`.`EmergenciasEcu`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Viviendas`.`EmergenciasEcu` (
  `idEmergenciasEcu` INT NOT NULL AUTO_INCREMENT,
  `emergencia` VARCHAR(100) NOT NULL,
  `servicio` VARCHAR(150) NOT NULL,
  PRIMARY KEY (`idEmergenciasEcu`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Viviendas`.`Provincia`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Viviendas`.`Provincia` (
  `idProvincia` INT NOT NULL,
  `nombreProvincia` VARCHAR(100) NOT NULL,
  `dispoHospitalaria` DECIMAL NOT NULL,
  `homicidios` INT NOT NULL,
  PRIMARY KEY (`idProvincia`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Viviendas`.`Canton`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Viviendas`.`Canton` (
  `idCanton` INT NOT NULL,
  `nv_Pobreza` FLOAT NOT NULL,
  `nombreCanton` VARCHAR(100) NOT NULL,
  `Provincia_idProvincia` INT NOT NULL,
  PRIMARY KEY (`idCanton`, `Provincia_idProvincia`),
  INDEX `fk_Canton_Provincia1_idx` (`Provincia_idProvincia` ASC) VISIBLE,
  CONSTRAINT `fk_Canton_Provincia1`
    FOREIGN KEY (`Provincia_idProvincia`)
    REFERENCES `Viviendas`.`Provincia` (`idProvincia`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Viviendas`.`Sector`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Viviendas`.`Sector` (
  `idSector` INT NOT NULL,
  `tipoSector` VARCHAR(100) NOT NULL,
  `nv_Desempleo` FLOAT NOT NULL,
  PRIMARY KEY (`idSector`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Viviendas`.`Ciudad`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Viviendas`.`Ciudad` (
  `idCiudad` BIGINT(150) NOT NULL,
  `nombreCiudad` VARCHAR(100) NOT NULL,
  `Canton_idCanton` INT NOT NULL,
  `Sector_idSector` INT NOT NULL,
  PRIMARY KEY (`idCiudad`, `Canton_idCanton`, `Sector_idSector`),
  INDEX `fk_Ciudad_Canton_idx` (`Canton_idCanton` ASC) VISIBLE,
  INDEX `fk_Ciudad_Sector1_idx` (`Sector_idSector` ASC) VISIBLE,
  CONSTRAINT `fk_Ciudad_Canton`
    FOREIGN KEY (`Canton_idCanton`)
    REFERENCES `Viviendas`.`Canton` (`idCanton`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Ciudad_Sector1`
    FOREIGN KEY (`Sector_idSector`)
    REFERENCES `Viviendas`.`Sector` (`idSector`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Viviendas`.`Conglomerado`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Viviendas`.`Conglomerado` (
  `idConglomerado` INT NOT NULL,
  `panelm` INT NOT NULL,
  `estrato` INT NOT NULL,
  `Ciudad_idCiudad` BIGINT(150) NOT NULL,
  PRIMARY KEY (`idConglomerado`, `Ciudad_idCiudad`),
  INDEX `fk_Conglomerado_Ciudad1_idx` (`Ciudad_idCiudad` ASC) VISIBLE,
  CONSTRAINT `fk_Conglomerado_Ciudad1`
    FOREIGN KEY (`Ciudad_idCiudad`)
    REFERENCES `Viviendas`.`Ciudad` (`idCiudad`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `Viviendas`.`Vivienda`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Viviendas`.`Vivienda` (
  `idVivienda` BIGINT(150) NOT NULL AUTO_INCREMENT,
  `codVivienda` BIGINT(150) NOT NULL,
  `fexp` FLOAT NOT NULL,
  `upm` BIGINT(150) NOT NULL,
  `incluye_luz` TEXT(200) NOT NULL,
  `periodo` INT NOT NULL,
  `Conglomerado_idConglomerado` INT NOT NULL,
  PRIMARY KEY (`idVivienda`, `Conglomerado_idConglomerado`),
  INDEX `fk_Vivienda_Conglomerado1_idx` (`Conglomerado_idConglomerado` ASC) VISIBLE,
  CONSTRAINT `fk_Vivienda_Conglomerado1`
    FOREIGN KEY (`Conglomerado_idConglomerado`)
    REFERENCES `Viviendas`.`Conglomerado` (`idConglomerado`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Viviendas`.`Hogar`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Viviendas`.`Hogar` (
  `Vivienda_idVivienda` BIGINT(150) NOT NULL AUTO_INCREMENT,
  `pagoMensual` DECIMAL(25) NOT NULL,
  `incluye_agua` TEXT(200) NOT NULL,
  `numDormitorios` BIGINT(150) NOT NULL,
  `numCuartosNegocio` BIGINT(150) NOT NULL,
  `numCuartos` BIGINT(150) NOT NULL,
  `tipoVivienda` VARCHAR(100) NOT NULL,
  `cantidadHogar` BIGINT(150) NOT NULL,
  `disponeHogarCocina` VARCHAR(2) NOT NULL,
  `codHogar` BIGINT(150) NOT NULL,
  INDEX `fk_Hogar_Vivienda1_idx` (`Vivienda_idVivienda` ASC) VISIBLE,
  CONSTRAINT `fk_Hogar_Vivienda1`
    FOREIGN KEY (`Vivienda_idVivienda`)
    REFERENCES `Viviendas`.`Vivienda` (`idVivienda`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Viviendas`.`Materiales`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Viviendas`.`Materiales` (
  `tipoVivienda` VARCHAR(100) NOT NULL,
  `materialCocina` VARCHAR(150) NOT NULL,
  `materialTechoCubierta` VARCHAR(150) NOT NULL,
  `estadoParedes` VARCHAR(150) NOT NULL,
  `estadoTecho` VARCHAR(150) NOT NULL,
  `materialPiso` VARCHAR(150) NOT NULL,
  `estadoPiso` VARCHAR(150) NOT NULL,
  `materialParedes` VARCHAR(150) NOT NULL,
  `Hogar_Vivienda_idVivienda` BIGINT(150) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Hogar_Vivienda_idVivienda`),
  CONSTRAINT `fk_Materiales_Hogar1`
    FOREIGN KEY (`Hogar_Vivienda_idVivienda`)
    REFERENCES `Viviendas`.`Hogar` (`Vivienda_idVivienda`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Viviendas`.`ServiciosBasicos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Viviendas`.`ServiciosBasicos` (
  `tipoServicioHigienico` VARCHAR(150) NOT NULL,
  `alternativaServicioHigienico` VARCHAR(150) NOT NULL,
  `servicioDucha` VARCHAR(100) NOT NULL,
  `tipoAlumbrado` VARCHAR(150) NOT NULL,
  `contieneMedidorAgua` VARCHAR(150) NOT NULL,
  `aguaJunta` VARCHAR(150) NOT NULL,
  `fuenteAgua` VARCHAR(150) NOT NULL,
  `sanitarioPrincipal` VARCHAR(100) NOT NULL,
  `aguaRecibida` VARCHAR(150) NOT NULL,
  `eliminacionBasura` VARCHAR(150) NOT NULL,
  `Hogar_Vivienda_idVivienda` BIGINT(150) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Hogar_Vivienda_idVivienda`),
  CONSTRAINT `fk_ServiciosBasicos_Hogar1`
    FOREIGN KEY (`Hogar_Vivienda_idVivienda`)
    REFERENCES `Viviendas`.`Hogar` (`Vivienda_idVivienda`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Viviendas`.`Vehiculos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Viviendas`.`Vehiculos` (
  `numMotos` BIGINT(150) NOT NULL,
  `numVehiculos` BIGINT(150) NOT NULL,
  `tieneMotos` CHAR(2) NOT NULL,
  `tieneVehiculos` CHAR(2) NOT NULL,
  `Hogar_Vivienda_idVivienda` BIGINT(150) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Hogar_Vivienda_idVivienda`),
  CONSTRAINT `fk_Vehiculos_Hogar1`
    FOREIGN KEY (`Hogar_Vivienda_idVivienda`)
    REFERENCES `Viviendas`.`Hogar` (`Vivienda_idVivienda`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Viviendas`.`Combustible`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Viviendas`.`Combustible` (
  `abastecimiento` VARCHAR(150) NOT NULL,
  `gasto` VARCHAR(150) NOT NULL,
  `Vehiculos_Hogar_Vivienda_idVivienda` BIGINT(150) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Vehiculos_Hogar_Vivienda_idVivienda`),
  CONSTRAINT `fk_Combustible_Vehiculos1`
    FOREIGN KEY (`Vehiculos_Hogar_Vivienda_idVivienda`)
    REFERENCES `Viviendas`.`Vehiculos` (`Hogar_Vivienda_idVivienda`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `Viviendas`.`Emergencia`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Viviendas`.`Emergencia` (
  `idCanton` INT NOT NULL,
  `EmergenciasEcu_idEmergenciasEcu` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`idCanton`, `EmergenciasEcu_idEmergenciasEcu`),
  INDEX `fk_Canton_has_EmergenciasEcu_EmergenciasEcu1_idx` (`EmergenciasEcu_idEmergenciasEcu` ASC) VISIBLE,
  INDEX `fk_Canton_has_EmergenciasEcu_Canton1_idx` (`idCanton` ASC) VISIBLE,
  CONSTRAINT `fk_Canton_has_EmergenciasEcu_Canton1`
    FOREIGN KEY (`idCanton`)
    REFERENCES `Viviendas`.`Canton` (`idCanton`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Canton_has_EmergenciasEcu_EmergenciasEcu1`
    FOREIGN KEY (`EmergenciasEcu_idEmergenciasEcu`)
    REFERENCES `Viviendas`.`EmergenciasEcu` (`idEmergenciasEcu`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
