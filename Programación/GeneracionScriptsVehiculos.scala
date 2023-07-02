package ProyectoIntegrador

import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}

object GeneracionScriptsVehiculos extends App{

  val reader = CSVReader.open(new File("C:\\Users\\USUARIO\\Downloads\\enemdu_vivienda_hogar_2023_I_trimestre.csv"))(new DefaultCSVFormat {
    override val delimiter = ';'
  })
  val data = reader.allWithHeaders()
  reader.close()

  case class Vehiculos(
                  numMotos: String,
                  numVehiculos: String,
                  tieneMotos: String,
                  tieneVehiculos: String
                  )

  val vehiculos = data
    .map(row => Vehiculos(row("vi1522").replaceAll("", "0")
      .replaceAll("0 0", "0"),
      row("vi1521").replaceAll("", "0").replaceAll("0 0", "0"),
      row("vi1512") match {
      case "1" => "Si"
      case "2" => "No"
    }, row("vi1511") match {
      case "1" => "Si"
      case "2" => "No"
    }))

  val SQL_PATTERN =
    """
      |INSERT INTO viviendas.vehiculos (numMotos, numVehiculos, tieneMotos, tieneVehiculos)
      |VALUES
      |(%s, %s, "%s", "%s");
      |""".stripMargin

  val scriptData = vehiculos
    .map(vehiculos => SQL_PATTERN.formatLocal(java.util.Locale.US,
      vehiculos.numMotos,
      vehiculos.numVehiculos,
      vehiculos.tieneMotos,
      vehiculos.tieneVehiculos))

  val ScriptFile = new File("C:\\Users\\USUARIO\\Desktop\\vehiculos_insert.sql")
  if (ScriptFile.exists()) ScriptFile.delete()


  scriptData.foreach(insert =>
    Files.write(Paths.get("C:\\Users\\USUARIO\\Desktop\\vehiculos_insert.sql"), insert.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND))

}
