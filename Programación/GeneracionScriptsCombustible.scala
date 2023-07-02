package ProyectoIntegrador

import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}

object GeneracionScriptsCombustible extends App{

  val reader = CSVReader.open(new File("C:\\Users\\USUARIO\\Downloads\\enemdu_vivienda_hogar_2023_I_trimestre.csv"))(new DefaultCSVFormat {
    override val delimiter = ';'
  })
  val data = reader.allWithHeaders()
  reader.close()

  case class Combustible(
                  Abastecimiento: String,
                  Gasto: String
                  )

  val SQL_PATTERN =
    """
      |INSERT INTO viviendas.combustible (abastecimiento, gasto)
      |VALUES
      |(%s, %s);
      |""".stripMargin

  val comSuper = data
    .map(row => Combustible(row("vi1531") match {
      case "1" => "Usa super"
      case "2" => "No usa super"
      case " " => "No posee vehículo"
    },
      row("vi1541").replaceAll(" ", "0")))

 val comExtra = data
   .map(row => Combustible(row("vi1532") match {
     case "1" => "Usa extra"
     case "2" => "No usa extra"
     case " " => "No posee vehículo"
   },
     row("vi1542").replaceAll(" ", "0")))

  val comDiesel = data
    .map(row => Combustible(row("vi1533") match {
      case "1" => "Usa diesel"
      case "2" => "No usa diesel"
      case " " => "No posee vehículo"
    },
      row("vi1543").replaceAll(" ", "0")))

  val comEcoPais = data
    .map(row => Combustible(row("vi1534") match {
      case "1" => "Usa EcoPais"
      case "2" => "No usa EcoPais"
      case " " => "No posee vehículo"
    },
      row("vi1544").replaceAll(" ", "0")))

  val comElectricidad = data
    .map(row => Combustible(row("vi1535") match {
      case "1" => "Usa electricidad"
      case "2" => "No usa electricidad"
      case " " => "No posee vehículo"
    },
      row("vi1545").replaceAll(" ", "0")))

  val comGas = data
    .map(row => Combustible(row("vi1536") match {
      case "1" => "Usa Gas"
      case "2" => "No usa Gas"
      case " " => "No posee vehículo"
    },
      row("vi1546").replaceAll(" ", "0")))


  val scriptData = comSuper
      .map(vehiculos => SQL_PATTERN.formatLocal(java.util.Locale.US,
      vehiculos.Abastecimiento,
      vehiculos.Gasto))

  val scriptData2 = comExtra
    .map(vehiculos => SQL_PATTERN.formatLocal(java.util.Locale.US,
      vehiculos.Abastecimiento,
      vehiculos.Gasto))

  val scriptData3 = comDiesel
    .map(vehiculos => SQL_PATTERN.formatLocal(java.util.Locale.US,
      vehiculos.Abastecimiento,
      vehiculos.Gasto))

  val scriptData4 = comEcoPais
    .map(vehiculos => SQL_PATTERN.formatLocal(java.util.Locale.US,
      vehiculos.Abastecimiento,
      vehiculos.Gasto))

  val scriptData5 = comElectricidad
    .map(vehiculos => SQL_PATTERN.formatLocal(java.util.Locale.US,
      vehiculos.Abastecimiento,
      vehiculos.Gasto))

  val scriptData6 = comGas
    .map(vehiculos => SQL_PATTERN.formatLocal(java.util.Locale.US,
      vehiculos.Abastecimiento,
      vehiculos.Gasto))

  val ScriptFile = new File("C:\\Users\\USUARIO\\Desktop\\vehiculos_insert.sql")
  if (ScriptFile.exists()) ScriptFile.delete()

  scriptData.foreach(insert =>
    Files.write(Paths.get("C:\\Users\\USUARIO\\Desktop\\combustibleSuper_insert.sql"), insert.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND))

  scriptData2.foreach(insert =>
    Files.write(Paths.get("C:\\Users\\USUARIO\\Desktop\\combustibleExtra_insert.sql"), insert.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND))

  scriptData3.foreach(insert =>
    Files.write(Paths.get("C:\\Users\\USUARIO\\Desktop\\combustibleDiesel_insert.sql"), insert.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND))

  scriptData4.foreach(insert =>
    Files.write(Paths.get("C:\\Users\\USUARIO\\Desktop\\combustibleEcopais_insert.sql"), insert.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND))

  scriptData5.foreach(insert =>
    Files.write(Paths.get("C:\\Users\\USUARIO\\Desktop\\combustibleElectricidad_insert.sql"), insert.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND))

  scriptData6.foreach(insert =>
    Files.write(Paths.get("C:\\Users\\USUARIO\\Desktop\\combustibleGas_insert.sql"), insert.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND))
}
