package ProyectoIntegrador

import com.github.tototoshi.csv._

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}

object GeneracionInserts extends App{
  val reader = CSVReader.open(new File("C:\\Users\\USUARIO\\Downloads\\enemdu_vivienda_hogar_2023_I_trimestre.csv"))(new DefaultCSVFormat {
    override val delimiter = ';'
  })
  val data = reader.allWithHeaders()
  reader.close()

  def escapeMysql(text: String): String = text
    .replaceAll("\\\\", "\\\\\\\\")
    .replaceAll("\b", "\\\\b")
    .replaceAll("\n", "\\\\n")
    .replaceAll("\r", "\\\\r")
    .replaceAll("\t", "\\\\t")
    .replaceAll("\\x1A", "\\\\Z")
    .replaceAll("\\x00", "\\\\0")
    .replaceAll("'", "\\\\'")
    .replaceAll("\"", "\\\\\"")

  case class Vivienda(
                     id_Vivienda: Long,
                     fexp: Float,
                     upm: Long,
                     incluye_luz: String,
                     periodo: Int,
                     conglomerado: Int
                     )

  val viviendaData = data
    .map(row => Vivienda(
      row("id_vivienda").toLong,
      row("fexp").replaceAll(",", ".").toFloat,
      row("upm").toLong,
      row("vi12") match {
        case "1" => "Empresa eléctrica pública"
        case "2" => "Planta eléctrica privada"
        case "3" => "Vela, candil, mechero, gas"
        case "4" => "Ninguno"
      },
      row("periodo").toInt,
      row("conglomerado").toInt
    )
    )

  val SQL_PATTERN =
    """
      |INSERT INTO viviendas.vivienda (idvivienda, fexp, upm, incluye_luz, periodo, Conglomerado_idConglomerado)
      |VALUES
      |(%d, %f, %d, "%s", %d, %d);
      |""".stripMargin

  val scriptData = viviendaData
    .map(vivienda => SQL_PATTERN.formatLocal(java.util.Locale.US,
      vivienda.id_Vivienda,
      vivienda.fexp,
      vivienda.upm,
      escapeMysql(vivienda.incluye_luz),
      vivienda.periodo,
      vivienda.conglomerado))

  val ScriptFile = new File("C:\\Users\\USUARIO\\Desktop\\vivienda_insert.sql")
  if (ScriptFile.exists()) ScriptFile.delete()

  scriptData.foreach(insert =>
    Files.write(Paths.get("C:\\Users\\USUARIO\\Desktop\\vivienda_insert.sql"), insert.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.APPEND))

}
